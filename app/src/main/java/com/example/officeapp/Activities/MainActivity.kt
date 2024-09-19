package com.example.officeapp.Activities

import android.os.Bundle
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
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    var fragmentContainer : FrameLayout? = null
    lateinit var homeFragment : HomeFragment
    lateinit var profileFragment : ProfileFragment
    lateinit var announcementFragment : AnnounementsFragment
    var bottomNavigationView : BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fragmentContainer = findViewById(R.id.fragmentContainer)

        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        announcementFragment = AnnounementsFragment()
        loadFragment(homeFragment)

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



}

