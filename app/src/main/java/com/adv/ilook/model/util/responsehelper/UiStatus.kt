package com.adv.ilook.model.util.responsehelper

import org.checkerframework.checker.guieffect.qual.UI

sealed class UiStatusLogin {
/*    data object Idle : UiStatus()
    data object Error : UiStatus()
    data object Loading : UiStatus()
    data object Success : UiStatus()*/
    data class LoginFormState(
        val usernameError: Any? = null,
        val isNameError: Boolean = false,
        val phoneError: Any? = null,
        val isPhoneError: Boolean = false,
        val message: Any? = null,
        val isDataValid: Boolean = false
    ) : UiStatusLogin()



    override fun toString(): String {
        return when (this) {
            is Nothing -> ""
       /*     is Idle -> "Idle"
            is Loading -> "Loading"
            is Success -> "Success"*/
            is LoginFormState -> "LoginFormState"
          /*  is OtpFormState -> "OtpFormState"*/
            is Error -> "Error"


        }
    }
}

sealed class UiStatusOtp{
    data class OtpFormState(
        val usernameError: Any? = null,
        val phoneError: Any? = null,
        val message: Any? = null,
        val isDataValid: Boolean = false
    ) : UiStatusOtp()
}
