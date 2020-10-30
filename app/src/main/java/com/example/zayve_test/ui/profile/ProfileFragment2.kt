package com.example.zayve_test.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.zayve_test.R
import com.example.zayve_test.databinding.FragmentProfile2Binding
import com.example.zayve_test.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class ProfileFragment2 : Fragment() {
    private lateinit var binding: FragmentProfile2Binding
    private lateinit var user:FirebaseUser
    private lateinit var database: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile2, container, false)
        user = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance().reference.child("users").child(user.uid);
        fetchUserData()
        binding.editButton.setOnClickListener {

        }
        return binding.root
    }

    //  fetches user name from cloud firestore and updates the profile view
    private fun fetchUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val valueListner = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val avatarUrl = dataSnapshot.child("avatar_image").value
                    val interests = dataSnapshot.child("interests").value
                    Log.d("response",interests.toString())
//                    this user is our model User
                    binding.user=User(user.displayName.toString(),avatarUrl as String, interests as ArrayList<String>)
                    Picasso.get().load(avatarUrl).into(binding.profileAvatar);
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w( "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.addListenerForSingleValueEvent(valueListner)
        }
    }


}