package com.kyc.project1.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.kyc.project1.Adapter.FilmListAdapter
import com.kyc.project1.Adapter.SliderAdapter
import com.kyc.project1.Models.Film
import com.kyc.project1.Models.SliderItems
import com.kyc.project1.R
import com.kyc.project1.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity(), FilmListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var userListener: ValueEventListener? = null
    private var userRef: DatabaseReference? = null
    private val sliderHandler = Handler()
    private val sliderRunnable = Runnable {
        binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        intent.getStringExtra("updatedUsername")?.let { updatedUsername ->
            binding.UsernameTxt.text = updatedUsername
        }

        window.setFlags(
            FLAG_LAYOUT_NO_LIMITS,
            FLAG_LAYOUT_NO_LIMITS
        )

        setupUsernameListener()
        initBanner()
        initTopMovie()
        initUpcoming()
        initBottomNavigation()
    }

    override fun onItemClick(film: Film) {
        navigateToFilmActivity(film)
    }

    private fun navigateToFilmActivity(film: Film) {
        val intent = Intent(this, FilmActivity::class.java)
        intent.putExtra("film", Gson().toJson(film))
        startActivity(intent)
    }

    private fun setupUsernameListener() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            userRef = database.reference.child("users").child(userId).child("username")

            userListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.getValue(String::class.java)
                    binding.UsernameTxt.text = username ?: "Guest"
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error listening for username changes: ${error.message}")
                }
            }
            userRef?.addValueEventListener(userListener!!)
        } else {
            binding.UsernameTxt.text = "Guest"
        }
    }

    override fun onStop() {
        super.onStop()
        userRef?.removeEventListener(userListener!!)
        userListener = null
        userRef = null
    }

    private fun initBottomNavigation(){
        val bottomNav = findViewById<ChipNavigationBar>(R.id.bottom_nav)
        bottomNav.setItemSelected(R.id.explorer, true)

        bottomNav.setOnItemSelectedListener { id ->
            when (id) {
                R.id.explorer -> {

                }

                R.id.favorites ->{
                    val intent = Intent(this,FavoritesActivity::class.java)
                    startActivity(intent)
                }
                R.id.tickets ->{
                    val intent = Intent(this,BookedTicketsActivity::class.java)
                    startActivity(intent)
                }
                R.id.profile ->{
                    val intent = Intent(this,ProfileActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initTopMovie() {
        val myRef: DatabaseReference = database.getReference("Items")
        binding.progressBar2Topmovies.visibility = View.VISIBLE
        val items = ArrayList<Film>()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        items.add(issue.getValue(Film::class.java)!!)
                    }
                    if (items.isNotEmpty()) {
                        binding.recyclerViewTopmovies.layoutManager = LinearLayoutManager(
                            this@HomeActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recyclerViewTopmovies.adapter = FilmListAdapter(items,this)
                    }
                    binding.progressBar2Topmovies.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initUpcoming() {
        val myRef: DatabaseReference = database.getReference("Upcomming")
        binding.progressBarupcomingmovies.visibility = View.VISIBLE
        val items = ArrayList<Film>()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        items.add(issue.getValue(Film::class.java)!!)
                    }
                    if (items.isNotEmpty()) {
                        binding.recyclerViewupcomingmovies.layoutManager = LinearLayoutManager(
                            this@HomeActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recyclerViewupcomingmovies.adapter = FilmListAdapter(items,this)
                    }
                    binding.progressBarupcomingmovies.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initBanner() {
        val myRef: DatabaseReference = database.getReference("Banners")
        binding.progressBarSlider.visibility = View.VISIBLE

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderItems>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(SliderItems::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                binding.progressBarSlider.visibility = View.GONE
                banners(lists)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun banners(lists: MutableList<SliderItems>) {
        binding.viewPager2.adapter = SliderAdapter(lists, binding.viewPager2)
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer(ViewPager2.PageTransformer { page, position ->
                val r = 1 - Math.abs(position)
                page.scaleY = 0.85f + r * 0.15f
            })
        }

        binding.viewPager2.setPageTransformer(compositePageTransformer)
        binding.viewPager2.currentItem = 1
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)

        val bottomNav = findViewById<ChipNavigationBar>(R.id.bottom_nav)
        bottomNav.setItemSelected(R.id.explorer, true)
    }
}