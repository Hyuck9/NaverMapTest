package com.example.navermaptest.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.example.navermaptest.extensions.gone

object ViewBinding {

    @JvmStatic
    @BindingAdapter("gone")
    fun bindGone(view: View, shouldBeGone: Boolean?) {
        if (shouldBeGone == true) {
            view.gone(true)
        } else {
            view.gone(false)
        }
    }
}