package com.kyc.project1.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.kyc.project1.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var selectedMethod: String? = null

    private lateinit var filmPoster: String
    private lateinit var filmTitle: String
    private lateinit var filmDuration: String
    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    private lateinit var selectedSeats: String
    private var numberOfTickets: Int = 0
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupListeners()
    }

    private fun getIntentData() {
        filmPoster = intent.getStringExtra("filmPoster") ?: ""
        filmTitle = intent.getStringExtra("filmTitle") ?: ""
        filmDuration = intent.getStringExtra("filmDuration") ?: ""
        selectedDate = intent.getStringExtra("selectedDate") ?: ""
        selectedTime = intent.getStringExtra("selectedTime") ?: ""
        selectedSeats = intent.getStringExtra("selectedSeats") ?: ""
        numberOfTickets = intent.getIntExtra("numberOfTickets", 0)
        totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
    }

    private fun setupListeners() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.payBtn.visibility = View.VISIBLE

            when (checkedId) {
                binding.radioOnline.id -> {
                    selectedMethod = "online"
                    binding.onlineForm.visibility = View.VISIBLE
                    binding.cardForm.visibility = View.GONE
                }

                binding.radioCard.id -> {
                    selectedMethod = "card"
                    binding.onlineForm.visibility = View.GONE
                    binding.cardForm.visibility = View.VISIBLE
                }
            }
        }

        binding.payBtn.setOnClickListener {
            when (selectedMethod) {
                "online" -> handleOnlinePayment()
                "card" -> handleCardPayment()
                else -> Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleOnlinePayment() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val txnId = binding.transactionIdInput.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || txnId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields for online payment", Toast.LENGTH_SHORT).show()
            return
        }

        proceedToTicketConfirmation()
    }

    private fun handleCardPayment() {
        val cardNumber = binding.cardNumberInput.text.toString().trim()
        val cvv = binding.cvvInput.text.toString().trim()

        if (cardNumber.length != 16 || !cardNumber.all { it.isDigit() }) {
            Toast.makeText(this, "Enter a valid 16-digit card number", Toast.LENGTH_SHORT).show()
            return
        }

        if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
            Toast.makeText(this, "Enter a valid 3-digit CVV", Toast.LENGTH_SHORT).show()
            return
        }

        proceedToTicketConfirmation()
    }

    private fun proceedToTicketConfirmation() {
        val intent = Intent(this, TicketConfirmationActivity::class.java)
        intent.putExtra("filmPoster", filmPoster)
        intent.putExtra("filmTitle", filmTitle)
        intent.putExtra("filmDuration", filmDuration)
        intent.putExtra("selectedDate", selectedDate)
        intent.putExtra("selectedTime", selectedTime)
        intent.putExtra("selectedSeats", selectedSeats)
        intent.putExtra("numberOfTickets", numberOfTickets)
        intent.putExtra("totalPrice", totalPrice)
        startActivity(intent)
        finish()
    }
}
