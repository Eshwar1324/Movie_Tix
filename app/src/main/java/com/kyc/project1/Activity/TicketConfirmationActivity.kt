package com.kyc.project1.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.kyc.project1.R
import com.kyc.project1.databinding.ActivityTicketConfirmationBinding

class TicketConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTicketConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filmPoster = intent.getStringExtra("filmPoster")
        val filmTitle = intent.getStringExtra("filmTitle")
        val filmDuration = intent.getStringExtra("filmDuration")
        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedTime = intent.getStringExtra("selectedTime")
        val selectedSeats = intent.getStringExtra("selectedSeats")
        val numberOfTickets = intent.getIntExtra("numberOfTickets",0)
        val totalPrice = intent.getDoubleExtra("totalPrice",0.0)

        Glide.with(this).load(filmPoster)
            .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)).into(binding.filmPosterImageView)

        binding.filmNameTxt.text = filmTitle
        binding.durationTxt.text = filmDuration
        binding.dateTxt.text = selectedDate
        binding.timeTxt.text = selectedTime
        binding.seatsTxt.text = selectedSeats
        binding.ticketsTxt.text = "$numberOfTickets Tickets"
        binding.priceTxt.text = "Rs. ${String.format("%.2f", totalPrice)}"

        val qrCodeContent = "Movie: $filmTitle\nDate: $selectedDate\nTime: $selectedTime\nSeats: $selectedSeats\nTickets: $numberOfTickets\nPrice: Rs. ${String.format("%.2f", totalPrice)}"
        val qrCodeBitmap = generateQRCode(qrCodeContent)
        binding.qrCodeImageView.setImageBitmap(qrCodeBitmap)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.backToHomeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        val writer = QRCodeWriter()
        try {
            val hints = mutableMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val bitMatrix:BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512,hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}