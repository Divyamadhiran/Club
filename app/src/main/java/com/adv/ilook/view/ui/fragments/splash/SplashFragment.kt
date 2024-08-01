package com.adv.ilook.view.ui.fragments.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navOptions
import coil.Coil
import coil.util.CoilUtils
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentSplashBinding
import com.adv.ilook.model.util.assets.SharedPrefKey
import com.adv.ilook.model.util.permissions.PermissionManager
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.fragments.homescreen.HomeScreenFragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>SplashFragment"

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    object MySingleton {
        var nextScreen by Delegates.notNull<Int>()
    }

    companion object {
        fun newInstance() = SplashFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSplashBinding
        get() = FragmentSplashBinding::inflate
    private var viewBinding: FragmentSplashBinding? = null
    val viewModel by viewModels<SplashViewModel>()
    private val handler = Handler(Looper.getMainLooper())

    // Create an OnBackPressedCallback to handle the back button event
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.splashFragment -> {
                        Toast.makeText(requireActivity(), "splash fragment", Toast.LENGTH_SHORT)
                            .show()
                        requireActivity().finish()
                    }

                    else -> {
                        Toast.makeText(requireActivity(), "invalid", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }
        Log.d(TAG, "onAttach: ")


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ------")

    }

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding
        initUI()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        lifecycleScope.launch(Dispatchers.Main) {
            liveDataObserver()
        }

    }

    private fun initUI() {
        uiReactiveAction()
    }

    fun liveDataObserver() {
        viewModel.nextScreenLiveData.observe(this) {
            Log.d(TAG, "onCreate: live => it =$it")
            MySingleton.nextScreen = it
            //  nav(it)
        }
        viewModel.splashFields.observe(this) {
            Log.d(TAG, "onCreate: live => it =$it")

            viewBinding?.apply {
                Glide.with(this@SplashFragment)

                    .load(it[R.id.back_IMV])
                    .placeholder(R.drawable.hslook_logo) // Optional placeholder image
                    .error(R.drawable.hslook_logo) // Optional error image
                    .into(backIMV)

                headerSplashTextTV.text = it[R.id.header_SplashText_TV]
                //Glide.with(requireActivity()).asGif().load(Uri.parse("https://firebasestorage.googleapis.com/v0/b/seeforme-server-function.appspot.com/o/seeforme_images%2Floading.gif?alt=media&token=9cb57bec-408f-42da-8490-c8bbdd70c11a")).into(backIView)
            }

        }

    }

    private fun uiReactiveAction() {
        viewBinding?.apply {
            headMotionLayout.postDelayed(Runnable {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d(TAG, "setup: ----postDelayed after 1 sec")
                    handler.postDelayed(runnableInner, 3000)
                }
            }, 500)

            // Glide.with(requireActivity()).asGif().load("res/drawable/e.gif").into(backIView)

        }
    }

    val runnableInner = {
        Log.d(TAG, "runnable run after 2sec: ")
       // nav(R.id.action_splashFragment_to_navigation) // goto home screen used this
       nav(R.id.action_splashFragment_to_selectScreenFragment)
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")

    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: ")
    }


    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: ")
        super.onDestroyView()
        // handler.removeCallbacks(runnableInner)
        viewBinding = null
    }
}