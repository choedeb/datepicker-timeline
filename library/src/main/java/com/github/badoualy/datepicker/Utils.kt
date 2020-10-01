package com.github.badoualy.datepicker

import android.content.Context
import android.os.Build
import android.util.TypedValue
import androidx.core.content.ContextCompat

internal object Utils {

    @JvmStatic
    fun getPrimaryColor(context: Context): Int {
        var color = context.resources.getIdentifier("colorPrimary", "attr", context.packageName)
        when {
            color != 0 -> {
                // If using support library v7 primaryColor
                val t = TypedValue()
                context.theme.resolveAttribute(color, t, true)
                color = t.data
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                // If using native primaryColor (SDK >21)
                val t = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimary))
                color = t.getColor(0, ContextCompat.getColor(context, R.color.mti_default_primary))
                t.recycle()
            }
            else -> {
                val t = context.obtainStyledAttributes(intArrayOf(R.attr.colorPrimary))
                color = t.getColor(0, ContextCompat.getColor(context, R.color.mti_default_primary))
                t.recycle()
            }
        }
        return color
    }

    @JvmStatic
    fun getPrimaryDarkColor(context: Context): Int {
        var color = context.resources.getIdentifier("colorPrimaryDark", "attr", context.packageName)
        when {
            color != 0 -> {
                // If using support library v7 primaryColor
                val t = TypedValue()
                context.theme.resolveAttribute(color, t, true)
                color = t.data
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                // If using native primaryColor (SDK >21)
                val t = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimaryDark))
                color = t.getColor(0, ContextCompat.getColor(context, R.color.mti_default_primary_dark))
                t.recycle()
            }
            else -> {
                val t = context.obtainStyledAttributes(intArrayOf(R.attr.colorPrimaryDark))
                color = t.getColor(0, ContextCompat.getColor(context, R.color.mti_default_primary_dark))
                t.recycle()
            }
        }
        return color
    }
}