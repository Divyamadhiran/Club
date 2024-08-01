package com.adv.ilook.view.base

import android.view.View
import androidx.viewbinding.ViewBinding

interface PermissionListener {
    fun onRequestPermissionListener(view: ViewBinding ,
                                    listOfPermission: ArrayList<String>,
                                    success: (Boolean) -> Unit)
}