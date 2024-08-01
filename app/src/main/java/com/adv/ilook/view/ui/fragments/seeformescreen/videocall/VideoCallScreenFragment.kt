package com.adv.ilook.view.ui.fragments.seeformescreen.videocall

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.buildSpannedString
import androidx.fragment.app.viewModels
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentCallhistoryScreenBinding
import com.adv.ilook.databinding.FragmentVideoCallScreenBinding
import com.adv.ilook.view.base.BaseActivity
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.ui.fragments.dataclasses.ContactList

import kotlin.properties.Delegates

private const val TAG = "==>>VideoCallScreenFragment"
class VideoCallScreenFragment : BaseFragment<FragmentVideoCallScreenBinding>()
{
    companion object {
        fun newInstance() = VideoCallScreenFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVideoCallScreenBinding
        get() = FragmentVideoCallScreenBinding::inflate


    private var _viewBinding: FragmentVideoCallScreenBinding? = null
    //private val viewModel: VideoCallScreenViewModel by viewModels()
    private var contactList:ContactList?=null

    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup:")
        _viewBinding=binding
//        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.init { }
//            uiReactiveAction()
//        }
//        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
//            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
//                liveDataObserver(lifecycleOwner)
//            }
//        }

        getBundleDataFromContactFrag()
        endCall()
        audioControlBtnClickListener()
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

private fun getBundleDataFromContactFrag(){
    arguments.let {
        contactList=it?.getParcelable("contactList")
    }
}
private fun endCall()
{
    _viewBinding?.endCallButton?.setOnClickListener {
        Log.d(TAG, "End call button clicked")
        Toast.makeText(requireContext(), "End Call......", Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}
private fun audioControlBtnClickListener(){
    _viewBinding?.apply {
        micOnImv.setOnClickListener {
            micOnImv.visibility=View.GONE
            micOffImv.visibility=View.VISIBLE
        }
        micOffImv.setOnClickListener {
            micOffImv.visibility=View.GONE
            micOnImv.visibility=View.VISIBLE
        }
        speakerOnImv.setOnClickListener {
            speakerOnImv.visibility=View.GONE
            speakerOffImv.visibility=View.VISIBLE
        }
        speakerOffImv.setOnClickListener {
            speakerOffImv.visibility=View.GONE
            speakerOnImv.visibility=View.VISIBLE
        }

    }
}

}
