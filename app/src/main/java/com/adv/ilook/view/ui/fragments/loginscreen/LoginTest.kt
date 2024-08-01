package com.adv.ilook.view.ui.fragments.login


import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.adv.ilook.databinding.FragmentLoginBinding
import com.adv.ilook.model.data.workflow.PhoneItem

import com.adv.ilook.view.ui.fragments.loginscreen.LoginFragment
import com.adv.ilook.view.ui.fragments.loginscreen.LoginViewModel


object LoginTest {
    suspend fun login(
        viewModel: LoginViewModel,
        fragment: LoginFragment,
        _viewBinding: FragmentLoginBinding
    ) {
        val phoneList = ArrayList<PhoneItem?>()
        val nameList = ArrayList<String>()
        viewModel.init{}
        viewModel.testingPhoneNumber.observe(fragment.viewLifecycleOwner, Observer { data ->
            phoneList.clear()
            nameList.clear()
            data?.forEach { item ->
                phoneList.add(item)
                nameList.add(item?.name ?: "") // Add item names to nameList
            }


            val adapter = ArrayAdapter(
                fragment.requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                nameList
            )
            _viewBinding.testPhoneText.setAdapter(adapter)
        })


        _viewBinding.apply {
            testPhoneText.setOnItemClickListener { adapterView, view, position, id ->
                val selectedName = adapterView.getItemAtPosition(position).toString()
                val selectedPhone = phoneList[position]?.number.toString() // Assuming PhoneItem has a 'number' property

                usernameText.setText(selectedName)
                phoneText.setText(selectedPhone)
            }
        }
    }
}
