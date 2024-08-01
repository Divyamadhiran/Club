package com.adv.ilook.view.ui.fragments.commodityscreen

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.adv.ilook.R
import com.adv.ilook.view.base.PermissionListener

class CommodityScreenFragment : Fragment() {

    companion object {
        fun newInstance() = CommodityScreenFragment()
    }

    private val viewModel: CommodityScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_commodity_screen, container, false)
    }

}