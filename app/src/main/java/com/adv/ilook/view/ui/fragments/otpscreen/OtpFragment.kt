package com.adv.ilook.view.ui.fragments.otpscreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentOtpBinding
import com.adv.ilook.model.util.assets.BundleKeys.LOGIN_OTP_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_NAME_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_PHONE_KEY
import com.adv.ilook.model.util.assets.Debug
import com.adv.ilook.model.util.extension.afterTextChanged
import com.adv.ilook.model.util.responsehelper.Status
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.base.BaseViewModel
import com.bumptech.glide.Glide


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

private const val TAG = "==>>OtpFragment"

@AndroidEntryPoint
class OtpFragment() : BaseFragment<FragmentOtpBinding>() {
    companion object {
        fun newInstance() = OtpFragment()
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOtpBinding
        get() = FragmentOtpBinding::inflate
    private var _viewBinding: FragmentOtpBinding? = null
    private val viewModel: OtpViewModel by viewModels()

    //  val shareViewModel by viewModels<BaseViewModel>()
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    var isSimValidationEnabled by Delegates.notNull<Boolean>()
    var isOtpEnabled by Delegates.notNull<Boolean>()
    var isAuthenticated by Delegates.notNull<Boolean>()
    fun Fragment.hideKeyboard(): Boolean? = view?.let { activity?.hideKeyboard(it) }


    private val onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.otpFragment -> {
                        try {
                            Toast.makeText(requireActivity(), "otp fragment", Toast.LENGTH_SHORT)
                                .show()
                            nav(previousScreenId)
                        } catch (e: Exception) {
                            Log.d(TAG, "handleOnBackPressed: ${e.message}")
                        }

                    }

                    else -> {
                        requireActivity().finish()
                    }

                }
            }
        }
    }
    var userName: String? = null
    var userPhone: String? = null
    var loginOtp: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup:")
        _viewBinding = binding
        arguments?.apply {
            userName = getString(USER_NAME_KEY)
            userPhone = getString(USER_PHONE_KEY)
            loginOtp = getString(LOGIN_OTP_KEY)
            Log.d(TAG, "setup: ${userName},${userPhone},${loginOtp}")
        }
        if (Debug.DEBUG_MODE) OtpTest.otp(this, _viewBinding!!)
        initUI()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPress)
    }

    private fun initUI() {
        uiReactiveAction()
        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                liveDataObserver(lifecycleOwner)
            }
        }
    }

    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
        binding.apply {
            viewModel.tv_otp_header.observe(lifecycleOwner) {
                headerOTPTextTV.text = it
            }
            viewModel.tv_otp_helper_text.observe(lifecycleOwner) {
                subOTPContentTextTV.text = it
            }
            viewModel.et_otp_number_text.observe(lifecycleOwner) {
                otpInputTextTV.hint = it
            }
            viewModel.bt_login_enable.observe(lifecycleOwner) {
                isOtpEnabled = it
                loginBtn.isEnabled = it
            }

            viewModel.bt_login_text.observe(lifecycleOwner) {
                loginBtn.text = it
            }
            viewModel.toast_load_message.observe(lifecycleOwner) {
                loadingOtpTV.text = it
            }
            viewModel.bt_sim_validation_enable.observe(lifecycleOwner) {
                isSimValidationEnabled = it
            }
            viewModel.nextScreenLiveData.observe(lifecycleOwner) {
                nextScreenId_1 = it
            }
            viewModel.prevScreenLiveData.observe(this@OtpFragment) {

                if (!(it == -1 or 0)) {
                    previousScreenId = it
                } else {
                    loginBtn.showSnackbar(this.loginBtn,
                        msg = "Can't found any screen",
                        actionMessage = "Ok", length = 2, action = { v1 ->

                        }, action2 = { v2 ->

                        }
                    )
                }

            }

            viewModel.signInResult.observe(lifecycleOwner) {
                isAuthenticated = it
                Log.d(TAG, "liveDataObserver: isAuthenticated -> ${isAuthenticated}")
                if (isAuthenticated) {

                    CoroutineScope(Dispatchers.Main).launch {
                        sharedModel.pushTokenLiveData.observe(lifecycleOwner) {
                            Log.d(TAG, "liveDataObserver: encrypted = $it")
                            launch(Dispatchers.Main) {
                                viewModel.registeredUserData(
                                    userName!!,
                                    userPhone!!,
                                    it
                                )
                            }
                        }


                    }

                } else {
                    hideKeyboard()
                    loginBtn.showSnackbar(this.loginBtn,
                        msg = "Verification failed",
                        actionMessage = "Ok", length = 1000, action = { v1 ->

                        }, action2 = { v2 ->

                        }
                    )
                    //  nav(previousScreenId)
                }

            }

            viewModel.otpResult.observe(lifecycleOwner) { otpResult ->
                when (otpResult.status) {
                    Status.LOADING -> {
                        Log.d(
                            TAG,
                            "liveDataObserver: LOADING ->${otpResult.is_loading}:${otpResult.user_message}"
                        )
                        if (otpResult.is_loading) {
                            otpFragInnerContainerCL.alpha = 0.5f
                            otpFragInnerContainerCL.isEnabled = false
                            loadingOtpIMV.visibility = View.VISIBLE
                            loadingOtpTV.visibility = View.VISIBLE
                            Glide.with(requireActivity()).load(R.drawable.loading)
                                .into(loadingOtpIMV)
                        } else {
                            loadingOtpIMV.visibility = View.GONE
                            loadingOtpTV.visibility = View.GONE
                            otpFragInnerContainerCL.alpha = 1f
                            otpFragInnerContainerCL.isEnabled = true
                        }
                    }

                    Status.SUCCESS -> {
                        Log.d(TAG, "liveDataObserver: SUCCESS")
                        lifecycleScope.launch(Dispatchers.Main) { nav(R.id.action_otpFragment_to_homeScreenFragment) }
                    }

                    Status.ERROR -> {
                        Log.d(TAG, "liveDataObserver: ERROR")
                        loginBtn.showSnackbar(this.loginBtn,
                            otpResult.user_message.toString(), 1, "Retry", { action1 ->
                                Log.d(TAG, "liveDataObserver: Action 1 ")
                            }) { action2 ->
                            Log.d(TAG, "liveDataObserver: Action 2 ")
                        }
                    }

                }
            }
        }
    }

    private fun uiReactiveAction() {
        binding.apply {
            if (loginOtp != null) {
                otpInputTextTV.setText(loginOtp.toString())
            } else loginOtp = ""
            otpInputTextTV.apply {
                doOnTextChanged { text, start, before, count ->
                    Log.d(
                        TAG,
                        "uiReactiveAction() called with: usernameText => text = $text, start = $start, before = $before, count = $count"
                    )
                    if (text.toString().length == 6) {
                        loginBtn.isEnabled = true
                        hideKeyboard()
                    } else {
                        loginBtn.isEnabled = false
                    }

                }
                afterTextChanged { str ->
                    loginOtp = str.trim()

                }
            }
            loginBtn.setOnClickListener {
                Toast.makeText(requireActivity(), "OTP sent successfully", Toast.LENGTH_SHORT)
                    .show()
                loginOtp = otpInputTextTV.text.toString().trim()
                if (!(loginOtp.isNullOrEmpty()))
                    viewModel.verifyCode(loginOtp!!)
            }
        }
    }


}