package com.example.bookverse.user

import Borrow
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityDetailBookBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBookBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBookBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("userSession", AppCompatActivity.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", null)
        Log.d("sodap", id.toString())

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("borrowbooks")

        val bookId = intent.getStringExtra("bookId"?: null)
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

        val dialogSuccess = Dialog(this)
        dialogSuccess.setContentView(R.layout.success_alert)
        dialogSuccess.window?.setBackgroundDrawable(getDrawable(R.drawable.notif_card))
        dialogSuccess.setCancelable(true)

        binding.btnPinjambuku.setOnClickListener {

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            val borrowData = Borrow(
                id = databaseReference.push().key,
                id_user = id.toString(),
                id_book = bookId.toString(),
                date_borrow = formattedDate
            )

            id?.let {
                borrowData.id?.let { key ->
                    databaseReference.child(key).setValue(borrowData)
                        .addOnSuccessListener {
                            dialogSuccess.show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                dialogSuccess.dismiss() // Dismiss the dialog
                                finish() // Close the current activity
                            }, 1500)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}