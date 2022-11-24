package com.example.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.instagram.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()



        hideBottomNav()
        setBottomNav()
    }

    override fun onStart() {
        super.onStart()

        auth?.currentUser
    }



    fun showBottomNav() {
        binding!!.bottomNavigation.visibility = View.VISIBLE

    }

    fun hideBottomNav() {
        binding!!.bottomNavigation.visibility = View.GONE

    }


    private fun setBottomNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setupWithNavController(navController)
    }


}