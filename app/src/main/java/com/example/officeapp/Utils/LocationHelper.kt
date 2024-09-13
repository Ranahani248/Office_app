package com.example.officeapp.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.officeapp.Activities.MainActivity
import com.example.officeapp.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class LocationHelper(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private var locationResultCallback: ((Location?) -> Unit)? = null

    // Registers permission request launcher
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        (context as androidx.fragment.app.FragmentActivity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false ||
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (isLocationGranted) {
                // Proceed with checking location settings and fetching the location
                locationResultCallback?.let { checkLocationSettingsAndFetchLocation(it) }
            } else {
                // Handle the case where permissions are denied
                onLocationDenied()
            }
        }

    // Method to start fetching location
    fun fetchLocation(onLocationResult: (Location?) -> Unit) {
        Log.d("TAG", "fetching location")
        locationResultCallback = onLocationResult // Save the callback for later use
        if (!hasLocationPermission()) {
            requestLocationPermissions()
        } else {
            checkLocationSettingsAndFetchLocation(onLocationResult)
        }
    }

    private fun checkLocationSettingsAndFetchLocation(onLocationResult: (Location?) -> Unit) {
        Log.d("TAG", "checking location settings")


        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(true)
            .setMaxUpdates(1)
            .build()

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                requestLocationUpdates(locationRequest, onLocationResult)
                Log.d("TAG", "location settings are satisfied")
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException
                ) {
                    Log.d("TAG", "location settings are not satisfied")
                    try {

                        if(isLocationEnabled(context)){
                            val activity : MainActivity = context as MainActivity
                            Toast(context).showCustomToast(activity, "Turn Off Mocked Location And retry", R.color.red)
                        }
                        else{
                            exception.startResolutionForResult(
                                context as androidx.fragment.app.FragmentActivity,
                                REQUEST_CHECK_SETTINGS
                            )
                        }

                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.d("TAG", "Failed to start resolution for location settings")
                        onLocationResult(null)
                    }
                } else {
                    // Handle other failures
                    Log.d("TAG", "Location settings check failed")
                    onLocationResult(null)
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(locationRequest: LocationRequest, onLocationResult: (Location?) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null ) {
                    onLocationResult(location)
                } else {
                    Log.d("TAG", "Mock location detected or location is null")
                    onLocationResult(null)
                }
                stopLocationUpdates() // Stop updates after getting the location
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null // Clear callback to avoid multiple triggers
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

        return context.checkSelfPermission(fineLocation) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(coarseLocation) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun onLocationDenied() {
        // Handle the case where the location permission is denied by the user
        locationResultCallback?.invoke(null)
        Log.d("TAG", "Location permission denied")
    }


    companion object {
        const val REQUEST_CHECK_SETTINGS = 1001
    }
}
