package com.example.officeapp.Activities

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
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

class MainActivity : AppCompatActivity() {

    var fragmentContainer : FrameLayout? = null
    lateinit var homeFragment : HomeFragment
    lateinit var profileFragment : ProfileFragment
    lateinit var announcementFragment : AnnounementsFragment
    var bottomNavigationView : BottomNavigationView? = null

    lateinit var name1 :String
    lateinit var email1 :String
    lateinit var designation1 :String
    lateinit var mobile1 :String
    lateinit var address1 :String
    lateinit var imageUrl :String

    companion object {
        lateinit var USERID :String
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
        intent.getStringExtra("id")?.let { USERID = it }
        fragmentContainer = findViewById(R.id.fragmentContainer)
        Log.d("TAG", "onCreate: $USERID")
        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        announcementFragment = AnnounementsFragment()
        loadFragment(homeFragment)

        fetchData()

         bottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.announcement -> {
                    loadFragment(announcementFragment)
                    true
                }
                R.id.profile -> {
                    loadFragment(profileFragment)
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
        CoroutineScope(Dispatchers.Main).launch {


            val profileResult = ApiService.get(ApiLinks.PROFILE_URL+"/$USERID", null)
            if (profileResult.isSuccess) {
                val profileData = profileResult.getOrNull()
                GsonHelper.deserializeFromJson<Map<String, String>>(profileData!!)?.let {
                    Log.d(this.javaClass.name, it.toString())
                    name1 = it["name"].toString()
                    email1 = it["email"].toString()
                    designation1 = it["job_title"].toString()
                    mobile1 = it["phone"].toString()
                    address1 = it["address"].toString()
                    imageUrl = it["image"].toString()
                }
            } else {
                val error = profileResult.exceptionOrNull()
                Log.d("Tag","Profile request failed: ${error?.message}")
            }
        }

    }

}

