package com.adv.ilook.model.util.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.adv.ilook.model.util.assets.BReceiverKeys.CONNECTED_KEY
import com.adv.ilook.view.base.dialog.UsbPermissionDialogFragment
import com.adv.ilook.view.ui.activities.main.MainActivity

private const val TAG = "==>>UsbReceiver"

class UsbReceiver() : BroadcastReceiver() {
    val intentAction by lazy { Intent() }
      var intentUSB: Intent?=null
    lateinit var callback: (device: UsbDevice) -> Unit

    fun onCallback(call: (device: UsbDevice) -> Unit) {
        callback = call
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
        } else {
            intent?.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        }
        if (device != null) {
            when (intent?.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val connected = intentAction.getBooleanExtra(CONNECTED_KEY, true)
                    Log.d(
                        TAG,
                        "onReceive: ACTION_USB_DEVICE_ATTACHED = $context ,connected = $connected " +
                                ""
                    )
                    intentUSB = intent
                    if (!connected) {
                        intentAction.putExtra(CONNECTED_KEY, true)
                        // requestUsbPermission(context!!, device)
                        callback(device)
                    }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    intentUSB=intent
                    Log.d(TAG, "onReceive: ACTION_USB_DEVICE_DETACHED ")
                    intentAction.putExtra(CONNECTED_KEY, false)

                }

                ACTION_USB_PERMISSION -> {
                    Log.d(TAG, "onReceive: ACTION_USB_PERMISSION")
                }

            }
        }
    }

    private fun requestUsbPermission(context: Context, device: UsbDevice) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val permissionIntent = PendingIntent.getBroadcast(
            context, 0, Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        )
        usbManager.requestPermission(device, permissionIntent)

    }

    fun getDevice(): UsbDevice? {
        val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intentUSB?.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
        } else {
            intentUSB?.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        }
   return  device }

    companion object {
        const val ACTION_USB_PERMISSION = "com.example.USB_PERMISSION"
    }
}