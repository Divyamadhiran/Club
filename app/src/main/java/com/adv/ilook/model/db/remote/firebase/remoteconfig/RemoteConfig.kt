package com.adv.ilook.model.db.remote.firebase.remoteconfig

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigPictureStyle
import androidx.core.app.NotificationCompat.Builder
import androidx.work.ListenableWorker
import com.adv.ilook.R
import com.adv.ilook.model.util.extension.customNotification
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.activities.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessagingService.NOTIFICATION_SERVICE
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "==>>RemoteConfig"

class RemoteConfig @Inject constructor(private val remoteConfig: FirebaseRemoteConfig) {

    var jsonString = ""
     fun fetchAndActivate(context: Context?, callback: (Boolean, String) -> Unit): String {
         Log.d(TAG, "fetchAndActivate() called with: context = $context, callback = $callback")
        // Fetch remote config value
        remoteConfig.fetchAndActivate().addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                remoteConfig.activate()
                val updated = task.result
                // Use the fetched value
                jsonString = remoteConfig.getString("workflow_key")
                // Update UI with the fetched value
                Log.d(
                    TAG, "fetchAndActivate: updated =$updated ,Json Value = ${jsonString}"
                )
                callback(true, jsonString)
            } else {
                // Handle errors
                Log.d(TAG, "fetchAndActivate:  failed to fetch remote config ")
                callback(false, jsonString)
            }
        }

        return jsonString
    }



    fun remoteUpdateConfig(context: Context?, callback: (Boolean, String) -> Unit) {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "onUpdate: key " + configUpdate.updatedKeys)
                if (configUpdate.updatedKeys.contains("workflow_key")) {
                    remoteConfig.activate().addOnCompleteListener {
                        Log.d(TAG, "onUpdate: isSuccessful-> ${it.isSuccessful}")
                        if (it.isSuccessful) {
                            val updated = it.result
                            Log.d(
                                TAG, "remoteUpdateConfig: updated = $updated"
                            )
                            callback(updated, remoteConfig.getString("workflow_key"))
                        } else {
                            Log.d(
                                TAG, "remoteUpdateConfig: updated = false"
                            )
                             callback(false, remoteConfig.getString("workflow_key"))

                        }
                    }

                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.d(TAG, "onError: error: ${error.message}")
                callback(false, remoteConfig.getString("workflow_key"))


            }

        })

    }


    suspend fun fetchWorkflow(
        context: Context?,
        fetchCallback: (Boolean, String) -> Unit
    ): Pair<Boolean, String> =
        suspendCancellableCoroutine { continuation ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate()
                    val updated = task.result
                    val jsonString = remoteConfig.getString("workflow_key")
                    Log.d(TAG, "fetchAndActivate: updated = $updated , Json Value = $jsonString")
                    continuation.resume(Pair(true, jsonString))
                } else {
                    Log.d(TAG, "fetchAndActivate: failed to fetch remote config")
                    continuation.resume(
                        Pair(
                            false, """{
  "status_code": 400,
  "status": true,
  "message": "failed to fetch workflow"
}"""
                        )
                    )

                }
            }
        }

    suspend fun updateWorkflow(
        context: Context?,
        notifyCallback:  (Boolean, String) -> Unit
    ): Pair<Boolean, String> =
        suspendCancellableCoroutine { continuation ->
            remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    if (configUpdate.updatedKeys.contains("workflow_key")) {
                        remoteConfig.activate().addOnCompleteListener { task ->
                            val updated = task.isSuccessful
                            val jsonString = remoteConfig.getString("workflow_key")
                            continuation.resume(Pair(updated, jsonString))
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    continuation.resume(
                        Pair(
                            false, """{
  "status_code": 400,
  "status": true,
  "message": "${error.message}"
}"""
                        )
                    )
                }
            })
        }




}