package com.adv.ilook.view.ui.fragments.ainavigator

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adv.ilook.R

class AiNavigatorFragment : Fragment() {

    companion object {
        fun newInstance() = AiNavigatorFragment()
    }

    private val viewModel: AiNavigatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_ai_navigator, container, false)
    }
}