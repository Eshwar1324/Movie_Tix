package com.kyc.project1.Activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.kyc.project1.Adapter.CastListAdapter
import com.kyc.project1.Adapter.FilmCategoryAdapter
import com.kyc.project1.Models.Film
import com.kyc.project1.R
import com.kyc.project1.databinding.ActivityFilmBinding
import eightbitlab.com.blurview.RenderScriptBlur

class FilmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilmBinding
    private var isFavorite:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setVariable()
    }


    private fun setVariable() {
        val filmJson = intent.getStringExtra("film")
        if (filmJson != null) {
            val item: Film? = Gson().fromJson(filmJson, Film::class.java)
            if (item != null) {
                val requestOptions = RequestOptions().transform(CenterCrop(), GranularRoundedCorners(0f, 0f, 50f, 50f))
                Glide.with(this).load(item.Poster).apply(requestOptions).into(binding.filmPic)

                binding.titleTxt.text = item.Title
                binding.imdbTxt.text = "IMDB ${item.Imdb}"
                binding.movieTimeTxt.text = "${item.Year} - ${item.Time}"
                binding.movieDescriptionTxt.text = item.Description

                binding.backBtn.setOnClickListener {
                    finish()
                }

                isFavorite = isMovieInFavorites(item)
                updateFavoriteIcon()

                binding.imageView6.setOnClickListener {
                    val currentFilm: Film? = Gson().fromJson(intent.getStringExtra("film"), Film::class.java)
                    if (currentFilm != null) {
                        if (!isFavorite) {
                            addToFavorites(currentFilm)
                            Toast.makeText(this, "${currentFilm.Title} added to favorites", Toast.LENGTH_SHORT).show()
                            isFavorite = true
                            updateFavoriteIcon()
                        }
                    }
                }

                binding.bookTktBtn.setOnClickListener {
                    val currentFilmForBooking: Film? = Gson().fromJson(intent.getStringExtra("film"), Film::class.java)
                    currentFilmForBooking?.let {
                        val intent = Intent(this, SeatingActivity::class.java)
                        intent.putExtra("film", it)
                        startActivity(intent)
                    }
                }

                val radius = 10f
                val decorView = window.decorView
                val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowsBackground = decorView.background

                binding.blurView.setupWith(rootView, RenderScriptBlur(this)).setFrameClearDrawable(windowsBackground).setBlurRadius(radius)
                binding.blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                binding.blurView.clipToOutline = true

                item.Genre?.let {
                    binding.genreView.adapter = FilmCategoryAdapter(it)
                    binding.genreView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                }

                item.Casts?.let {
                    binding.castListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.castListView.adapter = CastListAdapter(it)
                }
            } else {
                Toast.makeText(this, "Error: Could not deserialize movie details", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Error: Movie data not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.imageView6.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.imageView6.setImageResource(R.drawable.ic_favorite_white)
        }
    }

    private fun isMovieInFavorites(film: Film): Boolean {
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        return sharedPreferences.contains(film.Title)
    }

    private fun addToFavorites(film: Film) {
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val filmJson = Gson().toJson(film)
        editor.putString(film.Title, filmJson)
        editor.apply()
    }
}