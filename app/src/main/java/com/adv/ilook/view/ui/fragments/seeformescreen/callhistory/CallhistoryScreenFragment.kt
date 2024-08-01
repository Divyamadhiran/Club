package com.adv.ilook.view.ui.fragments.seeformescreen.callhistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.adv.ilook.databinding.FragmentCallhistoryScreenBinding
import com.adv.ilook.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

private const val TAG = "==>>CallhistoryScreenFragment"
@AndroidEntryPoint
class CallhistoryScreenFragment : BaseFragment<FragmentCallhistoryScreenBinding>() {
    companion object {
        fun newInstance() = CallhistoryScreenFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCallhistoryScreenBinding
        get() = FragmentCallhistoryScreenBinding::inflate


    private var _viewBinding: FragmentCallhistoryScreenBinding? = null
    //private val viewModel: callhistoryScreenViewModel by viewModels()

    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup:")
        _viewBinding = binding
//        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.init { }
//            uiReactiveAction()
//        }
//        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
//            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
//                liveDataObserver(lifecycleOwner)
//            }
//        }

    }
//    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
//        binding.apply {
//            // Observe ViewModel live data here
//            // Example:
//            // viewModel.someLiveData.observe(lifecycleOwner) { data ->
//            //     // Update UI with data
//            // }
//        }
//    }
//    private fun uiReactiveAction() {
//        binding.apply {
//            // Set up UI interactions here
//            // Example:
//            // someButton.setOnClickListener {
//            //     // Handle button click
//            // }
//        }
//    }



}