package com.example.officeapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.officeapp.Activities.AttendanceActivity
import com.example.officeapp.Activities.HolidaysActivity
import com.example.officeapp.Activities.MainActivity
import com.example.officeapp.R
import com.example.officeapp.Utils.LocationHelper
import com.example.officeapp.Utils.showCustomToast
import java.util.*

class HomeFragment : Fragment() {

    private var locationHelper: LocationHelper? = null
    private val minLongitude = 73.0799669
    private val maxLongitude = 73.0829669
    private val minLatitude = 33.6494625
    private val maxLatitude = 33.6534625

    private var attendanceButton: LinearLayout? = null
    private var announcementButton: LinearLayout? = null
    private  var holidaysButton: LinearLayout? = null
    private  var leavesButton: LinearLayout?= null

    private var fetch: Button? = null
    private var welcome: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetch = view.findViewById(R.id.fetch)
        welcome = view.findViewById(R.id.welcome)
        attendanceButton = view.findViewById(R.id.attendanceButton)
        announcementButton = view.findViewById(R.id.announcementButton)

        holidaysButton = view.findViewById(R.id.HolidaysButton)
        leavesButton = view.findViewById(R.id.leavesButton)
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
        attendanceButton?.setOnClickListener {
            val intent = Intent(requireContext(), AttendanceActivity::class.java)
            startActivity(intent)
        }
        announcementButton?.setOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.bottomNavigationView?.selectedItemId = R.id.announcement
        }
        holidaysButton?.setOnClickListener {
            val intent = Intent(requireContext(), HolidaysActivity::class.java)
            startActivity(intent)
        }
        leavesButton?.setOnClickListener {
            val intent = Intent(requireContext(), HolidaysActivity::class.java)
            startActivity(intent)
        }



        // Initialize locationHelper
        locationHelper = LocationHelper(requireActivity())

        // Handle button click
        fetch?.setOnClickListener {
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        locationHelper?.fetchLocation { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("TAG", "$latitude   $longitude ")
                checkIfUserIsInArea(longitude, latitude)
            } else {
                Log.d("TAG", "Failed to fetch location")
            }
        }
    }

    private fun checkIfUserIsInArea(paramLongitude: Double, paramLatitude: Double) {
        if (paramLongitude > minLongitude && paramLongitude < maxLongitude &&
            paramLatitude > minLatitude && paramLatitude < maxLatitude) {
            Log.d("TAG", "User is in the area")
            Toast(requireContext()).showCustomToast(requireActivity(), "You are in Location area", R.color.green)
        } else {
            Toast(requireContext()).showCustomToast(requireActivity(), "You are out of Location area", R.color.red)
            Log.d("TAG", "User is not in the area")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationHelper.REQUEST_CHECK_SETTINGS) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // User agreed to change location settings
                if (locationHelper?.isLocationEnabled(requireActivity()) == true) {
                    fetchLocation()
                } else {
                    Toast(requireContext()).showCustomToast(requireActivity(), "Location Services are Unavailable, Please Try Again", R.color.yellow)
                }
            } else {
                // User did not agree to change location settings
                Toast(requireContext()).showCustomToast(requireActivity(), "Location Services are Required", R.color.red)
            }
        }
    }
}
