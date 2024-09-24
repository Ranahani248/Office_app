package com.example.officeapp.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "AppPreferences"
    }

    // Function to save a string value with a custom preference name
    fun saveString(preferenceName: String, key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString("${preferenceName}_$key", value)
            apply()
        }
    }

    // Function to save an integer value with a custom preference name
    fun saveInt(preferenceName: String, key: String, value: Int) {
        with(sharedPreferences.edit()) {
            putInt("${preferenceName}_$key", value)
            apply()
        }
    }

    // Function to save a boolean value with a custom preference name
    fun saveBoolean(preferenceName: String, key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("${preferenceName}_$key", value)
            apply()
        }
    }

    // Function to save a float value with a custom preference name
    fun saveFloat(preferenceName: String, key: String, value: Float) {
        with(sharedPreferences.edit()) {
            putFloat("${preferenceName}_$key", value)
            apply()
        }
    }

    // Function to save a long value with a custom preference name
    fun saveLong(preferenceName: String, key: String, value: Long) {
        with(sharedPreferences.edit()) {
            putLong("${preferenceName}_$key", value)
            apply()
        }
    }

    // Function to fetch a string value with a custom preference name
    fun getString(preferenceName: String, key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString("${preferenceName}_$key", defaultValue)
    }

    // Function to fetch an integer value with a custom preference name
    fun getInt(preferenceName: String, key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt("${preferenceName}_$key", defaultValue)
    }

    // Function to fetch a boolean value with a custom preference name
    fun getBoolean(preferenceName: String, key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean("${preferenceName}_$key", defaultValue)
    }

    // Function to fetch a float value with a custom preference name
    fun getFloat(preferenceName: String, key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat("${preferenceName}_$key", defaultValue)
    }

    // Function to fetch a long value with a custom preference name
    fun getLong(preferenceName: String, key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong("${preferenceName}_$key", defaultValue)
    }

    // Function to clear a specific key with a custom preference name
    fun clearKey(preferenceName: String, key: String) {
        with(sharedPreferences.edit()) {
            remove("${preferenceName}_$key")
            apply()
        }
    }

    fun clearAll(preferenceName: String) {
        with(sharedPreferences.edit()) {
            sharedPreferences.all.keys
                .filter { it.startsWith("${preferenceName}_") }  // Correct usage of underscore
                .forEach { remove(it) }
            apply()
        }
    }
}
