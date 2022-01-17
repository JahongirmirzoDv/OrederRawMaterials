package com.example.orderrawmaterials.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager

object Statusbar {
    fun startStatus(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                activity.window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            }
        }
    }
}