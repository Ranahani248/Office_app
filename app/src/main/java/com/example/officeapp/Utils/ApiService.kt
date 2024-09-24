package com.example.officeapp.Utils
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

// Define a callback interface for handling API responses

class ApiService {
    companion object {

        private val client = OkHttpClient()

        // Function for GET request with Coroutine
        suspend fun get(
            url: String,
            headers: Map<String, String>? = null
        ): Result<String> = withContext(Dispatchers.IO) {
            val requestBuilder = Request.Builder().url(url)

            headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            val request = requestBuilder.build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "Empty response")
                } else {
                    Result.failure(IOException("Error: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        // Function for POST request with Coroutine
        suspend fun post(
            url: String,
            headers: Map<String, String>? = null,
            parameters: Map<String, Any> // Change to Any to support file and string types
        ): Result<String> = withContext(Dispatchers.IO) {
            val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

            parameters.forEach { (key, value) ->
                when (value) {
                    is File -> {
                        // Add file to the multipart body
                        val fileRequestBody = value.asRequestBody("image/png".toMediaTypeOrNull())
                        multipartBodyBuilder.addFormDataPart(key, value.name, fileRequestBody)
                    }
                    is String -> {
                        // Add text to the multipart body
                        multipartBodyBuilder.addFormDataPart(key, value)
                    }
                    else -> {
                        // Handle other data types if needed
                    }
                }
            }

            val multipartBody = multipartBodyBuilder.build()

            val requestBuilder = Request.Builder()
                .url(url)
                .post(multipartBody)

            headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            Log.d("Request", "URL: $url")
            headers?.forEach { (key, value) -> Log.d("Header", "$key: $value") }



            val request = requestBuilder.build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "Empty response")
                } else {
                    Result.failure(IOException("Error: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


        // Function for PUT request with Coroutine
        suspend fun put(
            url: String,
            headers: Map<String, String>? = null,
            parameters: Map<String, String>
        ): Result<String> = withContext(Dispatchers.IO) {
            val formBodyBuilder = FormBody.Builder()
            parameters.forEach { (key, value) -> formBodyBuilder.add(key, value) }
            val formBody = formBodyBuilder.build()

            val requestBuilder = Request.Builder()
                .url(url)
                .put(formBody)

            headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            val request = requestBuilder.build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "Empty response")
                } else {
                    Result.failure(IOException("Error: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        // Function for DELETE request with Coroutine
        suspend fun delete(
            url: String,
            headers: Map<String, String>? = null
        ): Result<String> = withContext(Dispatchers.IO) {
            val requestBuilder = Request.Builder()
                .url(url)
                .delete()

            headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            val request = requestBuilder.build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "Empty response")
                } else {
                    Result.failure(IOException("Error: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
