package com.example.officeapp.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.officeapp.Fragments.AnnounementsFragment
import com.example.officeapp.Fragments.HomeFragment
import com.example.officeapp.Fragments.ProfileFragment
import com.example.officeapp.R
import com.example.officeapp.Utils.ApiLinks
import com.example.officeapp.Utils.ApiService
import com.example.officeapp.Utils.GsonHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.jar.Attributes.Name

class MainActivity : AppCompatActivity() {

    var fragmentContainer: FrameLayout? = null
    lateinit var homeFragment: HomeFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var announcementFragment: AnnounementsFragment
    var bottomNavigationView: BottomNavigationView? = null
    var fetching: Boolean = false
    var checkingStatus :Boolean = false
    var name1: String = ""
    var email1: String = ""
    var designation1: String = ""
    var mobile1: String = ""
    var address1: String = ""
    var imageUrl: String = ""


    companion object {
        var NAME: String = ""
        var isIN: Boolean = false
        var token :String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
        fragmentContainer = findViewById(R.id.fragmentContainer)
        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        announcementFragment = AnnounementsFragment()
        loadFragment(homeFragment)
        checkAttendanceStatus()
        fetchData()

        bottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    checkAttendanceStatus()
                    true
                }

                R.id.announcement -> {
                    loadFragment(announcementFragment)
                    if(announcementFragment.isAdded){
                    announcementFragment.announcementList()
                        }
                    checkAttendanceStatus()
                    true
                }

                R.id.profile -> {
                    loadFragment(profileFragment)
                    checkAttendanceStatus()
                    if (!fetching) {
                        fetchData()
                    }
                    true
                }

                else -> false
            }
        }

    }

    public fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // Hide all the fragments first
        supportFragmentManager.fragments.forEach {
            transaction.hide(it)
        }

        // Check if the fragment has already been added
        if (supportFragmentManager.fragments.contains(fragment)) {
            // Fragment is already added, just show it
            transaction.show(fragment)
        } else {
            // Fragment is not added, so add and show it
            transaction.add(fragmentContainer!!.id, fragment)
            transaction.show(fragment)
        }

        transaction.commit()
    }

    fun fetchData() {
        fetching = true




        CoroutineScope(Dispatchers.Main).launch {


            val profileResult = ApiService.get(ApiLinks.PROFILE_URL , null)
            if (profileResult.isSuccess) {
                val profileData = profileResult.getOrNull()
                GsonHelper.deserializeFromJson<Map<String, String>>(profileData!!)?.let {
                    Log.d(this.javaClass.name, it.toString())
                    name1 = it["name"].toString()
                    email1 = it["email"].toString()
                    designation1 = it["job_title"].toString()
                    mobile1 = it["phone"].toString()
                    address1 = it["address"].toString()
                    val image = it["image"].toString()
                    fetching = false

                    if (image.isNotEmpty() && image != "null") {
                        imageUrl = it["image"].toString()
                    }
                    if (profileFragment.isAdded) {
                        profileFragment.updateData()
                        profileFragment.imageUri = null
                    }
                }
            } else {
                val error = profileResult.exceptionOrNull()
                fetching = false

                Log.d("Tag", "Profile request failed: ${error?.message}")
            }
        }
    }

    fun checkAttendanceStatus() {
        if(!checkingStatus) {
            checkingStatus = true
            CoroutineScope(Dispatchers.Main).launch {

                val attendanceResult =
                    ApiService.get(ApiLinks.ATTENDANCE_STATUS_URL , null)
                if (attendanceResult.isSuccess) {
                    val attendanceData = attendanceResult.getOrNull()
                    GsonHelper.deserializeFromJson<Map<String, String>>(attendanceData!!)?.let {
                        Log.d(this.javaClass.name, it.toString())
                        if (it["attendance"].toString() == "present") {
                            if(it["off_at"] == null || it["off_at"] == "null" || it["off_at"] == "") {
                                isIN = true
                                if (homeFragment.isAdded) {
                                    homeFragment.checkIN_OUT?.text = "CHECK OUT"
                                    homeFragment.checkInTime?.visibility = View.VISIBLE
                                    homeFragment.checkInTime?.text = "CHECK IN TIME: ${it["on_at"]}"
                                    homeFragment.checkIN_OUT?.isEnabled = true

                                }
                            }
                            else{
                                isIN = false
                                if (homeFragment.isAdded) {
                                    homeFragment.checkIN_OUT?.text = "CHECKED OUT"
                                    homeFragment.checkInTime?.visibility = View.VISIBLE
                                    homeFragment.checkInTime?.text = "CHECK OUT TIME: ${it["off_at"]}"
                                    homeFragment.checkIN_OUT?.isEnabled = false
                                }
                            }
                        } else {
                            isIN = false
                            if (homeFragment.isAdded) {
                                homeFragment.checkInTime?.visibility = View.GONE
                                homeFragment.checkIN_OUT?.isEnabled = true
                                homeFragment.checkIN_OUT?.text = "CHECK IN"
                            }

                        }
                        checkingStatus = false

                    }
                } else {
                    Log.d(
                        "Tag",
                        "Attendance request failed: ${attendanceResult.exceptionOrNull()?.message}"
                    )
                    checkingStatus = false

                }
            }
        }
    }

}

