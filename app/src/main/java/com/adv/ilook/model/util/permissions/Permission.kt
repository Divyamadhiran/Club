package com.adv.ilook.model.util.permissions

import android.Manifest.permission.*
import android.Manifest.permission_group.MICROPHONE

sealed class Permission(vararg val permissions: String) {
    // Individual permissions
    data object Camera : Permission(CAMERA)

    // Bundled permissions
    data object MandatoryForFeatureOne : Permission(WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)

    // Grouped permissions
    data object Location : Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
        ACCESS_BACKGROUND_LOCATION)
    data object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,MANAGE_EXTERNAL_STORAGE)
    data object Bluetooth : Permission( BLUETOOTH_ADMIN,BLUETOOTH)
    data object Microphone : Permission( MICROPHONE, FOREGROUND_SERVICE_MICROPHONE)
    data object HS_Permissions: Permission(
        BLUETOOTH_ADMIN,BLUETOOTH,
        ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION,
       CAMERA,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,
       MICROPHONE, FOREGROUND_SERVICE_MICROPHONE,ACCESS_BACKGROUND_LOCATION
    )


    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,ACCESS_BACKGROUND_LOCATION -> Location
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera
            BLUETOOTH_ADMIN,BLUETOOTH -> Bluetooth
            MICROPHONE, FOREGROUND_SERVICE_MICROPHONE -> Microphone
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}