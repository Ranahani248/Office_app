package com.example.officeapp.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.officeapp.Activities.MainActivity
import com.example.officeapp.R
import com.example.officeapp.Utils.ApiLinks
import com.example.officeapp.Utils.ApiService
import com.example.officeapp.Utils.GsonHelper
import com.example.officeapp.Utils.showCustomToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Objects

class ProfileFragment : Fragment() {
    lateinit var name: TextView
    lateinit var email: TextView
    lateinit var designation: TextView
    lateinit var mobile: EditText
    lateinit var address: EditText
    lateinit var nameEditText: EditText
    lateinit var image: CircleImageView
     var imageUri : Uri? = null
    var progresslayout : androidx.constraintlayout.widget.ConstraintLayout? = null
    var loadingText : TextView? = null
    var handler: Handler? = null
    var dotCount = 0
    lateinit var updateButton : AppCompatButton

    private val PERMISSION_REQUEST_CODE = 101
    private val IMAGE_PICK_REQUEST_CODE = 100
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>


    lateinit var activity1 :MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            val view = inflater.inflate(R.layout.fragment_profile, container, false)

        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)


        image = view.findViewById(R.id.profile_image)
        designation = view.findViewById(R.id.designation)
        mobile = view.findViewById(R.id.numberEditText)
        address = view.findViewById(R.id.addressEditText)
        nameEditText = view.findViewById(R.id.nameEditText)
        updateButton = view.findViewById(R.id.updateButton)
        progresslayout = view.findViewById(R.id.progresslayout)
        loadingText = view.findViewById(R.id.loadingText)

        updateData()


        activity1 = requireActivity() as MainActivity
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                openImagePicker()
            } else {
                Toast(requireContext()).showCustomToast(requireActivity(), "Permission Denied", R.color.yellow)
            }
        }

        // Initialize Image Picker Launcher
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Directly get the URI of the selected image
                val uri = result.data?.data
                if (uri != null) {
                    imageUri = uri

                    Glide.with(this)
                        .load(uri)
                        .into(image)
                } else {
                    // Handle the case where no image was selected
                    Toast(requireContext()).showCustomToast(requireActivity(), "No image selected", R.color.yellow)
                }
            }
        }



        image.setOnClickListener {

            pickImageFromGallery()

        }
        updateButton.setOnClickListener {
            update()
        }


        return view
    }


    private fun pickImageFromGallery() {
        if (checkPermissions()) {
            openImagePicker()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Check for READ_MEDIA_VISUAL_USER_SELECTED for Android 14+
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Check for READ_MEDIA_IMAGES for Android 13+
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                // Check for READ_EXTERNAL_STORAGE for older versions
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun requestPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Request READ_MEDIA_VISUAL_USER_SELECTED for Android 14+
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Request READ_MEDIA_IMAGES for Android 13+
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
            else -> {
                // Request READ_EXTERNAL_STORAGE for older versions
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }



    private fun openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Use the Photo Picker for Android 14 and above to select a single image
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imagePickerLauncher.launch(intent)
        } else {
            // Use traditional picker for older versions
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }
    }






    fun update() {

        if(nameEditText.text.toString().isEmpty() || mobile.text.toString().isEmpty() || address.text.toString().isEmpty() ){
            Toast(activity).showCustomToast(
                requireActivity(),
                "Please enter all details",
                R.color.yellow
            )
            return
        }
        progress(true)
        val headers = mutableMapOf<String, String>()
        headers["Authorization"] = "123"
//        headers["User-Agent"] = "PostmanRuntime/7.42.0"
//        headers["Accept"] = "*/*"
//        headers["access-control-allow-origin"] = "*"
        val parameters = mutableMapOf<String, String>()

        parameters["name"] = nameEditText.text.toString()
        parameters["phone"] = mobile.text.toString()
        parameters["address"] = address.text.toString()
        var imageFile : File? = null
        if (imageUri != null) {
            val filepath = getPathFromUri(imageUri!!)
            val imageFile1 = filepath?.let { File(it) }

            if (imageFile1 != null && imageFile1.exists()) {
                imageFile = imageFile1 // Pass the File object, not the file path
            } else {
                Log.d("File Does not exist", "File not found")
            }
        }



        Log.d("Tag", parameters.toString())


        val activity = requireActivity()
        // Use CoroutineScope to launch the network call
        CoroutineScope(Dispatchers.Main).launch {
            // Perform the network request on IO dispatcher
            val response = withContext(Dispatchers.IO) {
                ApiService.postMultiPart(ApiLinks.PROFILE_URL , headers, parameters,imageFile,"image")
            }

            // Handle the response
            response.onSuccess { responseString ->
                // Update UI on the Main thread
                Toast(activity).showCustomToast(
                    activity,
                    "Profile Updated Successfully",
                    R.color.green
                )
                activity1.fetchData()
                if(activity1.homeFragment.isAdded){
                    activity1.homeFragment.NAME?.text  = nameEditText.text.toString()
                }
                if(activity1.announcementFragment.isAdded){
                    activity1.announcementFragment.NAME?.text  = nameEditText.text.toString()
                }
                MainActivity.NAME = nameEditText.text.toString()

                progress(false)


            }.onFailure { error ->
                // Update UI on the Main thread
                Toast(activity).showCustomToast(activity, "Profile Update Failed $error", R.color.red)
                Log.d("Tag", "Profile request failed: $error")
                progress(false)

            }
        }
    }
    private fun getPathFromUri(uri: Uri): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 (API 29) and above, use a stream to handle the file
            requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                val file = File(requireActivity().cacheDir, "temp_image_file")
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                file.absolutePath
            }
        } else {
            // For Android 9 and below, use the traditional way of getting file path
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (cursor.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
            null
        }
    }



    fun updateData(){
        activity1 = activity as MainActivity
        if(activity1.name1.isNotEmpty()) {
            name.setText(activity1.name1)
            nameEditText.setText(activity1.name1)
        }
        if(activity1.email1.isNotEmpty()) {
            email.setText(activity1.email1)
        }
        if(activity1.designation1.isNotEmpty()) {
            designation.setText(activity1.designation1)
        }
        if(activity1.mobile1.isNotEmpty()) {
            mobile.setText(activity1.mobile1)
        }
        if(activity1.address1.isNotEmpty()) {
            address.setText(activity1.address1)
        }
        if(activity1.imageUrl.isNotEmpty()) {
            Glide.with(this).load(activity1.imageUrl).into(image)
        }
    }

    fun progress(progress: Boolean) {
        if (progress) {
            progresslayout?.visibility = android.view.View.VISIBLE

            // Create a handler for updating text
            handler = Handler(Looper.getMainLooper())
            handler?.post(object : Runnable {
                override fun run() {
                    val dots = ".".repeat(dotCount % 4) // Add up to 3 dots
                    loadingText?.text = "Updating$dots"
                    dotCount++
                    if (progress) {
                        handler?.postDelayed(this, 500L) // Update every 500ms
                    }
                }
            })
        } else {
            progresslayout?.visibility = android.view.View.GONE
            handler?.removeCallbacksAndMessages(null)
        }
    }

}