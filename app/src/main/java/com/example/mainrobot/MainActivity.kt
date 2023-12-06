package com.example.mainrobot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mainrobot.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
//            // Handle FAB click
//        }

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_robotcar,R.id.nav_robotcar_new, R.id.nav_configurations, R.id.nav_location, R.id.nav_trips
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
//            TODO: modify this as it is read by the app...
            Log.d("MainActivity", "gving this a try")
            when (it.itemId) {
                R.id.nav_home -> {
                    // handle click
                    Log.d("MainActivity", "nav_home")
                    navController.navigate(R.id.nav_home)
                    true
                }
                R.id.nav_settings -> {
                    // handle click
                    Log.d("MainActivity", "nav_settings")
                    navController.navigate(R.id.nav_settings)
                    true
                }
                R.id.nav_configurations -> {
                    // handle click
                    Log.d("MainActivity", "nav_configurations")
                    navController.navigate(R.id.nav_configurations)
                    true
                }
                R.id.nav_robotcar -> {
                    // handle click
                    Log.d("MainActivity", "nav_robotcar")
                    navController.navigate(R.id.nav_robotcar)
                    true
                }
                R.id.nav_robotcar_new -> {
                    // handle click
                    Log.d("MainActivity", "nav_robotcarnew")
                    navController.navigate(R.id.nav_robotcar_new)
                    true
                }
                R.id.nav_location -> {
                    // handle click
                    Log.d("Location", "nav_location")
                    navController.navigate(R.id.nav_location)
                    true
                }
                R.id.nav_trips -> {
                    // handle click
                    Log.d("Trips", "nav_trips")
                    navController.navigate(R.id.nav_trips)
                    true
                }

                R.id.nav_logout -> {
                // handle click
                Log.d("MainActivity", "nav_logout")
                navController.navigate(R.id.nav_logout)
                performLogout()
                true
            }
                else -> false
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        Log.d("MainActivity", "isLoggedIn: $isLoggedIn")
        if (!isLoggedIn) {
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            Log.d("MainActivity", "User Not logged in")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.d("MainActivity", "onOptionsItemSelected: ItemId: ${item.itemId}")
//        return when (item.itemId) {
//            R.id.nav_logout -> {
//                Log.d("MainActivity", "Logout Selected")
//                performLogout()
//                true
//            }
//            else -> {
//                Log.d("MainActivity", "inside the else")
//                super.onOptionsItemSelected(item)
//            }
//        }
//    }
////    @SuppressWarnings("StatementWithEmptyBody")
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        Log.d("MainActivity", "onNavigationItemSelected: ItemId: ${item.itemId}")
//        when (item.itemId) {
//        R.id.nav_home -> {
//            // Handle Home menu item click
//            navController.navigate(R.id.nav_home)
//        }
//        R.id.nav_settings -> {
//            // Handle Settings menu item click
//            navController.navigate(R.id.nav_settings)
//        }
//        R.id.nav_configurations -> {
//            // Handle Configurations menu item click
//            navController.navigate(R.id.nav_configurations)
//        }
//        R.id.nav_robotcar -> {
//            // Handle RobotCar menu item click
//            navController.navigate(R.id.nav_robotcar)
//        }
//        R.id.nav_logout -> {
//            // Handle Logout menu item click
//
//            performLogout()
//        }
//    }
//
//    // Close the navigation drawer after handling the click
//    drawerLayout.closeDrawer(GravityCompat.START)
//    return true
//}

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun performLogout() {
        // Implement your logout logic here
        // For example, clear the user session, navigate to the login page, etc.
        // You can start the LoginActivity using an explicit intent
        // Here's an example of clearing a login status flag
        Log.d("MainActivity", "Logout")
        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
        Log.d("MainActivity", "is_logged_in: Not Sure")
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        Log.d("MainActivity", "is_logged_in: $isLoggedIn")
        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}