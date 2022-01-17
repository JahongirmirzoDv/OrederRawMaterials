package com.example.orderrawmaterials.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPref {
    lateinit var sharedPreferences: SharedPreferences

    fun getInstanceDis(context: Context) {
        sharedPreferences = context.getSharedPreferences(
            "" +
                    "", MODE_PRIVATE
        )
    }

    var user: String?
        get() = sharedPreferences.getString("user", null)
        set(value) = sharedPreferences.edit {
            if (value != null) {
                this.putString("user", value)
            }
        }
}