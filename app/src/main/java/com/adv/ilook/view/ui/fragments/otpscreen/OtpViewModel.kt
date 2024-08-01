package com.adv.ilook.view.ui.fragments.otpscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.OtpScreen
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.util.assets.PrefImpl
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_USERLOGIN
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_USERNAME
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_USERPHONE
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_USERSTATUS
import com.adv.ilook.model.util.assets.UserStatus
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.model.util.responsehelper.Resource
import com.adv.ilook.model.util.responsehelper.UiStatusOtp
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction

import com.adv.ilook.view.ui.fragments.splash.TypeOfData
import com.google.firebase.auth.PhoneAuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>OtpViewModel"

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val loginRepository: SeeForMeRepo,
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
    @Inject
    lateinit var sharedPreference: PrefImpl
    private var otpScreen by Delegates.notNull<OtpScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData

    private val _otpEnableFieldsFlow=MutableLiveData<Map<Int,Boolean>>()
    val otpEnableFieldsFlow:LiveData<Map<Int,Boolean>> get() = _otpEnableFieldsFlow


    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch {
            getWorkflowFromJson {
                otpScreen = it.screens?.otpScreen!!
                Log.d(TAG, "init: prev ->${otpScreen.previousScreen.toString()}")
                Log.d(TAG, "init: next ->${otpScreen.nextScreen.toString()}")
                launch(Dispatchers.Main) {
                    _nextScreenLiveData.postValue(BasicFunction.getScreens()[otpScreen.nextScreen.toString()] as Int)
                    _prevScreenLiveData.postValue(BasicFunction.getScreens()[otpScreen.previousScreen.toString()] as Int)
                }
            }

            launch(Dispatchers.Main) {
                updateViewElements()
            }
        }


    }




    private fun updateViewElements() {
        val otpFields= mapOf<Int,Boolean>()
        otpScreen.views?.apply {
            textView?.header?.let { head ->
                head.enable?.let { enable ->
                    head.text?.let { text ->
                        postIfEnabledAndNotEmpty(enable, text) {
                            _tv_otp_header.postValue(it)
                        }
                    }


                    head.helperText?.let { helperText ->
                        postIfEnabledAndNotEmpty(enable, helperText) {
                            _tv_otp_helper_text.postValue(it)
                        }
                    }
                }
            }


            textView?.otpCode?.let { otpCode ->
                otpCode.enable?.let { enable ->
                    otpCode.text?.let { text ->
                        postIfEnabledAndNotEmpty(enable, text) {
                            _et_otp_number_text.postValue(it)
                        }
                    }
                }
            }


            buttonView?.verifyOtp?.let { verifyOtp ->
                verifyOtp.enable?.let { enable ->
                    verifyOtp.text?.let { text ->
                        postIfEnabledAndNotEmpty(enable, text) {
                            _bt_login_text.postValue(it)
                        }
                    }
                }
            }


            toastView?.loading?.let { loading ->
                loading.enable?.let { enable ->
                    loading.text?.let { text ->
                        postIfEnabledAndNotEmpty(enable, text) {
                            _toast_load_message.postValue(it)
                        }
                    }
                }
            }


            toastView?.loginSuccess?.let { loginSuccess ->
                loginSuccess.enable?.let { enable ->
                    loginSuccess.text?.let { text ->
                        postIfEnabledAndNotEmpty(enable, text) {
                            _toast_success_message.postValue(it)
                        }
                    }
                }
            }


            toastView?.loginFailure?.let { loginFailure ->
                loginFailure.enable?.let { enable ->
                    loginFailure.text?.let { text ->
                        postIfEnabledAndNotEmpty(enable, text) {
                            _toast_failure_message.postValue(it)
                        }
                    }
                }
            }


            _bt_otp_enable.postValue(textView?.otpCode?.enable ?: false)
            _bt_login_enable.postValue(buttonView?.verifyOtp?.enable ?: false)


            otpFields[R.id.header_text] to textView?.header?.enable
            otpFields[R.id.sub_content_text] to textView?.bodyText?.enable
            otpFields[R.id.login_button] to buttonView?.verifyOtp?.enable
            //otpFields[R.id.loading_text] to textView?.bodyText?.enable
            _otpEnableFieldsFlow.postValue(otpFields)
        }


    }



    fun verifyCode(code: String) {
        try {
            val credential = loginRepository.verifyCode(code)
            signInWithCredential(credential)
        } catch (e: Exception) {
            _verificationFailed.value = e.message
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        loginRepository.signInWithCredential(credential) { user, exception ->
            if (user != null) {
                _signInResult.value = true
            } else {
                _signInResult.value = false
                exception?.message?.let { _verificationFailed.value = it }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun registeredUserData(userName: String, userPhone: String, tokenEncrypt: String) {
        _otpResult.postValue(Resource.loading(isLoading = true, data = "Please wait..."))
        withContext(Dispatchers.IO) {
            sharedPreference.put(APP_USERNAME, userName)
            sharedPreference.put(APP_USERPHONE, userPhone)
        }

        withContext(Dispatchers.IO) {
            val result = loginRepository.login(
                username = userName,
                phone = userPhone,
                status = UserStatus.ONLINE.name,
                isLogged = true,
                token = tokenEncrypt
            ) { isDone, message ->
                Log.d(TAG, "registeredUserData: $isDone, $message")
                if (isDone) {
                    launch {
                        sharedPreference.put(APP_USERSTATUS,UserStatus.ONLINE.name)
                        sharedPreference.put(APP_USERLOGIN, true)
                        _toast_success_message.postValue(message.toString())
                    }
                    launch {
                        _otpResult.postValue(
                            Resource.loading(
                                isLoading = false,
                                data = "Registered successfully"
                            )
                        )
                    }
                    launch { _otpResult.postValue(Resource.success(message.toString())) }


                } else {
                    Log.d(TAG, "registeredUserData:Else => $isDone, $message")
                    launch {
                        _otpResult.postValue(
                            Resource.error(
                                custom_message = "Registration failed",
                                data = message.toString()
                            )
                        )
                    }
                    launch {
                        _toast_failure_message.postValue(message.toString())

                    }
                    launch {
                        _otpResult.postValue(
                            Resource.loading(
                                isLoading = false,
                                data = "Registration failed"
                            )
                        )
                    }
                }

            }

            Log.d(TAG, "registeredUserData:result-> $result")
        }


    }


    //OTP Login
    private val _verificationCompleted = MutableLiveData<PhoneAuthCredential>()
    val verificationCompleted: LiveData<PhoneAuthCredential> = _verificationCompleted

    private val _verificationFailed = MutableLiveData<String>()
    val verificationFailed: LiveData<String> = _verificationFailed

    private val _signInResult = MutableLiveData<Boolean>()
    val signInResult: LiveData<Boolean> = _signInResult

    //UI Updates like progressbar,toast message,snackbar,buttons,views enable disable
    private val _otpForm = MutableLiveData<UiStatusOtp>()
    val otpForm: LiveData<UiStatusOtp> = _otpForm

    //AlertBox content,nextscreen,prevscreen
    private val _otpResult = MutableLiveData<Resource<Any>>()
    val otpResult: LiveData<Resource<Any>> = _otpResult

    private val _tv_otp_header = MutableLiveData<String>()
    val tv_otp_header: LiveData<String> = _tv_otp_header

    private val _tv_otp_helper_text = MutableLiveData<String>()
    val tv_otp_helper_text: LiveData<String> = _tv_otp_helper_text

    private val _et_otp_number_text = MutableLiveData<String>()
    val et_otp_number_text: LiveData<String> = _et_otp_number_text

    private val _bt_login_text = MutableLiveData<String>()
    val bt_login_text: LiveData<String> = _bt_login_text

    private val _bt_otp_enable = MutableLiveData<Boolean>()
    val bt_otp_enable: LiveData<Boolean> = _bt_otp_enable
    private val _bt_sim_validation_enable = MutableLiveData<Boolean>()
    val bt_sim_validation_enable: LiveData<Boolean> = _bt_sim_validation_enable

    private val _bt_login_enable = MutableLiveData<Boolean>()
    val bt_login_enable: LiveData<Boolean> = _bt_login_enable

    private val _toast_success_message = MutableLiveData<String>()
    var toast_success_message: LiveData<String> = _toast_success_message
    private val _toast_failure_message = MutableLiveData<String>()
    var toast_failure_message: LiveData<String> = _toast_failure_message
    private val _toast_load_message = MutableLiveData<String>()
    var toast_load_message: LiveData<String> = _toast_load_message
}