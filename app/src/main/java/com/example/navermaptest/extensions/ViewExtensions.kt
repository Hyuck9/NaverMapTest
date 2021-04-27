package com.example.navermaptest.extensions

import android.view.View


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone(shouldBeGone: Boolean) {
    if (shouldBeGone) visibility = View.GONE
    else visible()
}

fun View.onClick(action: (View) -> Unit) {
    setOnClickListener(action)
}