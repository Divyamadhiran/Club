package com.adv.ilook.model.util.worker

import android.content.Context
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.adv.ilook.model.db.remote.firebase.remoteconfig.RemoteConfig
import com.adv.ilook.model.util.assets.IPref
import com.adv.ilook.model.util.assets.PrefImpl
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_ADV_WORKFLOW
import com.adv.ilook.model.util.assets.WORK_TYPES
import com.adv.ilook.model.util.assets.WorkManagerKeys
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.fragments.homescreen.HomeScreenViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.work.WorkManager
import com.adv.ilook.MyApp
import com.adv.ilook.view.base.BaseViewModelFactory
import kotlinx.coroutines.coroutineScope
import java.io.File

private const val TAG = "==>>BaseSeeForMeWorker"

@HiltWorker
open class BaseSeeForMeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val remoteConfig: RemoteConfig,
    private val networkService: NetworkHelper,
    private val basicFunction: BasicFunction,
    private var sharedPref: PrefImpl
) : CoroutineWorker(context, params) {
    // Get the ViewModel or Repository instance
    var myViewModel: BaseViewModel? =
        null/*ViewModelProvider.NewInstanceFactory().create(BaseViewModel::class.java)*/
    private var jsonString = ""

    override suspend fun doWork(): Result {
        val viewModelFactory = BaseViewModelFactory(
            networkService, application = MyApp(), WorkManager.getInstance(applicationContext)
        )
        myViewModel =
            ViewModelProvider(ViewModelStore(), viewModelFactory).get(BaseViewModel::class.java)

        /*    withContext(Dispatchers.Main){
                myViewModel?.resultLiveData?.observeForever {
                    Log.d(TAG, "doWork: --------------->>>>>>>>>>>>${it.second}")

                }
            }*/

        return withContext(Dispatchers.IO) {
            try {
                val fileName = inputData.getString(WorkManagerKeys.FILE_NAME_KEY).toString()
                val sharedPrefKey = inputData.getString(WorkManagerKeys.SHARED_PREF_KEY).toString()
                val isRemoteConfig = inputData.getBoolean(WorkManagerKeys.REMOTE_CONFIG_KEY, false)
                Log.d(TAG, "doWork: isRemoteConfig: $isRemoteConfig")
                val isCallFromActivity =
                    inputData.getBoolean(WorkManagerKeys.CALL_ACTIVITY_KEY, false)

                val (result, data) = fetchNetworkData(
                    isRemoteConfig, sharedPrefKey, fileName, context, isCallFromActivity
                )
                Result.success(data)

            } catch (e: Exception) {
                Log.e(TAG, "Error in doWork", e)
                Result.failure(createOutputData(error = e.message))
            }
        }
    }

    private suspend fun fetchNetworkData(
        isRemoteConfig: Boolean,
        sharedPrefKey: String,
        fileName: String,
        appContext: Context,
        isCallFromActivity: Boolean
    ): Pair<Boolean, Data> {
        Log.d(
            TAG,
            "fetchNetworkData() called with: isRemoteConfig = $isRemoteConfig, sharedPrefKey = $sharedPrefKey, fileName = $fileName, appContext = $appContext, isCallFromActivity = $isCallFromActivity"
        )
        if (!isRemoteConfig) {

            val task = suspendCancellableCoroutine { continuation ->
                try {
                    jsonString = basicFunction.getFileFromAsset(fileName, appContext)
                    val filePath = basicFunction.saveJsonToFile(appContext, jsonString, fileName)
                    sharedPref.clear()
                    sharedPref.put(key = sharedPrefKey, jsonString)
                    if (continuation.isActive) {
                        continuation.resume(
                            true to createOutputData(
                                workType = WORK_TYPES.WORKFLOW_LOCAL,
                                jsonString = filePath,
                                booleanValue = true
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "fetchNetworkData: Error==>> ${e.message}")
                    continuation.resume(
                        false to createOutputData(
                            workType = WORK_TYPES.ERROR,
                            jsonString = "${e.message}",
                            booleanValue = true
                        )
                    )
                }
            }

            return task

        } else {/* val deferredResult = CompletableDeferred<Pair<Boolean, String>>()

             try {
                 basicFunction.getFileFromRemoteConfig(appContext, isCallFromActivity,
                     notifyCallback = { result, data ->
                         if (!deferredResult.isCompleted) {
                             deferredResult.complete(result to data)
                         }
                     },
                     callback = { result, data ->
                         if (!deferredResult.isCompleted) {
                             deferredResult.complete(result to data)
                         }
                     })
             } catch (e: Exception) {
                 if (!deferredResult.isCompleted) {
                     deferredResult.complete(false to "${e.message}")
                 }
             }

             return deferredResult.await()*/


            val task = suspendCancellableCoroutine { continuation ->
                try {
                    basicFunction.getFileFromRemoteConfig(appContext,
                        isCallFromActivity,
                            notifyCallback = { result, data ->
                            Log.d(
                                TAG,
                                "fetchNetworkData: notifyCallback ===> result: ${result}, data: ${data}"
                            )
                            if (data.isNotEmpty() && data.isNotBlank()) {
                                jsonString = data
                                Log.d(
                                    TAG,
                                    "fetchNetworkData: notifyCallback ===> result: ${result}, data: ${data}"
                                )
                                sharedPref.clear()
                                sharedPref.put(key = sharedPrefKey, jsonString)
                                val filePath =
                                    basicFunction.saveJsonToFile(appContext, jsonString, fileName)
                                if (continuation.isActive) {
                                    Log.d(
                                        TAG,
                                        "fetchNetworkData: if continuation is isActive ===>Notification sending"
                                    )
                                    continuation.resume(
                                        true to createOutputData(
                                            workType = WORK_TYPES.NOTIFY,
                                            jsonString = filePath,
                                            booleanValue = result
                                        )
                                    )

                                } else {

                                    Log.d(TAG, "fetchNetworkData: Else ===>Notification sending")
                                    val pair = Pair(
                                        true, createOutputData(
                                            workType = WORK_TYPES.NOTIFY,
                                            jsonString = filePath,
                                            booleanValue = result
                                        )
                                    )
                                    myViewModel?.fetchData(pair)

                                }
                            } else {
                                Log.d(
                                    TAG,
                                    "fetchNetworkData: notifyCallback ===>> empty response and result =${result}"
                                )
                                val pair = Pair(
                                    true, createOutputData(
                                        workType = WORK_TYPES.ERROR,
                                        jsonString = File(context.filesDir, fileName).absolutePath,
                                        booleanValue = result
                                    )
                                )
                                myViewModel?.fetchData(pair)
                            }
                        },
                        callback = { result, data ->
                            Log.d(
                                TAG,
                                "fetchNetworkData: callback ===> result: ${result}, data: ${data}"
                            )
                            if (data.isNotEmpty() && data.isNotBlank()) {
                                jsonString = data
                                sharedPref.clear()
                                sharedPref.put(key = sharedPrefKey, jsonString)
                                val filePath =
                                    basicFunction.saveJsonToFile(appContext, jsonString, fileName)
                                if (continuation.isActive) {
                                    continuation.resume(
                                        true to createOutputData(
                                            workType = WORK_TYPES.WORKFLOW_REMOTE,
                                            jsonString = filePath,
                                            booleanValue = result
                                        )
                                    )
                                } else {
                                    val pair = Pair(
                                        true, createOutputData(
                                            workType = WORK_TYPES.ERROR, jsonString = File(
                                                context.filesDir, fileName
                                            ).absolutePath, booleanValue = result
                                        )
                                    )
                                    myViewModel?.fetchData(pair)
                                }
                            } else {
                                val pair = Pair(
                                    true, createOutputData(
                                        workType = WORK_TYPES.ERROR,
                                        jsonString = File(context.filesDir, fileName).absolutePath,
                                        booleanValue = result
                                    )
                                )
                                myViewModel?.fetchData(pair)
                            }

                        })
                } catch (e: Exception) {
                    // continuation.resumeWithException(e)
                    Log.d(TAG, "fetchNetworkData: callback ===> Error: ${e.message}")
                    continuation.resume(
                        false to createOutputData(
                            workType = WORK_TYPES.ERROR,
                            jsonString = File(context.filesDir, fileName).absolutePath,
                            booleanValue = false
                        )
                    )
                }
            }

            return task
        }
    }

    private fun createOutputData(
        workType: WORK_TYPES? = null,
        jsonString: String? = null,
        booleanValue: Boolean = false,
        error: String? = null
    ): Data {
        return Data.Builder().apply {
            workType?.let {
                putString("workType", it.name)
            }
            putString("jsonString", jsonString)
            putBoolean("booleanValue", booleanValue)
            putString("error", error.toString())
        }.build()
    }


    class Factory @Inject constructor(
        val remoteConfig: RemoteConfig,
        val networkService: NetworkHelper,
        val basicFunction: BasicFunction,
        var sharedPref: PrefImpl
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return BaseSeeForMeWorker(
                appContext, params, remoteConfig, networkService, basicFunction, sharedPref
            )
        }
    }
}
