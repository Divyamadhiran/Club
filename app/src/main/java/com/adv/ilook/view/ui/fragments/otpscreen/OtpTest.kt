package com.adv.ilook.view.ui.fragments.otpscreen

import com.adv.ilook.databinding.FragmentOtpBinding

object OtpTest {
    fun otp(activity: OtpFragment, _viewBinding:FragmentOtpBinding){
        _viewBinding.apply {
            otpInputTIEditText.setText("123456")
        }
    }
}