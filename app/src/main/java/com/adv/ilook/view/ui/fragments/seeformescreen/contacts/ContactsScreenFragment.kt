package com.adv.ilook.view.ui.fragments.seeformescreen.contacts

import ContactListAdapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentContactsScreenBinding
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.ui.fragments.seeformescreen.dataclasses.ContactList
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

    private var viewBinding: FragmentContactsScreenBinding? = null
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContactsScreenBinding
        get() = FragmentContactsScreenBinding::inflate
    private val viewModel: ContactsScreenViewModel by viewModels()
    lateinit var contacts: List<ContactList>
    lateinit var contactListAdapter: ContactListAdapter
    private lateinit var pickContactLauncher: ActivityResultLauncher<Intent>
    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) { viewModel.init { } }
         pickContactLauncher = registerForActivityResult(PickContactContract(requireContext())) { result ->
            result?.let { (name, number) ->
                // e.g., display them in a TextView or use them in your app logic

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
        contactListAdapter = (viewBinding?.recyclerContactListRV?.adapter as? ContactListAdapter)!!
        sharedModel._addContactList.value = contacts
        sharedModel._contactListAdapter.value = contactListAdapter
    }


    //------------------------------------Setup Recyclerview--------------------------------//
    private fun setupRecyclerView() {
        contacts = getContactLists()
        contactListAdapter = ContactListAdapter(contacts, this)
        viewBinding?.recyclerContactListRV?.layoutManager = LinearLayoutManager(requireContext())
        viewBinding?.recyclerContactListRV?.adapter = contactListAdapter
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
            addContactFABtn.setOnClickListener {
                val addDialog = Dialog(requireContext(), R.style.RoundedDialogTheme)
                addDialog.setContentView(R.layout.dialog_addcontacts)
                val addNameId: TextInputEditText = addDialog.findViewById(R.id.addContactNameid)
                val addPhoneNumberId: TextInputEditText =
                    addDialog.findViewById(R.id.addContactNumberid)
                val cancelBtn: View = addDialog.findViewById(R.id.cancelBtn)
                val saveBtn: View = addDialog.findViewById(R.id.saveBtn)
                val pickBtn: View = addDialog.findViewById(R.id.pickBtn)

                cancelBtn.setOnClickListener {
                    addDialog.dismiss()
                }
                saveBtn.setOnClickListener {
                    // Handle save button click here
                    Toast.makeText(requireContext(), "Save Button Clicked", Toast.LENGTH_SHORT)
                        .show()

                    viewModel.addContactsToFirebase(
                        addNameId.text?.trim().toString(),
                        addPhoneNumberId.text?.trim().toString()
                    )
                }

                pickBtn.setOnClickListener {
                    // Handle save button click here
                    Toast.makeText(requireContext(), "Pick Button Clicked", Toast.LENGTH_SHORT)
                        .show()
                    pickContactLauncher.launch(Intent())

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