package com.adv.ilook.model.db.local.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class LocalDataSource( private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): CommonDataSource {
    suspend fun loginData(){

    }
}

class RemoteDataSource( private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): CommonDataSource {
    suspend fun loginData(){

    }
}