package com.adv.ilook.view.ui.fragments.selectscreen

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentSelectScreenBinding
import com.adv.ilook.model.util.assets.BundleKeys.LOGIN_USER_TYPE_KEY
import com.adv.ilook.model.util.assets.LoginUserType
import com.adv.ilook.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "SelectScreenFragment"

@AndroidEntryPoint
class SelectScreenFragment() : BaseFragment<FragmentSelectScreenBinding>() {
    companion object {
        fun newInstance() = SelectScreenFragment()
    }
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSelectScreenBinding
        get() = FragmentSelectScreenBinding::inflate
    private var viewBinding: FragmentSelectScreenBinding? = null
    val viewModel by viewModels<SelectScreenViewModel>()
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    private val handler = Handler(Looper.getMainLooper())
    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenInitLiveObserver()
    }
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding
        iniUI()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun iniUI() {
        liveDataObserver()
        uiReactiveAction()
    }

    private fun screenInitLiveObserver() {
        viewModel.nextScreenLiveData_1.observe(this@SelectScreenFragment) {
            nextScreenId_1 = it
        }
        viewModel.nextScreenLiveData_2.observe(this@SelectScreenFragment) {
            nextScreenId_2 = it
        }
        viewModel.prevScreenLiveData.observe(this@SelectScreenFragment) {
            previousScreenId = it
        }
    }
    private fun liveDataObserver() {
        viewBinding?.apply {
            viewModel.instructionWorkflow.observe(viewLifecycleOwner){ datas ->
                observeViewElements(datas)
            }

            viewModel.selectScrFieldsEnable.observe(viewLifecycleOwner){ isOptionEnabled ->
                Log.d(TAG, "observeViewElementsControls: $isOptionEnabled")
                observeViewElementsControls(isOptionEnabled)
            }

        }
    }
    private fun observeViewElements(elements: Map<Int, String>) {
        viewBinding?.apply {
            elements.forEach{(id,value)->
                when(id){
                    R.id.header_title ->{
                        headerTitle.text=value
                    }
                    R.id.txt_guide_mode ->{
                        txtGuideMode.text=value
                    }
                    R.id.txt_visually_impaired_mode ->{
                        txtVisuallyImpairedMode.text=value
                    }
                }
            }
        }
    }


    private fun observeViewElementsControls(optionEnabled: Map<Int, Boolean>?) {
        viewBinding?.apply {
            optionEnabled?.forEach { (id, values) ->
                Log.d(TAG, "observeViewElementsControls: $id  $values")
                when(id)
                {
                    headerTitle.id-> headerTitle.visibility = if (values) View.VISIBLE else View.GONE
                    txtGuideMode.id-> txtGuideMode.visibility = if (values) View.VISIBLE else View.GONE
                    txtVisuallyImpairedMode.id-> txtVisuallyImpairedMode.visibility = if (values) View.VISIBLE else View.GONE
                }
            }
        }
    }



    private fun uiReactiveAction() {
        viewBinding?.apply {
            txtGuideMode.setOnClickListener {

                nav(nextScreenId_1,Bundle().apply {
                    putString(LOGIN_USER_TYPE_KEY, LoginUserType.ASSISTIVE.name)

                })
            }
            txtVisuallyImpairedMode.setOnClickListener {
                nav(nextScreenId_2,Bundle().apply {
                    putString(LOGIN_USER_TYPE_KEY, LoginUserType.VISUALLY_IMPAIRED.name)
                })
            }
        }
    }
    // Create an OnBackPressedCallback to handle the back button event
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.selectScreenFragment -> {
                        Toast.makeText(requireActivity(), "splash fragment", Toast.LENGTH_SHORT)
                            .show()
                        if (previousScreenId==-1){
                            requireActivity().finish()
                        }else{
                            nav(previousScreenId)
                        }

                    }

                    else -> {
                        Toast.makeText(requireActivity(), "invalid", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        }
    }
}