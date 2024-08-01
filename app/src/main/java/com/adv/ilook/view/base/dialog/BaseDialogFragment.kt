package com.adv.ilook.view.base.dialog

import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnCancelListener
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.adv.ilook.model.util.receiver.UsbReceiver.Companion.ACTION_USB_PERMISSION
import com.adv.ilook.view.ui.activities.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "==>>UsbPermDialog"

class UsbPermissionDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_DEVICE_NAME = "device_name"
        private const val ARG_DEVICE = "usb_device"

        fun newInstance(deviceName: String, device: UsbDevice): UsbPermissionDialogFragment {
            val fragment = UsbPermissionDialogFragment()
            val args = Bundle().apply {
                putString(ARG_DEVICE_NAME, deviceName)
                putParcelable(ARG_DEVICE, device)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val deviceName = arguments?.getString(ARG_DEVICE_NAME) ?: ""
        val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_DEVICE, UsbDevice::class.java)
        } else {
            arguments?.getParcelable(ARG_DEVICE)
        }

        val dialogMaterial = MaterialAlertDialogBuilder(requireContext())
            .setTitle("SeeForMe")
            .setMessage("Choose an app to open the USB device: $deviceName")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                device?.let {
                    requestUsbPermission(it)
                } ?: run {
                    Toast.makeText(requireContext(), "Device not found", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()
        dialogMaterial.setCanceledOnTouchOutside(false)
        return dialogMaterial
    }

    private fun requestUsbPermission(device: UsbDevice) {
        val usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        val permissionIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        )

        val hasPermission = usbManager.hasPermission(device)
        if (hasPermission) {
            Toast.makeText(requireContext(), "Permission already granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            usbManager.requestPermission(device, permissionIntent)
            Toast.makeText(requireContext(), "Requesting permission", Toast.LENGTH_SHORT).show()
        }
    }


}
