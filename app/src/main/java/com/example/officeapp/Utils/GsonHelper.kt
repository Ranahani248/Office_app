package com.example.officeapp.Utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

object GsonHelper {

    val gson = Gson()

    // Serialize an object to JSON
    fun serializeToJson(obj: Any): String? {
        return try {
            gson.toJson(obj)
        } catch (e: Exception) {
            // Log or handle the exception as needed
            null
        }
    }

    // Deserialize JSON to a specific class
     inline fun <reified T> deserializeFromJson(json: String): T? {
        return try {
            gson.fromJson(json, T::class.java)
        } catch (e: JsonSyntaxException) {
            // Log or handle the exception as needed
            null
        }
    }
}
