package com.adv.ilook.view.ui.fragments.seeformescreen

import ContactListAdapter
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentSeeformeScreenBinding
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.fragments.dataclasses.ContactList
import com.adv.ilook.view.ui.fragments.seeforme.SeeformeScreenViewModel
import com.adv.ilook.view.ui.fragments.seeformescreen.adapter.TabLayoutAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "==>>SeeformeScreenFragment"

@AndroidEntryPoint
class SeeformeScreenFragment : BaseFragment<FragmentSeeformeScreenBinding>() {
    companion object {
        fun newInstance() = SeeformeScreenFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSeeformeScreenBinding
        get() = FragmentSeeformeScreenBinding::inflate
    private var viewBinding: FragmentSeeformeScreenBinding? = null

    private val viewModel: SeeformeScreenViewModel by viewModels()
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    var nextFragment_1 = "fragment_contacts_screen"
    var nextFragment_2 = "fragment_call_logs_screen"
    var nextFragment_3 = "fragment_call_history_screen"
    override fun onAttach(context: Context) {
        super.onAttach(context)

        lifecycleScope.launch { viewModel.init() {} }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenInitLiveObserver()
    }

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding
        initUI()
        liveDataObserver()
        uiReactiveAction()
    }


    private fun screenInitLiveObserver() {
        viewModel.allScreenLiveData.observe(this) { screenMap ->
            // handle screen navigation here
            Log.d(TAG, "screenInitLiveObserver: $screenMap")
            screenMap.forEach { key, value ->
                if (key == 0) nextFragment_1 = value as String
                if (key == 1) nextFragment_2 = value as String
                if (key == 2) nextFragment_3 = value as String
            }
        }
    }

    private fun initUI() {
        setupTabs()
        searchContacts()
    }

    private fun setupTabs() {
        val adapter = TabLayoutAdapter(
            requireActivity()
        )

        adapter.addFragment(
            BasicFunction.getFragments(nextFragment_1),
            "Contact"
        )
        adapter.addFragment(
            BasicFunction.getFragments(nextFragment_2),
            "Call Logs"
        )
        adapter.addFragment(
            BasicFunction.getFragments(nextFragment_3),
            "Call History"
        )
        viewBinding?.apply {
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = adapter.getPageTitle(position)
                Log.d(TAG, "initUI: -------------")
            }.attach()
        }
    }

    lateinit var contactListAdapter: ContactListAdapter
    lateinit var contactList: List<ContactList>
    private fun liveDataObserver() {}
    private fun uiReactiveAction() {}
    private fun searchContacts() {
        sharedModel.addContactList.observe(this) {
            Log.d(TAG, "searchContacts: addContactList=$it")
            contactList = it
        }
        sharedModel.contactListAdapter.observe(this) {
            Log.d(TAG, "searchContacts: contactListAdapter =$it")
            contactListAdapter = it
            viewBinding?.apply { searchContactText.setText("") }
        }
        viewBinding?.apply {
            searchContactText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    contactListAdapter.filter(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
    }


}