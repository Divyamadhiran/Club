package com.adv.ilook.view.ui.fragments.seeformescreen.contacts

import ContactListAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.adv.ilook.R
import com.adv.ilook.databinding.DialogAddcontactsBinding
import com.adv.ilook.databinding.FragmentContactsScreenBinding
import com.adv.ilook.model.util.responsehelper.UiStatusLogin
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.ui.fragments.dataclasses.ContactList
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val TAG = "==>>ContactsScreenFragment"

@AndroidEntryPoint
class ContactsScreenFragment : BaseFragment<FragmentContactsScreenBinding>(),
    ContactListAdapter.OnItemClickListener {
    companion object {
        fun newInstance() = ContactsScreenFragment()
        private const val PICK_CONTACT_REQUEST = 1
    }

    private lateinit var addDialogBinding: DialogAddcontactsBinding
    private var viewBinding: FragmentContactsScreenBinding? = null
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContactsScreenBinding
        get() = FragmentContactsScreenBinding::inflate
    private val viewModel: ContactsScreenViewModel by viewModels()
    lateinit var contacts: List<ContactList>
    lateinit var contactListAdapter: ContactListAdapter
    private lateinit var pickContactLauncher: ActivityResultLauncher<Intent>
    private lateinit var userName: String
    private lateinit var phoneNumber: String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) { viewModel.init { } }
        pickContactLauncher =
            registerForActivityResult(PickContactContract(requireContext())) { result ->
                result?.let { (name, number) ->
                    // e.g., display them in a TextView or use them in your app logic
                    addDialogBinding.apply {
                        phoneNumber = viewModel.countryCodeAdd(number.trim().replace(" ", ""))
                        userName = name.trim().replace(" ", "")
                        addContactNameText.setText(userName)
                        addContactNumberText.setText(phoneNumber)
                    }
                }
            }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup:")
        viewBinding = binding
        initUI()
        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
            lifecycleScope.launch(Dispatchers.Main) {
                liveDataObserver(lifecycleOwner)
            }
        }

    }

    private fun initUI() {
        uiReactiveAction()
        setupRecyclerView()
        showAddContactDialog()
    }


    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
        binding.apply {
            // Observe ViewModel live data here
            viewModel.loginFormState.observe(lifecycleOwner) { state ->
                when (state) {
                    is UiStatusLogin.LoginFormState -> {
                        Log.d(
                            TAG,
                            "liveDataObserver: username_error=${state.usernameError},password_error=${state.phoneError} isDatavalid=${state.isDataValid}"
                        )
                        addDialogBinding?.apply {
                            if (state.usernameError != null) {
                                addContactNameText.requestFocus()
                                addContactNameTil.error = state.usernameError as String
                            } else addContactNameTil.isErrorEnabled = false
                            if (state.phoneError != null) {
                                addContactNumberText.requestFocus()
                                addContactNumberTil.error = state.phoneError as String
                            } else addContactNumberTil.isErrorEnabled = false
                        }
                        if (state.isDataValid) {
                            addDialogBinding?.apply {
                                pickBtn.showSnackbar(addDialogBinding.root,
                                    msg = state.message as String,
                                    length = 1000,
                                    action = { v1 ->

                                    },
                                    action2 = { v2 ->

                                    })
                            }
                        }

                    }
                }

            }
        }
    }


    private fun uiReactiveAction() {
        binding.apply {
            // Set up UI interactions here

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        contacts = getContactLists()
        contactListAdapter = (viewBinding?.recyclerContactList?.adapter as? ContactListAdapter)!!
        sharedModel._addContactList.value = contacts
        sharedModel._contactListAdapter.value = contactListAdapter
    }


    //------------------------------------Setup Recyclerview--------------------------------//
    private fun setupRecyclerView() {
        contacts = getContactLists()
        contactListAdapter = ContactListAdapter(contacts, this)
        viewBinding?.recyclerContactList?.layoutManager = LinearLayoutManager(requireContext())
        viewBinding?.recyclerContactList?.adapter = contactListAdapter


    }

    private fun getContactLists(): List<ContactList> {
        //Generate or fetch contact list here.
        return listOf(
            ContactList("Divya", "123456789"),
            ContactList("Mekala", "789456123"),
            ContactList("Praveen", "159357821"),
            ContactList("Vivek", "123456789"),
            ContactList("Malar", "789456123"),
            ContactList("Madhiran", "159357821"),
            ContactList("Divya", "123456789"),
            ContactList("Mekala", "789456123"),
            ContactList("Praveen", "159357821"),
            ContactList("Vivek", "123456789"),
            ContactList("Malar", "789456123"),
            ContactList("Madhiran", "159357821")

        )

    }

    private fun showAddContactDialog() {
        viewBinding?.apply {
            fabBtnAddContacts.setOnClickListener {
                val addDialog = Dialog(requireContext(), R.style.RoundedDialogTheme)
                addDialogBinding = DialogAddcontactsBinding.inflate(layoutInflater)
                addDialog.setContentView(addDialogBinding.root)
                addDialogBinding?.apply {
                    addContactNameText.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            // Handle text change here
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // Handle text before change here
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            Log.d(TAG, "onTextChanged: s =$s")
                            // Handle text change here
                            viewModel.userNameState(
                                addContactNameText.text.toString().trim()
                            )
                        }
                    })
                    addContactNumberText.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            // Handle text change here
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // Handle text before change here
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            // Handle text change here
                            viewModel.userPhoneState(
                                addContactNumberText.text.toString().trim()
                            )
                        }
                    })

                    cancelBtn.setOnClickListener {
                        addDialog.dismiss()
                    }

                    saveBtn.setOnClickListener {
                        // Handle save button click here
                        Toast.makeText(requireContext(), "Save Button Clicked", Toast.LENGTH_SHORT)
                            .show()
                        userName = addContactNameText.text?.trim().toString()
                        phoneNumber = addContactNumberText.text?.trim().toString()
                        viewModel.addContactsToFirebase(username = userName, phone = phoneNumber)
                    }

                    pickBtn.setOnClickListener {
                        // Handle save button click here
                        Toast.makeText(requireContext(), "Pick Button Clicked", Toast.LENGTH_SHORT)
                            .show()
                        pickContactLauncher.launch(Intent())

                    }
                }
                addDialog.show()
            }

        }

    }

    override fun onItemClick(contactList: ContactList) {
        val bundle = Bundle().apply {
            putParcelable("contactList", contactList)
        }
        nav(R.id.action_contactsScreenFragment_to_videoCallScreenFragment, bundle)
    }

}