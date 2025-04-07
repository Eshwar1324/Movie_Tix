package com.kyc.project1.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyc.project1.Activity.FavoritesActivity
import com.kyc.project1.Models.Film
import com.kyc.project1.R

class FavoritesAdapter(private val films: MutableList<Film>,
                       private val activity: FavoritesActivity,
                        private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(film: Film)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val FilmImage: ImageView = itemView.findViewById(R.id.filmImage)
        val titleText: TextView = itemView.findViewById(R.id.filmTitle)
        val yearText: TextView = itemView.findViewById(R.id.FilmYear)
        val durationText: TextView = itemView.findViewById(R.id.DurationText)
        val imdbText: TextView = itemView.findViewById(R.id.imdbText)
        val likeBtn: ImageView = itemView.findViewById(R.id.LikeBtn)

        fun bind(film: Film, position: Int) {
            Glide.with(itemView.context).load(film.Poster).into(FilmImage)
            titleText.text = film.Title
            yearText.text = film.Year.toString()
            durationText.text = film.Time
            imdbText.text = "IMDB ${film.Imdb}"

            likeBtn.setOnClickListener {
                activity.removeItemFromFavorites(film)
                val indexToRemove = films.indexOfFirst { it.Title == film.Title }

                if (indexToRemove != -1) {
                    films.removeAt(indexToRemove)
                    Toast.makeText(itemView.context, "${film.Title} removed from favorites", Toast.LENGTH_SHORT).show()
                    notifyItemRemoved(indexToRemove)
                }
            }
            itemView.setOnClickListener {
                itemClickListener.onItemClick(film)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(films[position], position)
    }

    override fun getItemCount(): Int {
        return films.size
    }
}