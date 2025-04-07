package com.kyc.project1.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kyc.project1.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        binding.LoginButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.RegisterButton.setOnClickListener {
            val email = binding.EmailID.text.toString().trim()
            val pass = binding.PasswordText.text.toString().trim()
            val confirmPass = binding.ConfirmPassword.text.toString().trim()
            val username = binding.UsernameEditText.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && username.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {task->
                        if (task.isSuccessful) {
                            val firebaseUser = firebaseAuth.currentUser
                            firebaseUser?.let{user->
                                val userId = user.uid
                                val usersRef = firebaseDatabase.reference.child("users").child(userId)

                                val userDetails = HashMap<String, String>()
                                userDetails["email"] = email
                                userDetails["username"] = username

                                usersRef.setValue(userDetails).addOnSuccessListener {
                                    Toast.makeText(this, "Registered successfully",Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }.addOnFailureListener {e->
                                    Toast.makeText(this, "Failed to register: ${e.message}", Toast.LENGTH_SHORT).show()
                                    user.delete()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}