package com.adv.ilook.view.ui.fragments.instruction

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentInstructionBinding
import com.adv.ilook.databinding.FragmentSelectScreenBinding
import com.adv.ilook.model.util.assets.BundleKeys.LOGIN_OTP_KEY
import com.adv.ilook.model.util.assets.BundleKeys.LOGIN_USER_TYPE_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_NAME_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_PHONE_KEY
import com.adv.ilook.model.util.assets.LoginUserType
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.ui.fragments.loginscreen.LoginFragment

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.properties.Delegates

private const val TAG = "InstructionFragment"

@AndroidEntryPoint
class InstructionFragment() : BaseFragment<FragmentInstructionBinding>() {
    companion object {
        fun newInstance() = InstructionFragment()
    }
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
var loginUserType=LoginUserType.VISUALLY_IMPAIRED.name
    // Create an OnBackPressedCallback to handle the back button event
    private val onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.instructionFragment -> {
                        Toast.makeText(
                            requireActivity(),
                            "instruction fragment",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        try {
                            if (previousScreenId != 0) nav(previousScreenId) else requireActivity().finish()
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



    private val viewModel: InstructionViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInstructionBinding
        get() = FragmentInstructionBinding::inflate
    private var viewBinding: FragmentInstructionBinding? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }
    }
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        arguments?.apply {
            loginUserType = getString(LOGIN_USER_TYPE_KEY).toString()

            Log.d(TAG, "setup: loginUserType => ${loginUserType}")
        }
        viewBinding = binding
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPress)

        viewModel.nextScreenLiveData_1.observe(this@InstructionFragment) {
            nextScreenId_1 = it
        }
        viewModel.prevScreenLiveData.observe(this@InstructionFragment) {
            previousScreenId = it
        }

        liveDataObserver()

    }

    fun liveDataObserver() {
        viewBinding?.apply {
            viewModel.tv_select_screen_header.observe(this@InstructionFragment) {
                headerTitle.text = it
            }
            viewModel.html_terms_of_use_enable.observe(this@InstructionFragment) { enable ->
                if (enable) {
                    viewModel.html_terms_of_use.observe(this@InstructionFragment) { htmlStr ->
                        webViewSetUp(enable, htmlStr.trimIndent())
                    }
                } else {
                    webViewSetUp(enable)
                }
            }

            viewModel.btn_agree_text.observe(this@InstructionFragment) { text ->
                agreeButton.text = text
            }
            viewModel.btn_disagree_text.observe(this@InstructionFragment) { text ->
                disagreeButton.text = text
            }
            viewModel.btn_agree_enable.observe(this@InstructionFragment) { enable ->
                agreeButton.isEnabled = enable
            }
            viewModel.btn_disagree_enable.observe(this@InstructionFragment) { enable ->
                disagreeButton.isEnabled = enable
            }

            viewModel.instructionScrRemoteFlow.observe(viewLifecycleOwner){isEnabledOptions->
                instructionScrElementsControls(isEnabledOptions)
            }

        }
    }

    private fun instructionScrElementsControls(isEnabledOptions:Map<Int,Boolean>) {
        viewBinding?.apply {
            isEnabledOptions.forEach { (id, values) ->
                when (id) {
                    headerTitle.id -> headerTitle.visibility =
                        if (values) View.VISIBLE else View.GONE

                    imgBackArrow.id -> imgBackArrow.visibility =
                        if (values) View.VISIBLE else View.GONE

                    webView.id -> webView.visibility = if (values) View.VISIBLE else View.GONE
                    agreeButton.id -> agreeButton.visibility =
                        if (values) View.VISIBLE else View.GONE

                    disagreeButton.id -> disagreeButton.visibility =
                        if (values) View.VISIBLE else View.GONE
                }
            }
        }
    }

        fun webViewSetUp(enable: Boolean = false, htmlStr: String = "app/src/main/assets/terms.html") {
        viewBinding?.apply {

            webView.settings.javaScriptEnabled = false
            webView.webViewClient = WebViewClient()
            if (enable)
                webView.loadData(htmlStr, "text/html", "UTF-8")
            else
                webView.loadUrl(htmlStr)
        }
    }

    override fun onResume() {
        super.onResume()
        uiReactiveAction()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun uiReactiveAction() {
        Log.d(TAG, "uiReactiveAction: ")
        viewBinding?.apply {
            agreeButton.setOnClickListener {
                val time = System.currentTimeMillis()
                Log.d(TAG, "uiReactiveAction: ${time}")
                lifecycleScope.launch {

                    requireActivity().runOnUiThread {
                        nav(nextScreenId_1)
                    }
                    agreeButton.isEnabled = false
                    delay(1000)
                    agreeButton.isEnabled = true
                    Log.d(TAG, "uiReactiveAction: ${System.currentTimeMillis() - time}")
                }
            }
            disagreeButton.setOnClickListener {
                val time = System.currentTimeMillis()
                Log.d(TAG, "uiReactiveAction:disagree ${time}")
                lifecycleScope.launch() {
try {
    requireActivity().runOnUiThread {
        if (previousScreenId != 0) nav(previousScreenId)
    }
    disagreeButton.isEnabled = false
    delay(1000)
    disagreeButton.isEnabled = true
    Log.d(TAG, "uiReactiveAction:disagree ${System.currentTimeMillis() - time}")
}catch (e:Exception){
    Log.d(TAG, "uiReactiveAction: ${e.message}")
}
          
                }

            }
        }
    }
}