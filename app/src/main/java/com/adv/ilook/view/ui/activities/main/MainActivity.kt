package com.adv.ilook.view.ui.activities.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.adv.ilook.R
import com.adv.ilook.databinding.ActivityMainBinding
import com.adv.ilook.model.util.assets.PrefImpl
import com.adv.ilook.model.util.assets.SharedPrefKey
import com.adv.ilook.model.util.receiver.UsbReceiver
import com.adv.ilook.model.util.service.appservice.MainServiceActions
import com.adv.ilook.model.util.worker.UsbWorker
import com.adv.ilook.model.util.extension.REQUEST_CODE_SCREEN_CAPTURE
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseActivity
import com.adv.ilook.view.base.NavigationHost
import com.adv.ilook.view.base.dialog.UsbPermissionDialogFragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar

import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val TAG = "==>>MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationHost {
    private var viewBinding: ActivityMainBinding? = null
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    //val sharedModel by viewModels<BaseViewModel>()
    val mainViewdModel by viewModels<MainViewModel>()
    lateinit var usbReceiver: UsbReceiver

    @Inject
    lateinit var sharedPreference: PrefImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        liveDataObserver()
        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        sharedModel.actionLiveData.observe(this) {
            Log.d(TAG, "onCreate: actionLiveData ==> ${it}")
            if (it == "REQUEST_CODE_NOTIFICATION-TRUE") startScreenCapture()
        }
        sharedModel.resultLiveData.observeForever  { data ->
            Log.d(TAG, "onCreate: ======================>>worflow $data")
        }
        setupBroadCast()
        scheduleUsbWorkAtOnce(true)
       // startActivity(Intent(this@MainActivity,MainActivity21::class.java))
    }

    private fun setupBroadCast() {
        usbReceiver = UsbReceiver()
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        ContextCompat.registerReceiver(
            this,
            usbReceiver,
            filter,
            ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
        )
        usbReceiver.onCallback { device ->
            // if (device != null)
            // showOpenWithDialog(device)

        }
    }

    private fun showOpenWithDialog(device: UsbDevice) {
        if (!isFinishing && !isDestroyed) {
            val dialogFragment = UsbPermissionDialogFragment.newInstance(device.deviceName, device)
            Log.d(TAG, "showOpenWithDialog: ------------------${device.deviceName}")
            dialogFragment.show(
                supportFragmentManager,
                UsbPermissionDialogFragment::class.java.simpleName
            )
        }


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: ")
        intent?.let {
            // Handle USB device if activity is restarted due to USB device attachment
            handleUsbDevice(it)
        }
    }

    private fun handleUsbDevice(intent: Intent) {
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED == intent.action) {
            val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
            } else {
                intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
            }
            device?.let {
                // Your logic to handle the USB device
                // For example, show a dialog or perform some action
                // showOpenWithDialog(this, it)
                showOpenWithDialog(device)
            }
        }
    }


    private fun showOpenWithDialog(context: Context, device: UsbDevice) {
        val openWithIntent = Intent(Intent.ACTION_VIEW).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, Uri.fromParts("usb", device.deviceName, null))
        }
        context.startActivity(Intent.createChooser(openWithIntent, "Open my with"))
    }

    private fun scheduleUsbWorkAtOnce(detected: Boolean) {
        val inputData = workDataOf("USB_DEVICE_DETECTED" to detected)
        val workRequest = OneTimeWorkRequestBuilder<UsbWorker>()
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun scheduleUsbWorkAtPeriod(detected: Boolean) {
        val workRequest = PeriodicWorkRequestBuilder<UsbWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(30, TimeUnit.SECONDS) // Optional initial delay
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }


    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding

        setupBackPressed()
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            Log.d(TAG, "setup: -------if")
            onRequestPermissionListener(
                binding,
                arrayListOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.FOREGROUND_SERVICE_CAMERA,
                    Manifest.permission.FOREGROUND_SERVICE_MICROPHONE
                )
            ) { result ->
                if (result)
                    startAppService()
                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d(TAG, "setup: ----->else")
            startAppService()
        }
    }


    fun liveDataObserver() {
        //this creating token
        try {
            if (sharedPreference.str(SharedPrefKey.TOKEN_KEY).isEmpty()|| sharedPreference.str(SharedPrefKey.TOKEN_KEY) == "null"){

                if (!networkHelper.isNetworkConnected()){
                    Toast.makeText(this, "Please check your network connection...", Toast.LENGTH_SHORT).show()
                    return
                }
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        // this fail
                        if (!task.isSuccessful) {
                            Log.d(
                                TAG,
                                "Fetching FCM registration token failed",
                                task.exception
                            )
                            return@addOnCompleteListener
                        }

                        val token = task.result
                        mainViewdModel.tokenEncrypt(token)




                    }.addOnFailureListener {
                        Log.d(TAG, "liveDataObserver: Failure = ${it.message}")
                    }
            }else{

                mainViewdModel.tokenEncrypt(sharedPreference.str(SharedPrefKey.TOKEN_KEY))
            }
        }
        catch (e: Exception) {
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()

        }


        mainViewdModel.apply {
            usbDeviceDetected.observe(this@MainActivity) {
                Log.d(TAG, "usbDeviceDetected ==> $it")

            }
            //used for decrypt
            privateKeyLiveData.observe(this@MainActivity) {
                sharedModel.privateToken(it)
            }
            pushTokenLiveData.observe(this@MainActivity) {
                sharedPreference.put(
                    SharedPrefKey.TOKEN_KEY,
                    it
                )
            }
            pushEncryptTokenLiveData.observe(this@MainActivity){
                sharedModel.pushNotificationToken(it)
            }

        }

    }


    private fun startAppService(isMediaProjection: Boolean = false) {
        if (isMediaProjection) mainServiceRepository.startMediaProjectionService(MainServiceActions.HANDLE_PROJECTION.name)
        else mainServiceRepository.startService(MainServiceActions.INIT_SERVICE.name)
    }


    private fun startScreenCapture() {
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
                startAppService(isMediaProjection = true)
            } else {
                Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun findNavControl(): NavController? = findNavHostFragment()?.findNavController()

    override fun hideNavigation(animate: Boolean) {
        Log.d(TAG, "hideNavigation: ")
    }

    override fun showNavigation(animate: Boolean) {
        Log.d(TAG, "showNavigation: ")
    }

    override fun openTab(navigationId: Int) {
        Log.d(TAG, "openTab: ")
    }

    override fun openDiscoverTab() {
        Log.d(TAG, "openDiscoverTab: ")
    }


    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    private fun setupBackPressed() {
        Log.d(TAG, "setupBackPressed: ")
        onBackPressedDispatcher.addCallback(this, callback)
    }

    // Create an OnBackPressedCallback to handle the back button event
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Handle the back button event here
            mainServiceRepository.stopService()
            isEnabled = false
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.splashFragment -> {
                        Log.d(TAG, "handleOnBackPressed: splash")
                    }

                    R.id.loginFragment -> {
                        Log.d(TAG, "handleOnBackPressed: login")
                    }

                    else -> {

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
        unregisterReceiver(usbReceiver)
        callback.remove()
        viewBinding = null

    }
}