package com.adv.ilook.view.ui.fragments.seeformescreen.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val TAG = "==>>TabLayoutAdapter"
class TabLayoutAdapter(fa: FragmentActivity,
                      ) : FragmentStateAdapter(fa) {
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private val fragmentTitle: ArrayList<String> = ArrayList()

    override fun getItemCount(): Int {

        return fragmentList.size
    }


    override fun createFragment(position: Int): Fragment {

      return fragmentList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitle.add(title)
    }

    fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }
}