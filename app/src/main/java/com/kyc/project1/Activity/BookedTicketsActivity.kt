package com.kyc.project1.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kyc.project1.R
import com.kyc.project1.databinding.ActivityBookedTicketsBinding

class BookedTicketsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookedTicketsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booked_tickets)
        binding = ActivityBookedTicketsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}