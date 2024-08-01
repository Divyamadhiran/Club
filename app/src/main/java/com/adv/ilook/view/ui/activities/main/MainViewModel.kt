package com.adv.ilook.view.ui.activities.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction.EndToEnd.decrypt
import com.adv.ilook.view.base.BasicFunction.EndToEnd.encrypt
import com.adv.ilook.view.base.BasicFunction.EndToEnd.generateKeyPair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyPair
import java.security.PrivateKey
import javax.inject.Inject

private const val TAG = "==>>MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {
    private var keyPair: KeyPair? = null
    private val _usbDeviceDetected = MutableLiveData<Boolean>()
    val usbDeviceDetected: LiveData<Boolean> get() = _usbDeviceDetected

    private val _privateKeyLiveData = MutableLiveData<PrivateKey>()
    val privateKeyLiveData: LiveData<PrivateKey> get() = _privateKeyLiveData
    private val _pushEncryptTokenLiveData = MutableLiveData<String>()
    val pushEncryptTokenLiveData: LiveData<String> get() = _pushEncryptTokenLiveData

    private val _pushTokenLiveData = MutableLiveData<String>()
    val pushTokenLiveData: LiveData<String> get() = _pushTokenLiveData
    fun updateUsbDeviceDetected(detected: Boolean) {
        Log.d(TAG, "updateUsbDeviceDetected: ")
        _usbDeviceDetected.postValue(detected)
    }

    fun tokenEncrypt(token: String) {

        viewModelScope.launch(Dispatchers.IO) {
            keyPair = generateKeyPair()
            val publicKey = keyPair?.public
            val privateKey = keyPair?.private
            // Encrypt the token
            val encryptedToken =encrypt(token, publicKey!!)
            withContext(Dispatchers.Main){
                postOriginalToken(token)
                postEncryptToken(encryptedToken)
                postPrivateKey(privateKey!!)
            }

        }
    }

    fun getKeyPair():KeyPair{
        return keyPair!!
    }

    fun postEncryptToken(token: String){
        Log.d(TAG, "postEncryptToken(): encryptToken = $token")
        _pushEncryptTokenLiveData.postValue(token)
    }

    fun postOriginalToken(token: String){
        Log.d(TAG, "postOriginalToken(): originalToken = $token")
        _pushTokenLiveData.postValue(token)
    }

    fun postPrivateKey(privateKey: PrivateKey){
        Log.d(TAG, "postPrivateKey(): privateKey = $privateKey")
        _privateKeyLiveData.postValue(privateKey)
    }
}