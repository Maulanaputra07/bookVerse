package com.example.bookverse.user

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bookverse.databinding.ActivityDetailBookBinding

class DetailBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBookBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBookBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bookId = intent.getIntExtra("bookId", -1) // Mengambil ID buku
        val title = intent.getStringExtra("title") // Mengambil judul buku
        val sinopsis = intent.getStringExtra("sinopsis")
        val cover = intent.getStringExtra("cover")
        val genre = intent.getStringExtra("genre")
        val tahunTerbit = intent.getStringExtra("tahunTerbit").toString()
        Log.d("tahun", tahunTerbit)
        val penulis = intent.getStringExtra("penulis")

        // Mengatur nilai ke TextView
//        binding.idBook.text = bookId.toString()
        binding.titleBook.text = title // Langsung mengatur judul
        binding.sinopsis.text = sinopsis
        cover.let {
            Glide.with(this)
                .load(it)
                .into(binding.img)
        }
        binding.tvGenre.text = genre
        binding.tvTahunTerbit.text = tahunTerbit
        binding.tvPenulis.text = penulis

    }
}