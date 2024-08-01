package com.adv.ilook.model.util.service.appservice

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

import javax.inject.Inject
private const val TAG = "==>>MainServiceRepository"
class MainServiceRepository @Inject constructor(private val context: Context) {
    fun startService(serviceName: String) {
        Thread{
            val intent = Intent(context, MainService::class.java)
            intent.action = serviceName
            startServiceIntent(intent)
        }.start()
    }

    fun startMediaProjectionService(serviceName: String) {
      // coroutineScope {  }

            // Send the result to the service to handle media projection
            val serviceIntent = Intent(context, MainService::class.java).apply {
                action = serviceName
                putExtra("resultCode", RESULT_OK)
                putExtra("data", data)
            }
           // ContextCompat.startForegroundService(context, serviceIntent)
        startServiceIntent(serviceIntent)

    }

    private fun startServiceIntent(intent: Intent) {
      try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              context.startForegroundService(intent)
          } else {
              context.startService(intent)
          }
      } catch (e:Exception) {
          Log.e(TAG, "startServiceIntent: error ${e.message}")

      }
    }

    fun stopService() {
        Log.d(TAG, "stopService: ")
       // Thread{
            val intent = Intent(context, MainService::class.java)
            intent.action = MainServiceActions.STOP_SERVICE.name
            startServiceIntent(intent)
      //  }.start()
    }

}