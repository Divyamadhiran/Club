package com.adv.ilook.view.ui.fragments.ocrscreen

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adv.ilook.R

class OcrScreenFragment : Fragment() {

    companion object {
        fun newInstance() = OcrScreenFragment()
    }

    private val viewModel: OcrScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_ocr_screen, container, false)
    }
}