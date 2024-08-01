package com.adv.ilook.di

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.Worker
import com.adv.ilook.model.util.worker.BaseSeeForMeWorker
import com.adv.ilook.model.util.worker.ChildWorkerFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class WorkerKey(val value: KClass<out CoroutineWorker>)
@Module
@InstallIn(SingletonComponent::class)
abstract class MyWorkerModule {


  /*  @Binds
    @IntoMap
    @WorkerKey(BaseSeeForMeWorker::class)
    internal abstract fun bindMyWorker(worker: BaseSeeForMeWorker): Worker*/

    @Binds
    @IntoMap
    @WorkerKey(BaseSeeForMeWorker::class)
    internal abstract fun bindMyWorkerFactory(worker: BaseSeeForMeWorker.Factory): ChildWorkerFactory
}



