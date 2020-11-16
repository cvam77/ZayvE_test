package com.example.zayve_test

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ZayveActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zayve)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        fetchUserData(navView)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        todo: don't forget to change here
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_chat_page, R.id.nav_profile, R.id.nav_browse_friends, R.id.nav_requests, R.id.nav_search_by_interest), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.zayve, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //  fetches user information from database and updates the profile view
    private fun fetchUserData(navView: NavigationView) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val valueListner = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("responseData", dataSnapshot.value.toString())
                    val profilePic = dataSnapshot.child("profile_image").value
                            ?: "https://upload.wikimedia.org/wikipedia/commons/3/3e/Android_logo_2019.png"
                    navView[0].findViewById<TextView>(R.id.nav_bar_user_name).text = user.displayName
                    val imageView = findViewById<ImageView>(R.id.nav_bar_user_image)
                    Picasso.get().load(profilePic as String).into(imageView);

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("loadPost:onCancelled", databaseError.toException())
                }
            }
            user.uid.let { FirebaseDatabase.getInstance().reference.child("users").child(it) }.addListenerForSingleValueEvent(valueListner)
        }
    }
}