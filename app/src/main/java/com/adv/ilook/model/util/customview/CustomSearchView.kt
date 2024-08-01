package com.adv.ilook.model.util.customview


import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SearchView

class CustomSearchView(context: Context, attrs: AttributeSet?) : SearchView(context, attrs) {

    init {
        // Customize the underline color here
        try {
            val searchPlate = this.findViewById<View>(androidx.appcompat.R.id.search_plate)
            searchPlate.setBackgroundColor(Color.TRANSPARENT) // Set your desired color
            val searchEdit = this.findViewById<View>(androidx.appcompat.R.id.search_src_text)
            searchEdit.setBackgroundColor(Color.TRANSPARENT) // Set your desired color
            this.queryHint ="jhdszhskdhk"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}