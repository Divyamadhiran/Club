package com.adv.ilook.model.util.customview

import android.content.Context
import android.view.LayoutInflater
import com.adv.ilook.databinding.DialogAddcontactsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomDialog(context: Context) : MaterialAlertDialogBuilder(context) {

   // private val customView: View = LayoutInflater.from(context).inflate(R.layout.dialog_addcontacts, null)
    private val binding: DialogAddcontactsBinding = DialogAddcontactsBinding.inflate(LayoutInflater.from(context))


    init {
        setView(binding.root)
        setCancelable(true)
        binding?.apply {

            saveBtn.setOnClickListener {
                val inputText = addContactNameText.text.toString()

                create().dismiss()
            }

        }

    }

    fun setTitleText(title: String): CustomDialog {

        return this
    }

    fun setButtonClickListener(listener: (String) -> Unit): CustomDialog {
        binding?.saveBtn?.setOnClickListener {
            val inputText = binding?.addContactNameText?.text.toString()
            listener(inputText)
            create().dismiss()
        }
        return this
    }
}
