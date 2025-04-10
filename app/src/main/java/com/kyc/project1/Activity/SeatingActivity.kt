package com.kyc.project1.Activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyc.project1.Adapter.DateAdapter
import com.kyc.project1.Adapter.SeatListAdapter
import com.kyc.project1.Adapter.TimeAdapter
import com.kyc.project1.Models.Film
import com.kyc.project1.Models.Seat
import com.kyc.project1.databinding.ActivitySeatingBinding
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SeatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatingBinding
    private lateinit var film: Film
    private var price: Double = 0.0
    private var number: Int = 0
    private var selectedDate: String?= null
    private var selectedTime: String?= null
    private var selectedSeats: String?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySeatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentExtra()
        setVariable()
        initSeatsList()
        initDateRecyclerView()
        initTimeRecyclerView()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun tryInitSeatsList() {
        if (!selectedDate.isNullOrEmpty() && !selectedTime.isNullOrEmpty()) {
            initSeatsList()
        }
    }

    private fun initSeatsList() {
        val gridLayoutManager = GridLayoutManager(this, 7)
        gridLayoutManager.spanSizeLookup = object:GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(position % 7 == 3)1 else 1
            }
        }

        binding.seatRecyclerview.layoutManager = gridLayoutManager
        val seatList = mutableListOf<Seat>()
        val numberOfRows = 11
        val seatsPerRow = 7
        var seatCounter = 1

        for (row in 'A'..'K') {
            for (col in 1..seatsPerRow) {
                val seatName = "$row$col"
                val sharedPreferences = getSharedPreferences("BookedSeats", MODE_PRIVATE)
                val key = "booked_seats_${film.Title}_${selectedDate}_${selectedTime}"
                val bookedSeats = sharedPreferences.getStringSet(key, setOf()) ?: setOf()

                val SeatStatus = if (bookedSeats.contains(seatName)) {
                    Seat.SeatStatus.UNAVAILABLE
                } else {
                    Seat.SeatStatus.AVAILABLE
                }

                seatList.add(Seat(SeatStatus, seatName))
                seatCounter++
            }
        }

        val seatAdapter = SeatListAdapter(seatList,this,object:SeatListAdapter.SelectedSeat{
            override fun Return(selectedName: String, num: Int) {
                binding.seatNumberTxt.text = "$num seats selected"
                val df = DecimalFormat("#.##")
                price = df.format(num * film.Price).toDouble()
                number = num
                binding.priceTxt.text = "Rs.$price"
                selectedSeats = selectedName
            }

        })
        binding.seatRecyclerview.adapter = seatAdapter
        binding.seatRecyclerview.isNestedScrollingEnabled = false

        //binding.timeRecyclerview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        //binding.timeRecyclerview.adapter = TimeAdapter(generateTimeSlots())

        //binding.dateRecyclerview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
       //binding.dateRecyclerview.adapter = DateAdapter(generateDates())
    }

    private fun initDateRecyclerView() {
        binding.dateRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val dateAdapter = DateAdapter(generateDates()) { selectedDateItem ->
            selectedDate = selectedDateItem
            tryInitSeatsList()
        }
        binding.dateRecyclerview.adapter = dateAdapter
    }

    private fun initTimeRecyclerView() {
        binding.timeRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val timeAdapter = TimeAdapter(generateTimeSlots()) { selectedTimeSlot ->
            selectedTime = selectedTimeSlot
            tryInitSeatsList()
        }
        binding.timeRecyclerview.adapter = timeAdapter
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.BookTicketBtn.setOnClickListener {
            if (number > 0 && selectedDate != null && selectedTime != null && selectedSeats != null) {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("filmPoster", film.Poster)
                intent.putExtra("filmTitle", film.Title)
                intent.putExtra("filmDuration", film.Time)
                intent.putExtra("selectedDate", selectedDate)
                intent.putExtra("selectedTime", selectedTime)
                intent.putExtra("selectedSeats", selectedSeats)
                intent.putExtra("numberOfTickets", number)
                intent.putExtra("totalPrice", price)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select date, time, and seats", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getIntentExtra() {
        film = intent.getParcelableExtra("film")!!
    }

    private fun generateTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")

        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(21, 0)
        var currentTime = startTime

        while (currentTime.isBefore(endTime.plusMinutes(1))) {
            timeSlots.add(currentTime.format(formatter))
            currentTime = currentTime.plusMinutes(30)
        }
        return timeSlots
    }

    private fun generateDates():List<String>{
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE/dd/MMM")

        for(i in 0 until 7){
            dates.add(today.plusDays(i.toLong()).format(formatter))
        }
        return dates
    }
}