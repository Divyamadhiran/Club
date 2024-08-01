package com.adv.ilook.model.util.customview

import android.content.Context
import android.content.SharedPreferences
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.view.Window
import android.view.WindowInsets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.internal.EdgeToEdgeUtils



/** Helper that saves the current window preferences for the Catalog.  */
class WindowPreferencesManager(private val context: Context) {
    private val listener =
        OnApplyWindowInsetsListener { v: View, insets: WindowInsetsCompat ->
            var leftInset = insets.stableInsetLeft
            var rightInset = insets.stableInsetRight
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                leftInset = insets.getInsets(WindowInsets.Type.systemBars()).left
                rightInset = insets.getInsets(WindowInsets.Type.systemBars()).right
            }

            v.setPadding(leftInset, 0, rightInset, 0)
            insets
        }

    fun toggleEdgeToEdgeEnabled() {
        sharedPreferences
            .edit()
            .putBoolean(KEY_EDGE_TO_EDGE_ENABLED, !isEdgeToEdgeEnabled)
            .commit()
    }

    val isEdgeToEdgeEnabled: Boolean
        get() = sharedPreferences
            .getBoolean(KEY_EDGE_TO_EDGE_ENABLED, VERSION.SDK_INT >= VERSION_CODES.Q)

    fun applyEdgeToEdgePreference(window: Window) {
        EdgeToEdgeUtils.applyEdgeToEdge(window, isEdgeToEdgeEnabled)
        ViewCompat.setOnApplyWindowInsetsListener(
            window.decorView, if (isEdgeToEdgeEnabled) listener else null
        )
    }

    private val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "window_preferences"
        private const val KEY_EDGE_TO_EDGE_ENABLED = "edge_to_edge_enabled"
    }
}