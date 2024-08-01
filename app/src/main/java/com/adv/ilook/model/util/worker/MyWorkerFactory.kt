package com.adv.ilook.model.util.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class MyWorkerFactory @Inject constructor(
    val workers: Map<Class<out CoroutineWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val foundEntry = workers.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
        val provider = foundEntry?.value

        return provider?.get()?.create(appContext,workerParameters)
    }
}


interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): CoroutineWorker
}