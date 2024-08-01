package com.adv.ilook.view.base


import ContactListAdapter
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.adv.ilook.MyApp
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.Workflow
import com.adv.ilook.model.db.remote.firebase.remoteconfig.RemoteConfig
import com.adv.ilook.model.db.remote.repository.apprepo.UsbRepository
import com.adv.ilook.model.util.assets.FileKeys.workflow_file_name

import com.adv.ilook.model.util.assets.IPref
import com.adv.ilook.model.util.assets.RemoteConfigMode
import com.adv.ilook.model.util.assets.RemoteConfigMode.REMOTE_ENABLE
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_ADV_WORKFLOW
import com.adv.ilook.model.util.assets.WORK_TYPES
import com.adv.ilook.model.util.assets.WorkManagerKeys
import com.adv.ilook.model.util.extension.customNotification
import com.adv.ilook.model.util.extension.launchOnIO
import com.adv.ilook.model.util.extension.launchOnMain
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.model.util.worker.BaseSeeForMeWorker
import com.adv.ilook.view.ui.fragments.dataclasses.ContactList
import com.adv.ilook.view.ui.fragments.splash.TypeOfData
import com.google.firebase.database.core.Constants
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.security.PrivateKey
import java.time.Duration
import java.time.LocalDateTime
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "==>>BaseViewModel"

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val application: Application = MyApp(),
    private val workManager: WorkManager = WorkManager.getInstance(application),

) : ViewModel() {
    val _pushTokenLiveData = MutableLiveData<String>()
    val pushTokenLiveData: LiveData<String> get() = _pushTokenLiveData
    val _privateKeyLiveData = MutableLiveData<PrivateKey>()
    val privateKeyLiveData: LiveData<PrivateKey> get() = _privateKeyLiveData

    val _speechCompletedLiveData = MutableLiveData<String>()
    var speechCompletedLiveData: LiveData<String> = _speechCompletedLiveData

    private val _resultLiveData = MutableLiveData<Pair<Boolean, Data>>()
    val resultLiveData: LiveData<Pair<Boolean, Data>> get() = _resultLiveData

    val actionLiveData = MutableLiveData<String>()

    val _addContactList = MutableLiveData<List<ContactList>>()
    val addContactList: LiveData<List<ContactList>> get() = _addContactList
    val _contactListAdapter = MutableLiveData<ContactListAdapter>()
    val contactListAdapter: LiveData<ContactListAdapter> get() = _contactListAdapter

    @Inject
    lateinit var basicFunction: BasicFunction

    @Inject
    lateinit var usbRepository: UsbRepository

    @Inject
    open lateinit var sharedPref: IPref

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var jsonObj: JSONObject

    @Inject
    lateinit var jsonArray: JSONArray
    lateinit var owner: LifecycleOwner
    lateinit var workflow: Workflow
    private var jsonString: String = ""
    var jsonStringFromAsset: String = ""
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    @Inject
    lateinit var remoteConfig: RemoteConfig

    private val _workStatus = MutableLiveData<WorkInfo>()
    val workStatus: LiveData<WorkInfo> get() = _workStatus
    open fun callOtherActivity(activity: Activity, msg: String) {

    }

    open suspend fun init(function: (TypeOfData) -> Unit) {}
    fun fetchData(data: Pair<Boolean, Data>) {
        // Simulate fetching data and post value
        Log.d(TAG, "fetchData: --------------${data.second}")

        _resultLiveData.postValue(data)


    }

    inline fun postIfEnabledAndNotEmpty(
        enable: Boolean?,
        text: String?,
        postAction: (String) -> Unit
    ) {
        if (enable == true && !text.isNullOrEmpty()) {
            postAction(text)
        }
    }

    private fun startWorker(workRequest: WorkRequest) {
        workManager.enqueue(workRequest)
        // Observe the work status
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observeForever { workInfo ->
                viewModelScope.launch(Dispatchers.Main) {
                    val outputData = workInfo.outputData
                    Log.d(TAG, "startWorker: ---------status =${workInfo.state} : outputData = $outputData")

                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {

                        val workType = outputData.getString("workType")
                        val resultJson = outputData.getString("jsonString")
                        val resultBoolean = outputData.getBoolean("booleanValue", false)
                        // Handle the results
                        Log.d(
                            TAG,
                            "configureWorkflow: workType = $workType : data = $resultJson : result = $resultBoolean"
                        )
                        if (workType == WORK_TYPES.WORKFLOW_LOCAL.name || workType == WORK_TYPES.WORKFLOW_REMOTE.name) {
                            withContext(Dispatchers.IO) {
                                remoteWorkerJsonConfig(resultJson)
                            }
                        }
                        if (workType == WORK_TYPES.NOTIFY.name) {
                            val bMap = withContext(Dispatchers.IO) {
                                BitmapFactory.decodeStream(
                                    URL(
                                        Uri.parse("https://firebasestorage.googleapis.com/v0/b/seeforme-server-function.appspot.com/o/seeforme_images%2Fseeforme_notify%2Fmissed_call.png?alt=media&token=12d04360-6137-4ee1-91c5-878bcf75634d")
                                            .toString()
                                    ).openStream()
                                )
                            }
                            launch(Dispatchers.Main) {
                                var messageBody: String = ""
                                messageBody = if (resultBoolean) {
                                    "Software updates available"
                                } else {
                                    "Unable to connect to the server,Check your connection and try again..."
                                }
                                application.customNotification(
                                    messageBody,
                                    "SeeForMe",
                                    Uri.parse("https://firebasestorage.googleapis.com/v0/b/seeforme-server-function.appspot.com/o/seeforme_images%2Fseeforme_notify%2Fmissed_call.png?alt=media&token=12d04360-6137-4ee1-91c5-878bcf75634d"),
                                    bMap, networkHelper.isNetworkConnected()
                                )
                            }

                        }
                    }
                    _workStatus.postValue(workInfo)
                   // _resultLiveData.postValue(Pair(true,outputData))
                }
            }
    }

     fun remoteWorkerJsonConfig(resultJson: String?) {
        if (resultJson != null) {
            jsonString = basicFunction.readJsonFromFile(resultJson)
            // sharedPref.clear()
            Log.d(TAG, "remoteWorkerJsonConfig:  jsonString ==>$jsonString ")
            // sharedPref.put(key = APP_ADV_WORKFLOW, jsonString)
            getWorkflowPojo(jsonString) { data ->

            }

        }
    }


    fun scheduleWorkflowAtOnce(callFromActivity: Boolean) {
        Log.d(TAG, "scheduleWorkflowAtOnce: ==============>>>>>>callFromActivity =$callFromActivity")
        viewModelScope.launch {
            val inputData = workDataOf(
                WorkManagerKeys.FILE_NAME_KEY to workflow_file_name,
                WorkManagerKeys.SHARED_PREF_KEY to APP_ADV_WORKFLOW,
                WorkManagerKeys.REMOTE_CONFIG_KEY to RemoteConfigMode.REMOTE_ENABLE,
                WorkManagerKeys.CALL_ACTIVITY_KEY to callFromActivity
            )
            val workRequest = OneTimeWorkRequestBuilder<BaseSeeForMeWorker>()
                .setInputData(inputData)
                .build()
            startWorker(workRequest)
        }
    }

    fun scheduleWorkflowAPeriod(detected: Boolean) {
        val workRequest = PeriodicWorkRequestBuilder<BaseSeeForMeWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(30, TimeUnit.SECONDS) // Optional initial delay
            .build()
        startWorker(workRequest)
    }

    suspend fun configureWorkflow(
        context: Context,
        fileName: String = "workflow.json",
        key: String = APP_ADV_WORKFLOW,
        remote: Boolean = false,
        callFromActivity: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!remote)
                jsonString = basicFunction.getFileFromAsset(fileName, context)
            else
                basicFunction.getFileFromRemoteConfig(
                    context,
                    callFromActivity,
                    notifyCallback = { result, data ->
                        scope.launch {
                            var messageBody: String = ""
                            if (result) {
                                messageBody = "Software updates available"
                            } else {
                                messageBody =
                                    "Unable to connect to the server,Check your connection and try again..."
                            }
                            context.customNotification(
                                messageBody,
                                "SeeForMe",
                                imgUrl = Uri.parse("https://firebasestorage.googleapis.com/v0/b/seeforme-server-function.appspot.com/o/seeforme_images%2Fseeforme_notify%2Fmissed_call.png?alt=media&token=12d04360-6137-4ee1-91c5-878bcf75634d"),
                                null, networkHelper.isNetworkConnected()
                            )
                        }
                    },
                    callback = { result, data ->
                        jsonString = if (result) {
                            data
                        } else {

                            Log.d(TAG, "configureWorkflow: fileName =$fileName ")
                            basicFunction.getFileFromAsset(fileName, context)
                        }
                        Log.d(TAG, "configureWorkflow: data = $jsonString : result = $result")
                        sharedPref.clear()
                        sharedPref.put(key = key, jsonString)
                        when (key) {
                            APP_ADV_WORKFLOW -> {
                                Log.d(TAG, "configureWorkflow: APP_ADV_WORKFLOW ")
                                // launch(Dispatchers.IO) { getWorkflowPojo(jsonString) {} }
                            }

                            else -> {
                                // getPojoFromStrJson(jsonString, key) {}
                            }
                        }
                    })
        }
    }

    fun callWorkManager() {
        viewModelScope.launch() {
            usbRepository
        }
    }

    suspend fun getWorkflowFromJson(
        jsonStr: String = "",
        key: String = APP_ADV_WORKFLOW,
        function: (Workflow) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            var tempString = ""
            if (jsonStr.isNotEmpty()) {
                tempString = jsonStr
            } else {
                jsonString = sharedPref.str(key)
                tempString = jsonString
                // Log.i(TAG, "getWorkflowFromJson:  $jsonString")

            }

            //  launch(Dispatchers.IO) {
            getWorkflowPojo(tempString) {
                workflow = it
                function(it)
            }
            //  }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    private fun getWorkflowPojo(jsonString: String, function: (Workflow) -> Unit) {
        try {
            workflow = gson.fromJson(jsonString, Workflow::class.java)
            function(workflow)
        } catch (e: Exception) {
            Log.d(TAG, "getWorkflowPojo: ERROR =>  ${e.message}")
        }

    }

    private var locale: Int = 0
    private lateinit var localeSpeech: Locale
    private lateinit var mediaPlayer: MediaPlayer
    private var currentSongIndex = 0

    private val button_tones = listOf(
        R.raw.button_metallic_sound,
        R.raw.phone_key_sound,
        R.raw.notification_sound
    )

    fun initializeMediaPlayer(context: Context, currentSongIndex: Int) {
        this.currentSongIndex = currentSongIndex
        mediaPlayer = MediaPlayer.create(context, button_tones[currentSongIndex])
        mediaPlayer.setOnCompletionListener {

            mediaPlayer.reset()
            initializeMediaPlayer(context, this.currentSongIndex)
            mediaPlayer.start()
        }
    }

    private fun playSong() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    private fun stopSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        mediaPlayer.release()
    }


    fun sendTextForSpeech(
        tts: TextToSpeech,
        text: String,
        volume: Float = 0.9f,
        pan: Float = 0.0f,
        stream: Float = 1.0f,
        pitch: Float = 1.0f,
        rate: Float = 1.1f,
        language: String = "en",
        country: String = "US"
    ) {
        localeSpeech = Locale(language, country)
        locale = if (tts.isLanguageAvailable(localeSpeech) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(localeSpeech)
        } else {
            tts.setLanguage(Locale.US)
        }
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume) // Set volume (0.0 to 1.0)
            putFloat(TextToSpeech.Engine.KEY_PARAM_PAN, pan) // Set pan (-1.0 to 1.0)
            putFloat(TextToSpeech.Engine.KEY_PARAM_STREAM, stream)
        }
        tts.setPitch(pitch) // Set pitch (default is 1.0)
        tts.setSpeechRate(rate) // Set speech rate (default is 1.0)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "")

    }

    fun onInitSpeech(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            if (locale == TextToSpeech.LANG_MISSING_DATA || locale == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "The Language not supported!")
                _speechCompletedLiveData.value = "failed"
            } else {
                _speechCompletedLiveData.value = "completed"
            }
        }
    }

    fun onDestroySpeech(tts: TextToSpeech) {
        tts.stop()
        tts.shutdown()
    }

    fun pushNotificationToken(token: String?) {
        Log.d(TAG, "pushNotificationToken: $token")
        _pushTokenLiveData.postValue(token.toString())
    }

    fun privateToken(privateKey: PrivateKey) {
        Log.d(TAG, "privateToken: $privateKey")
        _privateKeyLiveData.postValue(privateKey)
    }

    fun <T> thisTaskTakenTime(task: () -> T): Pair<T, Long> {
        val startTime = LocalDateTime.now()
        val result = task()
        val endTime = LocalDateTime.now()

        val duration = Duration.between(startTime, endTime).toMillis()
        return Pair(result, duration)
    }
}


class BaseViewModelFactory(
    private val networkHelper: NetworkHelper,

    private val application: Application,
    private val workManager: WorkManager

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BaseViewModel(networkHelper, application, workManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}