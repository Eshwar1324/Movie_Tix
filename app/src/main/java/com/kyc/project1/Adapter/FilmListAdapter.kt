package com.kyc.project1.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.kyc.project1.Activity.FilmActivity
import com.kyc.project1.Models.Film
import com.kyc.project1.databinding.ViewholderFilmBinding

class FilmListAdapter(private val items:ArrayList<Film>,
                      private val listener: ValueEventListener
    ):RecyclerView.Adapter<FilmListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(film: Film)
    }

    private var context: Context?=null

    inner class ViewHolder(val binding: ViewholderFilmBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(film:Film){
            binding.nameTxt.text = film.Title
            val requestOptions = RequestOptions().transform(CenterCrop(),RoundedCorners(30))

            Glide.with(context!!).load(film.Poster).apply(requestOptions).into(binding.pic)

            binding.root.setOnClickListener{
                val intent = Intent(context,FilmActivity::class.java)
                intent.putExtra("film", Gson().toJson(film))
                context!!.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmListAdapter.ViewHolder {
        context = parent.context
        val binding = ViewholderFilmBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmListAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}