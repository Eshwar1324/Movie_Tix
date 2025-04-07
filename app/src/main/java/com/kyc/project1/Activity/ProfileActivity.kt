package com.kyc.project1.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kyc.project1.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.backBtn.setOnClickListener {
            finish()
        }

        loadUserProfile()

        binding.saveProfileButton.setOnClickListener {
            saveUserProfile()
        }

        binding.logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.reference.child("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").getValue(String::class.java)
                    val email = currentUser.email

                    binding.usernameEditText.setText(username)
                    binding.emailTextView.text = email
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Failed to load profile data.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveUserProfile() {
        val newUsername = binding.usernameEditText.text.toString().trim()

        if (newUsername.isEmpty()) {
            binding.usernameEditText.error = "Username cannot be empty"
            return
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.reference.child("users").child(userId)

            val updates = HashMap<String, Any>()
            updates["username"] = newUsername

            userRef.updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(this@ProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                    intent.putExtra("updatedUsername",newUsername)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@ProfileActivity, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun logoutUser() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}