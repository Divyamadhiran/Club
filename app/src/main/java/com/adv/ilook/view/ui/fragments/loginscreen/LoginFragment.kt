package com.adv.ilook.view.ui.fragments.loginscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentLoginBinding
import com.adv.ilook.model.util.assets.BundleKeys.LOGIN_OTP_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_NAME_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_PHONE_KEY
import com.adv.ilook.model.util.assets.Debug
import com.adv.ilook.model.util.assets.PrefImpl
import com.adv.ilook.model.util.responsehelper.Status
import com.adv.ilook.model.util.responsehelper.UiStatusLogin
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.ui.fragments.login.LoginTest
import com.adv.ilook.view.ui.fragments.seeformescreen.contacts.ContactsScreenFragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.PhoneAuthCredential
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>LoginFragment"

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    @Inject
    lateinit var sharedPreference: PrefImpl
    private var _viewBinding: FragmentLoginBinding? = null
    val viewModel by viewModels<LoginViewModel>()

    // val shareViewModel by viewModels<BaseViewModel>()
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate


    var codeSend by Delegates.notNull<Boolean>()
    var verificationCompleted by Delegates.notNull<PhoneAuthCredential>()
    var verificationFailed by Delegates.notNull<String>()
    var isAuthenticated by Delegates.notNull<Boolean>()

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    // Create an OnBackPressedCallback to handle the back button event
    private val onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.loginFragment -> {
                        try {
                            Toast.makeText(requireActivity(), "loginFragment", Toast.LENGTH_SHORT)
                                .show()
                            nav(previousScreenId)
                        } catch (e: Exception) {
                            Log.e(TAG, "handleOnBackPressed: ${e.message}")
                        }
                    }

                    else -> {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) {
            if (Debug.DEBUG_MODE) LoginTest.login(
                viewModel,
                this@LoginFragment,
                _viewBinding!!
            )
            viewModel.init { }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        _viewBinding = binding
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPress)
        initUI()

    }

    private fun initUI() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwnerLiveData.observe(this@LoginFragment) { lifecycleOwner ->
                // Perform UI-related actions on the main thread
                uiReactiveAction()
                // Observe LiveData in the background
                liveDataObserver(lifecycleOwner)
            }
        }
    }

    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
        _viewBinding?.apply {

            viewModel.loginFormState.observe(lifecycleOwner,
                Observer { loginFormState ->
                    val loginState: UiStatusLogin = loginFormState ?: return@Observer
                    when (loginState) {
                        is UiStatusLogin.LoginFormState -> {
                            // disable login button unless both username / password is valid

                            if (loginState.usernameError != null) {
                                usernameText.error = loginState.usernameError.toString()
                            }
                            if (loginState.phoneError != null) {
                                phoneText.error = loginState.phoneError.toString()
                            }
                            generateOtpButton.isEnabled = loginState.isDataValid
                            if (loginState.isDataValid) {
                                hideKeyboard()
                                CoroutineScope(Dispatchers.Main).launch {
                                    viewModel.login(
                                        requireActivity(),
                                        usernameText.text.toString().trim(),
                                        phoneText.text.toString().trim()
                                    )
                                }
                            }
                        }
                    }
                })

            viewModel.loginResult.observe(lifecycleOwner) { it ->
                val loginResult = it ?: return@observe
                Log.d(TAG, "liveDataObserver: ${loginResult.status} ${loginResult.is_loading}")

                when (loginResult.status) {
                    Status.LOADING -> {
                        if (loginResult.is_loading) {
                            innerContainer.alpha = 0.5f
                            innerContainer.isEnabled = false
                            loadingImage.visibility = View.VISIBLE
                            loadingText.visibility = View.VISIBLE
                            Glide.with(requireActivity()).load(R.drawable.loading)
                                .into(loadingImage)
                        } else {
                            loadingImage.visibility = View.GONE
                            innerContainer.alpha = 1f
                            innerContainer.isEnabled = true
                            loadingText.visibility = View.GONE
                        }
                    }

                    Status.ERROR -> {
                        Log.d(TAG, "ERROR----")
                        showLoginFailed(loginResult.user_message as Int)
                    }

                    Status.SUCCESS -> {
                        Log.d(TAG, "liveDataObserver: Success")

                    }
                }
            }
            sharedModel.pushTokenLiveData.observe(lifecycleOwner) {
                Log.d(
                    TAG,
                    "liveDataObserver:-----------encrypted = ${sharedModel.pushTokenLiveData.value}"
                )
            }


            viewModel.tv_login_header.observe(lifecycleOwner) {
                loginText.text = it
            }
            viewModel.tv_username.observe(lifecycleOwner) {
                usernameTILayout.hint = it
            }
            viewModel.tv_phone_number.observe(lifecycleOwner) {
                phoneTILayout.hint = it
            }
            viewModel.bt_login_text.observe(lifecycleOwner) {
                generateOtpButton.text = it
            }
            viewModel.tv_loading_text.observe(lifecycleOwner) {
                loadingText.text = it
            }
            viewModel.prevScreenLiveData.observe(lifecycleOwner) {

                if (!(it == -1 or 0)) {
                    previousScreenId = it
                } else {
                    generateOtpButton.showSnackbar(this.generateOtpButton,
                        msg = "Can't found any screen",
                        actionMessage = "Ok", length = 2, action = { v1 ->

                        }, action2 = { v2 ->

                        }
                    )
                }
            }

            viewModel.nextScreenLiveData.observe(lifecycleOwner) {
                Log.d(TAG, "liveDataObserver:nextScreenId_1  $it")
                nextScreenId_1 = it
            }

            viewModel.codeSent.observe(lifecycleOwner) {
                codeSend = it
                Log.d(TAG, "liveDataObserver: codeSend = ${it}")
                nav(nextScreenId_1, Bundle().apply {
                    val userName = usernameText.text.toString().trim()
                    val userPhone = phoneText.text.toString().trim()
                    putString(USER_NAME_KEY, userName)
                    putString(USER_PHONE_KEY, userPhone)
                    putString(LOGIN_OTP_KEY, null)
                })
                Toast.makeText(requireActivity(), "OTP sent successfully", Toast.LENGTH_SHORT)
                    .show()
            }

            viewModel.verificationCompleted.observe(lifecycleOwner) {
                verificationCompleted = it
                val code = verificationCompleted.smsCode
                Log.d(TAG, "liveDataObserver:Otp verification completed -> ${code}")
//                phoneText.setText(code.toString())

                if (code != null) {
                    nav(nextScreenId_1, Bundle().apply {
                        val userName = usernameText.text.toString().trim()
                        val userPhone = phoneText.text.toString().trim()
                        putString(USER_NAME_KEY, userName)
                        putString(USER_PHONE_KEY, userPhone)
                        putString(LOGIN_OTP_KEY, code)
                    })
                }
            }

            viewModel.verificationFailed.observe(lifecycleOwner) {
                verificationFailed = it
                Log.d(TAG, "liveDataObserver:verificationFailed===> ${verificationFailed}")
                updateUiWithUser(it)
            }

            viewModel.signInResult.observe(lifecycleOwner) {
                isAuthenticated = it
                Log.d(TAG, "liveDataObserver: isAuthenticated -> ${isAuthenticated}")

            }


            viewModel.loginFieldsEnabledFlow.observe(viewLifecycleOwner){ isEnableOptions->
                loginFieldsEnableCondition(isEnableOptions)
            }

        }
        requestSMSPermissions()

    }


    private fun loginFieldsEnableCondition(enableOptions: Map<Int, Boolean>?) {
        _viewBinding?.apply {
            enableOptions?.forEach { (id,values)->
                when(id){
                    loginText.id-> loginText.visibility=if (values) View.VISIBLE else View.GONE
                    generateOtpButton.id-> generateOtpButton.visibility=if (values) View.VISIBLE else View.GONE
                    loadingText.id-> loadingText.visibility=if (values) View.VISIBLE else View.GONE
                }
            }
        }
    }


    private fun requestSMSPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                1
            )
        }
    }

    private fun uiReactiveAction() {
        _viewBinding?.apply {
            ipText.visibility = View.VISIBLE
            getIPAdd { msg, wifiManager ->
                ipText.text = msg
            }
            usernameText.setText(sharedPreference.str(USER_NAME_KEY).toString())
            phoneText.setText(sharedPreference.str(USER_PHONE_KEY))
            usernameText.apply {
                doOnTextChanged { text, start, before, count ->
                    Log.d(
                        TAG,
                        "uiReactiveAction() called with: usernameText => text = $text, start = $start, before = $before, count = $count"
                    )
                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                }
                /*afterTextChanged { str ->
                    Log.d(TAG, "uiReactiveAction: $str")

                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString(),
                        enter="name_entered"
                    )
                }*/
            }
            phoneText.apply {

                doOnTextChanged { text, start, before, count ->
                    Log.d(
                        TAG,
                        "uiReactiveAction() called with: phoneText => text = $text, start = $start, before = $before, count = $count"
                    )
                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                }


                /*  afterTextChanged {
                      viewModel.loginDataChange(
                          usernameText.text.toString(),
                          phoneText.text.toString(),
                                  enter="phone_entered")
                  }*/



                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            activityListener.onRequestPermissionListener(
                                _viewBinding!!,
                                arrayListOf(
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.RECEIVE_SMS
                                )
                            ) {
                                lifecycleScope.launch {

                                    viewModel.loginDataChange(
                                        usernameText.text.toString(),
                                        phoneText.text.toString()
                                    )

                                    /*    viewModel.login(
                                            requireActivity(),
                                            usernameText.text.toString(),
                                            phoneText.text.toString()
                                        )*/
                                }
                            }
                    }
                    false
                }
            }

            generateOtpButton.setOnClickListener {
                activityListener.onRequestPermissionListener(
                    _viewBinding!!,
                    arrayListOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS
                    )
                ) {
                    val userName = usernameText.text.toString().trim()
                    val phone = phoneText.text.toString().trim()

                    //  loadingImage.visibility = View.VISIBLE

                    lifecycleScope.launch {
                        viewModel.loginDataChange(
                            userName,
                            phone,
                        )
                    }

                }

            }
        }

    }


    private fun updateUiWithUser(model: String) {
        val welcome = getString(R.string.welcome) + model
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null

    }
}