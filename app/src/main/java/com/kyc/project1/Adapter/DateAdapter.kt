package com.kyc.project1.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kyc.project1.R
import com.kyc.project1.databinding.ItemDateBinding

class DateAdapter(private val dates: List<String>, private val onDateSelected: (String) -> Unit) :
    RecyclerView.Adapter<DateAdapter.DateViewholder>() {
    private var selectedPosition = -1

    inner class DateViewholder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            val dateParts = date.split("/")
            if (dateParts.size == 3) {
                binding.dayTxt.text = dateParts[0]
                binding.dateMonthTxt.text = dateParts[1] + " " + dateParts[2]

                if (selectedPosition == adapterPosition) {
                    binding.mailLayout.setBackgroundResource(R.drawable.white_bg)
                    binding.dayTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                    binding.dateMonthTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                } else {
                    binding.mailLayout.setBackgroundResource(R.drawable.light_bg)
                    binding.dayTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey))
                    binding.dateMonthTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey))
                }

                binding.root.setOnClickListener {
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                    onDateSelected(date)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewholder {
        return DateViewholder(
            ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DateViewholder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount(): Int = dates.size
}