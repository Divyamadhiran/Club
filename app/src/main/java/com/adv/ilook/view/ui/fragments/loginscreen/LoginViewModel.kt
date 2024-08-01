package com.adv.ilook.view.ui.fragments.loginscreen


import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.LoginScreen
import com.adv.ilook.model.data.workflow.PhoneItem
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.model.util.responsehelper.Resource

import com.adv.ilook.model.util.responsehelper.UiStatusLogin

import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction


import com.adv.ilook.view.ui.fragments.splash.TypeOfData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: SeeForMeRepo,
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
    private lateinit var addPhoneCountry: String

    // private lateinit var loginScreen: LoginScreen
    private var loginScreen by Delegates.notNull<LoginScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData
    private var testingPhone by Delegates.notNull<List<PhoneItem?>>()
    private val _loginFieldsEnabledFlow = MutableLiveData<Map<Int, Boolean>>()
    val loginFieldsEnabledFlow: LiveData<Map<Int, Boolean>> get() = _loginFieldsEnabledFlow


    override suspend fun init(function: (TypeOfData) -> Unit) {
        viewModelScope.launch {
            getWorkflowFromJson {
                loginScreen = it.screens?.loginScreen!!
                testingPhone = it.testUpdate?.phone!!
                Log.d(TAG, "init: prev ->${loginScreen?.previousScreen.toString()}")
                Log.d(TAG, "init: next ->${loginScreen?.nextScreen.toString()}")
                launch(Dispatchers.Main) {
                    _nextScreenLiveData.postValue(BasicFunction.getScreens()[loginScreen.nextScreen] as Int)
                    _prevScreenLiveData.postValue(BasicFunction.getScreens()[loginScreen.previousScreen] as Int)
                    function(TypeOfData.INT)
                }
            }
            withContext(Dispatchers.Main) {
                try {
                    updateViewElements()
                    loginFieldsEnabledDatas()
                } catch (e: Exception) {
                    Log.d(TAG, "init: ${e.message}")
                }

            }
        }
    }


    private fun updateViewElements() {
        loginScreen.views?.apply {
            postIfEnabledAndNotEmpty(textView?.header?.enable, textView?.header?.text) {
                _tv_login_header.postValue(it)
            }
            postIfEnabledAndNotEmpty(textView?.userName?.enable, textView?.userName?.text) {
                _tv_username.postValue(it)
            }
            postIfEnabledAndNotEmpty(textView?.mobileNumber?.enable, textView?.mobileNumber?.text) {
                _tv_phone_number.postValue(it)
            }
            postIfEnabledAndNotEmpty(
                toastView?.loginSuccess?.enable,
                toastView?.loginSuccess?.text
            ) {
                _validation_message.postValue(it)
            }
            postIfEnabledAndNotEmpty(textView?.userName?.enable, textView?.userName?.helperText) {
                _validation_name_error.postValue(it)
            }
            postIfEnabledAndNotEmpty(
                textView?.mobileNumber?.enable,
                textView?.mobileNumber?.helperText
            ) {
                _validation_phone_error.postValue(it)
            }
            postIfEnabledAndNotEmpty(buttonView?.login?.enable, buttonView?.login?.text) {
                _bt_login_text.postValue(it)
            }
            postIfEnabledAndNotEmpty(toastView?.loading?.enable, toastView?.loading?.text) {
                _tv_loading_text.postValue(it)
            }
        }
        _testingPhoneNumber.postValue(testingPhone)
    }

    suspend fun login(activity: Activity, username: String, phone: String) {
        //  loginFlow(username, phone)


        withContext(Dispatchers.Main) {

            _loginResult.postValue(Resource.loading(isLoading = true, "Please wait..."))
        }
        //  delay(1000)
        if (networkHelper.isNetworkConnected()) {
            if (isUserNameValid(username) && isPhoneNumberValid(phone)) {

                try {
                    withContext(Dispatchers.IO) {
                        /*     val result = loginRepository.login(username, addPhoneCountry) {
                                 _loginResult.postValue(Resource.success(it))
                             }*/
                        sendVerificationCode(activity, addPhoneCountry)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _loginResult.postValue(
                            Resource.loading(
                                isLoading = false,
                                "${e.message}..."
                            )
                        )
                        _loginResult.postValue(
                            Resource.error(
                                custom_message = R.string.login_failed,
                                null
                            )
                        )
                    }
                } finally {
                    // launch {  _loginResult.postValue(Resource.loading(isLoading = false,"Completed...")) }
                }

            } else {
                withContext(Dispatchers.Main) {
                    _loginResult.postValue(Resource.loading(isLoading = false, "Completed..."))
                }
                withContext(Dispatchers.Main) {
                    _loginResult.postValue(
                        Resource.error(
                            custom_message = R.string.login_failed,
                            null
                        )
                    )
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                _loginResult.postValue(Resource.loading(isLoading = false, "Completed..."))
                _loginResult.postValue(
                    Resource.error(
                        custom_message = R.string.network_error,
                        null
                    )
                )
            }
        }


    }

    suspend fun sendVerificationCode(activity: Activity, phone: String) {
        withContext(Dispatchers.Main) {
            launch(Dispatchers.IO) {
                loginRepository.sendVerificationCode(activity, phone, object : PhoneAuthProvider
                .OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Log.d("__TAG", "sendVerificationCode: Complete function")
                        launch { _verificationCompleted.value = credential }
                        _loginResult.postValue(Resource.loading(data = false))
                    }


                    override fun onVerificationFailed(e: FirebaseException) {
                        _verificationFailed.value = e.message
                        Log.e(TAG, "onVerificationFailed: Message ->${e.message}")
                        _loginResult.postValue(Resource.loading(data = false))
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        Log.d(
                            TAG,
                            "onCodeSent() called with: verificationId = $verificationId, token = $token"
                        )
                        launch {
                            loginRepository.setVerificationIdAndToken(verificationId, token)
                        }
                        loginRepository.setVerificationIdAndToken(verificationId, token)
                        _loginResult.postValue(Resource.loading(data = false))
                        _codeSent.value = true
                    }
                })
            }

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

    private fun loginFieldsEnabledDatas() {
        val loginFields = mutableMapOf<Int, Boolean>()
        loginScreen.views?.let { views ->
            views.textView?.header?.enable?.let { enable ->
                loginFields[R.id.loginText] = enable
            }
            views.buttonView?.login?.enable?.let { enable ->
                loginFields[R.id.generate_otp_button] = enable
            }
            views.textView?.bodyText?.enable?.let { enable ->
                loginFields[R.id.loginText] = enable
            }
        }
        _loginFieldsEnabledFlow.postValue(loginFields)
        Log.d(TAG, "loginFieldsEnabledData: $loginFields")
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

    var i = 0
    fun loginDataChange(username: String, phone: String) {


        if (!isUserNameValid(username)) {
            _loginForm.postValue(UiStatusLogin.LoginFormState(usernameError = validation_name_error.value))
        } else if (!isPhoneNumberValid(phone)) {
            Log.d(TAG, "loginDataChange: ElseIf-${phone.length}")
            _loginForm.postValue(UiStatusLogin.LoginFormState(phoneError = validation_phone_error.value))
        } else {
            Log.d(TAG, "loginDataChange:Else- ${phone.length}")
            _loginForm.postValue(UiStatusLogin.LoginFormState(message = validation_message.value))
            _loginForm.postValue(UiStatusLogin.LoginFormState(isDataValid = true))

        }
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        Log.d(TAG, "isPhoneNumberValid: ${phone.length}")
        if (phone.length != 10) {
            return false
        } else {
            addPhoneCountry = "+91$phone"
            Log.d(TAG, "isPhoneNumberValid: ${Patterns.PHONE.matcher(addPhoneCountry).matches()}")
            return Patterns.PHONE.matcher(addPhoneCountry).matches()
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.trim().length > 3
    }

    override fun callOtherActivity(activity: Activity, msg: String) {

    }


    private val _loginForm = MutableLiveData<UiStatusLogin>()
    val loginFormState: LiveData<UiStatusLogin> = _loginForm

    private val _loginResult = MutableLiveData<Resource<Any>>()
    val loginResult: LiveData<Resource<Any>> = _loginResult

    private val _codeSent = MutableLiveData<Boolean>()
    val codeSent: LiveData<Boolean> = _codeSent

    private val _verificationCompleted = MutableLiveData<PhoneAuthCredential>()
    val verificationCompleted: LiveData<PhoneAuthCredential> = _verificationCompleted

    private val _verificationFailed = MutableLiveData<String>()
    val verificationFailed: LiveData<String> = _verificationFailed

    private val _signInResult = MutableLiveData<Boolean>()
    val signInResult: LiveData<Boolean> = _signInResult

    val username = MutableLiveData<String>()


    private val _tv_login_header = MutableLiveData<String>()
    var tv_login_header: LiveData<String> = _tv_login_header
    private val _tv_username = MutableLiveData<String>()
    var tv_username: LiveData<String> = _tv_username
    private val _tv_phone_number = MutableLiveData<String>()
    var tv_phone_number: LiveData<String> = _tv_phone_number
    private val _tv_loading_text = MutableLiveData<String>()
    var tv_loading_text: LiveData<String> = _tv_loading_text


    private val _validation_message = MutableLiveData<String>()
    var validation_message: LiveData<String> = _validation_message
    private val _validation_phone_error = MutableLiveData<String>()
    var validation_phone_error: LiveData<String> = _validation_phone_error
    private val _validation_name_error = MutableLiveData<String>()
    var validation_name_error: LiveData<String> = _validation_name_error


    private val _bt_login_text = MutableLiveData<String>()
    var bt_login_text: LiveData<String> = _bt_login_text
    private val _testingPhoneNumber = MutableLiveData<List<PhoneItem?>>()
    val testingPhoneNumber: LiveData<List<PhoneItem?>> get() = _testingPhoneNumber

}