package com.kyc.project1.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyc.project1.Models.BookedTickets
import com.kyc.project1.Activity.TicketConfirmationActivity
import com.kyc.project1.databinding.ItemBookedTicketBinding

class BookedTicketsAdapter(private val ticketList: List<BookedTickets>) :
    RecyclerView.Adapter<BookedTicketsAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(val binding: ItemBookedTicketBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ItemBookedTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        with(holder.binding) {
            Glide.with(imageViewMoviePoster.context)
                .load(ticket.posterUrl)
                .into(imageViewMoviePoster)

            textViewMovieName.text = ticket.title
            textViewDate.text = "Date: ${ticket.date}"
            textViewTime.text = "Time: ${ticket.time}"
            textViewSeat.text = "Seat: ${ticket.seats}"

            root.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, TicketConfirmationActivity::class.java).apply {
                    putExtra("filmPoster", ticket.posterUrl)
                    putExtra("filmTitle", ticket.title)
                    putExtra("filmDuration", ticket.duration)
                    putExtra("selectedDate", ticket.date)
                    putExtra("selectedTime", ticket.time)
                    putExtra("selectedSeats", ticket.seats)
                    putExtra("numberOfTickets", ticket.numberOfTickets)
                    putExtra("totalPrice", ticket.totalPrice)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = ticketList.size
}
