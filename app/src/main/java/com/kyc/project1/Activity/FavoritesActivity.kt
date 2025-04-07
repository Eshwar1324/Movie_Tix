package com.kyc.project1.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kyc.project1.Adapter.FavoritesAdapter
import com.kyc.project1.Models.Film
import com.kyc.project1.R
import com.kyc.project1.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter
    private val favoriteFilms = mutableListOf<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveFavorites()
        setupRecyclerView()

        binding.backBtn.setOnClickListener {
            finish()
        }

    }

     fun removeItemFromFavorites(film: Film){
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(film.Title)
        editor.apply()
        favoriteFilms.remove(film)
    }

    private fun retrieveFavorites(){
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val gson = Gson()
        val allEntries: Map<String, *> = sharedPreferences.all
        for ((_, value) in allEntries) {
            val filmJson = value as String
            val film = gson.fromJson(filmJson, Film::class.java)
            favoriteFilms.add(film)
        }
    }

    private fun setupRecyclerView() {
        if (favoriteFilms.isEmpty()) {
            binding.favContainer.visibility = View.GONE
            binding.emptyFavoritesText.visibility = View.VISIBLE
        } else {
            binding.favContainer.visibility = View.VISIBLE
            binding.emptyFavoritesText.visibility = View.GONE
            favoritesAdapter = FavoritesAdapter(favoriteFilms, this)
            binding.favContainer.layoutManager = LinearLayoutManager(this)
            binding.favContainer.adapter = favoritesAdapter
        }
    }
}