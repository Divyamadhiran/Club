package com.adv.ilook.view.ui.fragments.homescreen

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentHomeScreenBinding
import com.adv.ilook.model.util.customview.WindowPreferencesManager
import com.adv.ilook.view.base.BaseFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "==>>HomeScreenFragment"

@AndroidEntryPoint
class HomeScreenFragment : BaseFragment<FragmentHomeScreenBinding>(),
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        fun newInstance() = HomeScreenFragment()
    }

    private val viewModel: HomeScreenViewModel by viewModels()

    private var doubleBackToExitPressedOnce = false
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    private var viewBinding: FragmentHomeScreenBinding? = null
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeScreenBinding
        get() = FragmentHomeScreenBinding::inflate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch {
            viewModel.init {}
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding
        initUI()
        viewModel.menuTitles.observe(viewLifecycleOwner, Observer { titles ->
            updateMenuItemTitles(titles)
        })

    }

    override fun onResume() {
        Log.d(TAG, "onResume: ")
        super.onResume()
        initUI()
    }

    private fun initUI() {
        viewBinding?.apply {
            (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as? AppCompatActivity)?.supportActionBar?.setHomeButtonEnabled(true)
            navigationViewStart.setNavigationItemSelectedListener(this@HomeScreenFragment)

            drawer.addDrawerListener(
                ActionBarDrawerToggle(
                    requireActivity(),
                    drawer, toolbar,
                    0,
                    0
                )
            )
        }
        drawerOpenAndClose()
        onBackPressTwoTimes()
    }

    private fun drawerOpenAndClose() {
        viewBinding?.apply {
            drawer.addDrawerListener(
                object : DrawerLayout.SimpleDrawerListener() {
                    override fun onDrawerOpened(drawerView: View) {
                        navigationViewStart.fitsSystemWindows = true
                        drawerOnBackPressedCallback.isEnabled = true
                    }

                    override fun onDrawerClosed(drawerView: View) {
                        // navigationViewStart.fitsSystemWindows =false
                        drawerOnBackPressedCallback.isEnabled = false
                    }
                }).also {
                drawer.post {
                    if (viewBinding?.drawer!!.isDrawerOpen(GravityCompat.START)
                        || viewBinding?.drawer!!.isDrawerOpen(GravityCompat.END)
                    ) {
                        // navigationViewStart.fitsSystemWindows =true
                        drawerOnBackPressedCallback.isEnabled = true
                    }
                }
            }


        }

    }

    private fun onBackPressTwoTimes() {
        if (doubleBackToExitPressedOnce) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPress)
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(requireActivity(), "Press back again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    private fun updateMenuItemTitles(titles: Map<Int, Pair<String,String>>) {
        val menu = viewBinding?.navigationViewStart?.menu
        titles.forEach { (id, title) ->
            Log.d(TAG, "updateMenuItemTitles: ${title}")
            menu?.findItem(id)?.title = title.first
        }
       /* loadImageAsDrawable(title.second,requireContext()){

        }*/
    }
    fun loadImageAsDrawable(imageUrl: String, context: Context, callback: (Drawable?) -> Unit) {
        Glide.with(context)
            .asDrawable()
            .load(Uri.parse(imageUrl))
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    callback(placeholder)
                }
            })
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        viewModel.handleNavigationItemSelected(item.itemId)
        viewBinding?.apply {
            drawer.closeDrawer(GravityCompat.START)
        }
        return true
    }

    private val drawerOnBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                viewBinding?.drawer!!.closeDrawers()
            }
        }

    private val onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.homeScreenFragment -> {
                        try {
                            requireActivity().finish()
                        } catch (e: Exception) {
                            Log.d(TAG, "handleOnBackPressed: ${e.message}")
                        }
                    }

                    else -> {
                        requireActivity().finish()
                    }
                }
            }
        }
    }


}