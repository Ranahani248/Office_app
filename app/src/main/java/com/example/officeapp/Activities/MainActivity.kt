package com.example.officeapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.officeapp.R
import com.example.officeapp.Utils.LocationHelper
import com.example.officeapp.Utils.showCustomToast
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    var locationHelper: LocationHelper? =null ;
    val minLongitude = 73.0799669
    val maxLongitude = 73.0829669
    val minLatitude = 33.6494625
    val maxLatitude = 33.6534625

    var fetch: Button? = null;
    var welcome: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetch =findViewById(R.id.fetch);
        welcome = findViewById(R.id.welcome)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // Set the welcome message based on the time of day
        when (hour) {
            in 0..5 -> welcome?.text = "Good Night!"
            in 6..11 -> welcome?.text = "Good Morning!"
            in 12..16 -> welcome?.text = "Good Afternoon!"
            in 17..19 -> welcome?.text = "Good Evening!"
            else -> welcome?.text = "Good Night!"
        }


        locationHelper = LocationHelper(this)

        fetch?.setOnClickListener() {
            fetchLocation()
        }
}
fun fetchLocation(){
    locationHelper?.fetchLocation { location ->
        if (location != null) {
            // Use the location data
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d("TAG", "$latitude   $longitude ")
            checkIfUserIsInArea(longitude, latitude)
        } else {
            Log.d("TAG", "Failed  to fetch")
        }
    }
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LocationHelper.REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // User agreed to change location settings
                if (locationHelper?.isLocationEnabled(this) == true) {
                    fetchLocation()
                } else {
                    Toast(this).showCustomToast(this, "Location Services are Unavailable, Please Try Again", R.color.yellow)
                }
            } else {
                // User did not agree to change location settings
                Toast(this).showCustomToast(this, "Location Services are Required", R.color.red)
            }
        }
    }

    fun checkIfUserIsInArea(paramLongitude: Double, paramLatitude: Double) {
        if (paramLongitude > minLongitude && paramLongitude < maxLongitude &&
            paramLatitude > minLatitude && paramLatitude < maxLatitude) {
            Log.d("Tag","User is in the area")

            Toast(this).showCustomToast(this, "You are in Location area", R.color.green)
        } else {
            Toast(this).showCustomToast(this, "You are out of Location area", R.color.red)
            Log.d("Tag","User is not IN the area")
        }
    }

}