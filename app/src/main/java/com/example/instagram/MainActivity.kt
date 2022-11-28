package com.example.instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.instagram.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    var mBackWait: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setBottomNav()

        hideNav()
    }

//    override fun onBackPressed() {
//        if (System.currentTimeMillis() - mBackWait >= 1000) {
//            mBackWait = System.currentTimeMillis()
////            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다!", Toast.LENGTH_SHORT).show()
//        } else {
//            finish()
//        }
//    }


    private fun setBottomNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setupWithNavController(navController)
    }

    override fun onPause() {
        super.onPause()
//        binding!!.bottomNavigation.visibility = View.VISIBLE
    }

    fun showNav() {
        binding!!.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideNav() {
        binding!!.bottomNavigation.visibility = View.GONE
    }

    fun hideToolbar() {
//        binding!!.myToolbar.visibility = View.GONE
    }

    fun showToolbar() {
//        binding!!.myToolbar.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        auth?.currentUser
    }


}