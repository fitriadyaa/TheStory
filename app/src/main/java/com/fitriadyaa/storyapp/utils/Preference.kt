package com.fitriadyaa.storyapp.utils

import android.content.Context
import android.content.SharedPreferences

object Preference {
    private const val PREF_NAME = "onSignIn"
    private const val KEY_TOKEN = "token"
    private const val KEY_STATUS = "status"

    fun initPref(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String, context: Context) {
        val editor = editorPreference(context)
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun logOut(context: Context) {
        val editor = editorPreference(context)
        editor.remove(KEY_TOKEN)
        editor.remove(KEY_STATUS)
        editor.apply()
    }

    private fun editorPreference(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
    }
}
