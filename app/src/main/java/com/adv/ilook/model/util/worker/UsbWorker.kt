package com.adv.ilook.model.util.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.adv.ilook.view.ui.activities.main.MainActivity

private const val TAG = "UsbWorker"
class UsbWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Log.d("UsbWorker", "doWork: ")
       // openApp()
       return Result.success()
    }

}