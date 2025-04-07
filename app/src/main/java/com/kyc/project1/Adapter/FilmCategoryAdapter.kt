package com.kyc.project1.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kyc.project1.databinding.ViewholderCategoryBinding

class FilmCategoryAdapter(private val items:List<String>):RecyclerView.Adapter<FilmCategoryAdapter.Viewholder>() {

    class Viewholder(val binding:ViewholderCategoryBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmCategoryAdapter.Viewholder {
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: FilmCategoryAdapter.Viewholder, position: Int) {
        holder.binding.titleTxt.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}