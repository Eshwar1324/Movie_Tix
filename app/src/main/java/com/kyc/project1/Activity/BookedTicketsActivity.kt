package com.kyc.project1.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kyc.project1.Adapter.BookedTicketsAdapter
import com.kyc.project1.Models.BookedTickets
import com.kyc.project1.R
import com.kyc.project1.databinding.ActivityBookedTicketsBinding

class BookedTicketsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookedTicketsBinding

    private fun loadBookedTickets(): List<BookedTickets> {
        val prefs = getSharedPreferences("booked_tickets", MODE_PRIVATE)
        val ticketListJson = prefs.getString("tickets", "[]")
        val gson = Gson()
        val type = object : TypeToken<List<BookedTickets>>() {}.type
        return gson.fromJson(ticketListJson, type)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booked_tickets)
        binding = ActivityBookedTicketsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }
        val tickets = loadBookedTickets()
        if (tickets.isNotEmpty()) {
            binding.recyclerViewBookedTickets.apply {
                layoutManager = LinearLayoutManager(this@BookedTicketsActivity)
                adapter = BookedTicketsAdapter(tickets)
            }
            binding.NoContTxt.visibility = View.GONE
        } else {
            binding.NoContTxt.visibility = View.VISIBLE
        }
    }
}