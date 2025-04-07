package com.kyc.project1.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kyc.project1.R
import com.kyc.project1.databinding.ItemTimeBinding

class TimeAdapter(private val timeSlots: List<String>, private val onTimeSelected: (String) -> Unit) :
    RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    inner class TimeViewHolder(private val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(time: String) {
            binding.TextViewTime.text = time
            if (selectedPosition == adapterPosition) {
                binding.TextViewTime.setBackgroundResource(R.drawable.white_bg)
                binding.TextViewTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            } else {
                binding.TextViewTime.setBackgroundResource(R.drawable.light_bg)
                binding.TextViewTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.grey))
            }

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(selectedPosition)
                    onTimeSelected(time)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(
            ItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }

    override fun getItemCount(): Int = timeSlots.size
}