package com.example.officeapp.Utils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

// Define a callback interface for handling API responses

class ApiService {
    companion object {

        private val client  = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES) // Connection timeout
            .readTimeout(1, TimeUnit.MINUTES)    // Read timeout
            .writeTimeout(1, TimeUnit.MINUTES)   // Write timeout
            .build()

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
//                        // Add file to the multipart body
//                        val fileRequestBody = value.asRequestBody("image/*".toMediaTypeOrNull())
//                        Log.d("TAG", "imagefile $key $value")
//                        multipartBodyBuilder.addFormDataPart(key, value.name, fileRequestBody)

                        if (value.exists()) {
                            Log.d("TAG", "File exists: ${value.absolutePath}, size: ${value.length()} bytes")
                            val fileRequestBody = value.asRequestBody("image/*".toMediaTypeOrNull())

                            multipartBodyBuilder.addFormDataPart(key, value.name, fileRequestBody)
                        } else {
                            Log.e("TAG", "File not found: ${value.absolutePath}")
                        }
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


        suspend fun postMultiPart(
            url: String,
            headers: Map<String, String>? = null,
            parameters: Map<String, String>,
            image: File?,
            imageParamName: String
        ): Result<String> = withContext(Dispatchers.IO) {
            val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

            // Add form data parameters
            parameters.forEach { (key, value) ->
                multipartBodyBuilder.addFormDataPart(key, value)
            }

            // Add image if provided and compress it
            if (image != null) {
                val compressedImage = compressImage(image)
                val imageBody = compressedImage.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBodyBuilder.addFormDataPart(imageParamName, compressedImage.name, imageBody)
            }

            val multipartBody = multipartBodyBuilder.build()

            val requestBuilder = Request.Builder()
                .url(url)
                .post(multipartBody)

            headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            val request = requestBuilder.build()
            try {
                val response = client.newCall(request).execute()
                return@withContext if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "Empty response")
                } else {
                    val errorBody = response.body?.string() ?: "No error details"
                    Log.d("Tag", "Error: ${response.code}. Details: $errorBody")
                    Result.failure(IOException("Error: ${response.code}, Details: $errorBody"))
                }
            } catch (e: Exception) {
                Log.d("Tag", "Exception: ${e.message}")
                Result.failure(e)
            }
        }

        // Function to compress the image to stay under 2MB
        fun compressImage(imageFile: File, maxSizeKB: Int = 2048): File {
            val bitmap = BitmapFactory.decodeFile(imageFile.path)
            var stream = ByteArrayOutputStream()

            // Compress to reduce size (adjust quality as needed)
            var quality = 90
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

            // Reduce quality further if necessary to keep file under 2MB
            while (stream.toByteArray().size / 1024 > maxSizeKB && quality > 10) {
                stream = ByteArrayOutputStream() // Reset stream
                quality -= 10
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            }

            // Write the compressed image to a new file
            val compressedImageFile = File(imageFile.parent, "compressed_" + imageFile.name)
            val fileOutputStream = FileOutputStream(compressedImageFile)
            fileOutputStream.write(stream.toByteArray())
            fileOutputStream.flush()
            fileOutputStream.close()

            return compressedImageFile
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
