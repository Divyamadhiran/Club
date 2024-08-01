package com.adv.ilook.model.db.remote.repository.apprepo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adv.ilook.di.IoDispatcher
import com.adv.ilook.model.db.remote.firebase.firestore.FireStoreClient
import com.adv.ilook.model.db.remote.firebase.realtimedatabase.FirebaseClient
import kotlinx.coroutines.CoroutineDispatcher

class UsbRepository(
   private val firebaseClient: FirebaseClient,
   private val fireStoreClient: FireStoreClient,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val _usbDeviceDetected = MutableLiveData<Boolean>()
    val usbDeviceDetected: LiveData<Boolean> get() = _usbDeviceDetected

    fun setUsbDeviceDetected(detected: Boolean) {
        _usbDeviceDetected.value = detected
    }
}