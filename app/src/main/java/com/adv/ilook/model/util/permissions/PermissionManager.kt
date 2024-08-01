package com.adv.ilook.model.util.permissions

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.adv.ilook.R


import java.lang.ref.WeakReference

final class PermissionManager private constructor(private val fragment: WeakReference<Fragment>) {
    private  val TAG = "xxxPermissionManager"
    private val requiredPermissions = mutableListOf<Permission>()
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<Permission,Boolean>) -> Unit = {}

    private val permissionCheck =
        fragment.get()?.registerForActivityResult(RequestMultiplePermissions()) { grantResults ->
            Log.d(TAG, "null() called with: grantResults keys= ${grantResults.keys} values= ${grantResults.values}")
            sendResultAndCleanUp(grantResults)
        }

    fun check(activity: AppCompatActivity){
        activity.registerForActivityResult(RequestMultiplePermissions()) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }
    }
    companion object {
        fun from(fragment: Fragment) = PermissionManager(WeakReference(fragment))
    }

    fun rationale(description: String): PermissionManager {
        rationale = description
        return this
    }

    fun request(vararg permission: Permission): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    fun checkPermission(callback: (Boolean) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun checkDetailedPermission(callback: (Map<Permission,Boolean>) -> Unit) {
        this.detailedCallback = callback
        handlePermissionRequest()
    }

    private fun handlePermissionRequest() {
        fragment.get()?.let { fragment ->

            when {
                areAllPermissionsGranted(fragment) -> {
                    Log.d(TAG, "handlePermissionRequest: sendPositiveResult()")
                    sendPositiveResult()
                }
                shouldShowPermissionRationale(fragment) ->{
                    Log.d(TAG, "handlePermissionRequest: displayRationale()")
                    displayRationale(fragment)
                }
                else -> {
                    Log.d(TAG, "handlePermissionRequest: requestPermissions()")
                    requestPermissions()
                }
            }
        }
    }

    private fun displayRationale(fragment: Fragment) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle(fragment.getString(R.string.dialog_permission_title))
            .setMessage(rationale ?: fragment.getString(R.string.dialog_permission_default_message))
            .setCancelable(false)
            .setPositiveButton(fragment.getString(R.string.dialog_permission_button_positive)) { _, _ ->
                requestPermissions()
            }
            .show()
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associateWith {
            Log.d(TAG, "sendPositiveResult: $it")
            true
        })
    }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all {
            Log.d(TAG, "sendResultAndCleanUp() called key=${it.key} value=${it.value}")
            it.value
        })
        detailedCallback(
            grantResults.mapKeys { Permission.from(it.key)
            })
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun requestPermissions() {
        permissionCheck?.launch(getPermissionList())
    }

    private fun areAllPermissionsGranted(fragment: Fragment) =
        requiredPermissions.all { it.isGranted(fragment) }

    private fun shouldShowPermissionRationale(fragment: Fragment) =
        requiredPermissions.any {
            it.requiresRationale(fragment)
        }


    private fun getPermissionList() =
        requiredPermissions.flatMap {
            Log.d(TAG, "getPermissionList() called ${it.permissions}")
            it.permissions.toList()
        }.toTypedArray()

    private fun Permission.isGranted(fragment: Fragment) =
        permissions.all { hasPermission(fragment, it) }

    private fun Permission.requiresRationale(fragment: Fragment) =
        permissions.any { fragment.shouldShowRequestPermissionRationale(it) }

    private fun hasPermission(fragment: Fragment, permission: String) =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
}