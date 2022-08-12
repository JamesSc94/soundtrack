package com.jamessc94.soundtrack.media.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.inflate(@LayoutRes layout: Int): T {
    return LayoutInflater.from(context).inflate(layout, this, false) as T
}

fun <T : ViewDataBinding> ViewGroup.inflateWithBinding(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): T {
    val layoutInflater = LayoutInflater.from(context)
    return DataBindingUtil.inflate(layoutInflater, layoutRes, this, attachToRoot) as T
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showOrHide(show: Boolean) = if (show) show() else hide()