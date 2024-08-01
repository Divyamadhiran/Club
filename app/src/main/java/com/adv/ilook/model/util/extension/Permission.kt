package com.adv.ilook.model.util.extension

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.internal.format

// Permission request codes
const val REQUEST_CODE_CAMERA = 100
const val REQUEST_CODE_BLUETOOTH = 101
const val REQUEST_CODE_USB = 102
const val REQUEST_CODE_CAMERA_MICROPHONE = 103
const val REQUEST_CODE_LOCATION = 104
const val REQUEST_CODE_BACKGROUND_LOCATION = 105
const val REQUEST_CODE_ALL = 106
const val REQUEST_CODE_NOTIFICATION = 107
const val REQUEST_CODE_SCREEN_CAPTURE = 108
const val REQUEST_CODE_SMS = 109

private const val TAG = "==>>Permission"
fun AppCompatActivity.hasPermission(
    context: Context, permission: List<String>, success: (result: Boolean) -> Unit
) {
    val getResult = permission.map {
        ActivityCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }
    Log.d(TAG, "hasPermission: getResults : ${getResult}")
    for (per in permission) Log.d(TAG, "hasPermission: permission: $per")
    for (result in getResult) success(result)
}

// Request Bluetooth permission
fun AppCompatActivity.requestBluetoothPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    var permissionManifest: List<String> = listOf(BLUETOOTH)
    permissionManifest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            BLUETOOTH_SCAN,
            BLUETOOTH_CONNECT,
            BLUETOOTH_ADVERTISE
        )
    } else {
        listOf(BLUETOOTH, BLUETOOTH_ADMIN)
    }
    hasPermission(activity, permissionManifest) { isGranted ->
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_BLUETOOTH
            )
            success(false)
        } else {
            success(true)
        }

    }
}

// Request USB permission
fun AppCompatActivity.requestUsbPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    var permissionManifest = mutableListOf("")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        permissionManifest.add(MANAGE_EXTERNAL_STORAGE)
    else {
        permissionManifest.add(WRITE_EXTERNAL_STORAGE)
        permissionManifest.add(READ_EXTERNAL_STORAGE)
    }
    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_USB
            )
            success(false)
        } else {
            success(true)
        }
    }
}

fun AppCompatActivity.requestNotificationPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    val permissionManifest = mutableListOf(FOREGROUND_SERVICE)
    if (Build.VERSION.SDK_INT >= 33) {
        permissionManifest.add(POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionManifest.add(FOREGROUND_SERVICE_MEDIA_PROJECTION)
            /*  permissionManifest.add("android.permission.PROJECT_MEDIA")
              permissionManifest.add("android.permission.CAPTURE_VIDEO_OUTPUT")*/
        }
    }

    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_NOTIFICATION
            )
            success(false)
        } else {
            success(true)
        }
    }
}


// Request Microphone permission
fun AppCompatActivity.requestCameraMicrophonePermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    val permissionManifest =
        mutableListOf(RECORD_AUDIO, CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        permissionManifest.add(FOREGROUND_SERVICE_MICROPHONE)
        permissionManifest.add(FOREGROUND_SERVICE_CAMERA)
    }
    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_CAMERA_MICROPHONE
            )
            success(false)
        } else {
            success(true)
        }

    }
}

fun AppCompatActivity.requestCameraPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    hasPermission(activity, listOf(CAMERA)) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(CAMERA), REQUEST_CODE_CAMERA
            )
            success(false)
        } else {
            success(true)
        }

    }
}

fun AppCompatActivity.requestLocationPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    hasPermission(activity, listOf(Manifest.permission.ACCESS_FINE_LOCATION)) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION
            )
            success(false)
        } else {

            success(true)
        }

    }
}

fun AppCompatActivity.requestBackgroundLocationPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    val permissionManifest =
        mutableListOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        permissionManifest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            permissionManifest.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        hasPermission(activity, permissionManifest) {
            if (!it) {
                ActivityCompat.requestPermissions(
                    activity,
                    permissionManifest.toTypedArray(),
                    REQUEST_CODE_BACKGROUND_LOCATION
                )
                success(false)
            } else {
                success(true)
            }

        }
    }
}

fun AppCompatActivity.requestSMSPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    val permissionManifest =
        mutableListOf(READ_SMS, RECEIVE_SMS)
    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity,
                permissionManifest.toTypedArray(),
                REQUEST_CODE_SMS
            )
            success(false)
        } else {
            success(true)
        }
    }


}

fun AppCompatActivity.requestAllPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    val permissionManifest: MutableList<String> = mutableListOf<String>()
    permissionManifest.add(CAMERA)
    permissionManifest.add(RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        permissionManifest.add(FOREGROUND_SERVICE_MICROPHONE)
    }
    permissionManifest.add(BLUETOOTH)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissionManifest.add(BLUETOOTH_SCAN)
        permissionManifest.add(BLUETOOTH_ADVERTISE)
    }
    permissionManifest.add(ACCESS_FINE_LOCATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissionManifest.add(ACCESS_BACKGROUND_LOCATION)
    }

    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity,
                permissionManifest.toTypedArray(),
                REQUEST_CODE_ALL
            )
            success(false)
        } else {
            success(true)
        }

    }
}