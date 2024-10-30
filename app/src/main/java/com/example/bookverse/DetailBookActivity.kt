package com.example.bookverse

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookverse.databinding.ActivityDetailBookBinding

class DetailBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBookBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBookBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bookId = intent.getIntExtra("bookId", -1) // Mengambil ID buku
        val title = intent.getStringExtra("title") // Mengambil judul buku

        // Mengatur nilai ke TextView
        binding.idBook.text = bookId.toString() // Mengubah ID menjadi String
        binding.titleBook.text = title // Langsung mengatur judul

    }
}