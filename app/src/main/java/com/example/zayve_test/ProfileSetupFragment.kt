package com.example.zayve_test

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zayve_test.databinding.FragmentProfileSetupBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class ProfileSetupFragment : Fragment() {
    private lateinit var binding: FragmentProfileSetupBinding
    private var SELECT_PHOTO: Int = 1
    private lateinit var uri: Uri
    private lateinit var mStorageRef: StorageReference;
    private lateinit var avatarImage:Intent
    private  var imageUrl=""
    private lateinit var user:FirebaseUser



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_setup, container, false)
//        receivers user name from firebase
        user = FirebaseAuth.getInstance().currentUser!!
            for (profile in user.providerData) {
                binding.profileUsername.text = profile.displayName
            }
//        picks an image from user
        binding.pickImage.setOnClickListener {
            var intent: Intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_PHOTO)
        }
        binding.saveProfile.setOnClickListener {
            submitInterests()
        }
        return binding.root
    }

    // Write a message to the database
    private fun submitInterests() {
        val database = FirebaseDatabase.getInstance()
        val myDBref = database.reference
        uploadAvatarImage(avatarImage)
        var interests = arrayListOf<String>(binding.interest1.text.toString(), binding.interest2.text.toString(),
                binding.interest3.text.toString(),
                binding.interest4.text.toString(),
                binding.interest5.text.toString()
        )
        myDBref.child("users").child(user.uid).child("interests").setValue(interests)
        myDBref.child("users").child(user.uid).child("avatar_image").setValue(imageUrl)
        findNavController().navigate(R.id.action_profileSetupFragment_to_profile)
    }

//    handles image picker result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PHOTO) {
            // handle chosen image
            if (data != null) {
                avatarImage=data
            }
        }
    }

    //   uploads avatar image and saves downloadurl into imageUrl
    private fun uploadAvatarImage(data: Intent?) {
        var imageURI = data?.data
        binding.profileAvatar.setImageURI(imageURI)
//        TODO: Add authentication afterwards
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
        if (imageURI != null) {
            mStorageRef = FirebaseStorage.getInstance().reference;
//                val riversRef: StorageReference = mStorageRef.child("images/" + user.uid)
            val riversRef: StorageReference = mStorageRef.child("images/${user.uid}")
            riversRef.putFile(imageURI)
                    .addOnSuccessListener { taskSnapshot-> // Get a URL to the uploaded content
                        // Get a URL to the uploaded content
                        val downloadTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                        downloadTask.addOnSuccessListener {
                            imageUrl = it.toString()
                            Log.d("response", imageUrl)

                        }
                        Log.d("response", "Successfully uploaded the image to firebase")
                    }
                    .addOnFailureListener {
                        Log.d("response", "Unsuccessful to upload the image to firebase")
                    }
        }
    }

}


