package com.adv.ilook.view.ui.fragments.seeformescreen.calogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentCallLogsScreenBinding
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.ui.fragments.seeformescreen.adapter.CallLogsListAdapter
import com.adv.ilook.view.ui.fragments.seeformescreen.dataclasses.MissedCallList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "==>>CallLogsScreenFragment"

@AndroidEntryPoint
class CallLogsScreenFragment : BaseFragment<FragmentCallLogsScreenBinding>() {
    companion object {
        fun newInstance() = CallLogsScreenFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCallLogsScreenBinding
        get() = FragmentCallLogsScreenBinding::inflate


    private var _viewBinding: FragmentCallLogsScreenBinding? = null
    private val viewModel: CallLogsScreenViewModel by viewModels()

    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }

        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup:")
        _viewBinding = binding
        initUI()
        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                liveDataObserver(lifecycleOwner)
            }
        }

    }

    private fun initUI() {
        uiReactiveAction()
        setupRecyclerView()
        showClearMissedCallDialog()
    }

    private fun uiReactiveAction() {
        binding.apply {
            // Set up UI interactions here
            // Example:
            // someButton.setOnClickListener {
            //     // Handle button click
            // }
        }

    }

    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
        binding.apply {
            // Observe ViewModel live data here
            // Example:
            // viewModel.someLiveData.observe(lifecycleOwner) { data ->
            //     // Update UI with data
            // }
        }
    }


    //------------------------------------Setup Recyclerview--------------------------------//
    private fun setupRecyclerView() {
        val missedCalls = getMissedCallList()
        val adapter = CallLogsListAdapter(missedCalls)
        _viewBinding?.apply {
            recyclerCallLogs.layoutManager = LinearLayoutManager(requireContext())
            recyclerCallLogs.adapter = adapter
        }

    }

    private fun getMissedCallList(): List<MissedCallList> {
        //Generate or fetch contact list here.
        return listOf(
            MissedCallList("Divya", "123456789", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Mekala", "789456123", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Praveen", "159357821", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Vivek", "123456789", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Malar", "789456123", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Madhiran", "159357821", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Divya", "123456789", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Mekala", "789456123", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Praveen", "159357821", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Vivek", "123456789", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Malar", "789456123", "Monday, 15 July", "3:05 pm"),
            MissedCallList("Madhiran", "159357821", "Monday, 15 July", "3:05 pm")

        )

    }

    private fun showClearMissedCallDialog() {
        _viewBinding?.apply {
            fabBtnClearMissedCalls.setOnClickListener {
                val clearMissedCallDialog = Dialog(requireContext())
                clearMissedCallDialog.setContentView(R.layout.clearmissedcalldialog)
                val clearMissedCallOkBtn: View =
                    clearMissedCallDialog.findViewById(R.id.clearMissedCallOkBtn)
                val clearMissedCallCancelBtn: View =
                    clearMissedCallDialog.findViewById(R.id.clearMissedCallCancelBtn)
                clearMissedCallOkBtn.setOnClickListener {
                    Toast.makeText(requireActivity(), "Ok button clicked", Toast.LENGTH_SHORT)
                        .show()
                }
                clearMissedCallCancelBtn.setOnClickListener {
                    clearMissedCallDialog.dismiss()
                }
                clearMissedCallDialog.show()
            }
        }
    }

}