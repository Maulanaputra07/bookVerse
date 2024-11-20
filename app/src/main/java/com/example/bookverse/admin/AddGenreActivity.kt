package com.example.bookverse.admin

import Genre
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityAddGenreBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddGenreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGenreBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialogLoading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGenreBinding.inflate(layoutInflater)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("genres")

        setContentView(binding.root)
        dialogLoading = Dialog(this)
        dialogLoading.window?.setBackgroundDrawable(getDrawable(R.drawable.notif_card))
        dialogLoading.setCancelable(true)

        val status = intent.getStringExtra("status")
        val genreId = intent.getStringExtra("genreId")
        val genreTitle = intent.getStringExtra("Judul")

        binding.etJudul.setText(genreTitle)
        binding.btnSave.text = status?:"Tambah genre"
        binding.title.text = status?:"Tambah genre"

        binding.btnSave.setOnClickListener {
            if(status != "Edit genre"){
                SaveGenreData()
            }else{
                if (genreId != null) {
                    UpdateGenreData(genreId)
                }
            }
        }
    }

    private fun SaveGenreData(){
        dialogLoading.setContentView(R.layout.loading_alert)
        dialogLoading.show()
        val judul = binding.etJudul.text.toString()
        val genreId = databaseReference.push().key
        val bookData = Genre(
            id = genreId,
            genre_name = judul
        )
        genreId?.let {
            databaseReference.child(it).setValue(bookData)
                .addOnSuccessListener {
                    dialogLoading.hide()
                    Toast.makeText(this, "Berhasil menambahkan genre", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menambahkan genre", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun UpdateGenreData(genreId: String){
        dialogLoading.setContentView(R.layout.loading_alert)
        dialogLoading.show()
        val judul = binding.etJudul.text.toString()
        val genreData = Genre(
            id = genreId,
            genre_name = judul,
        )
        databaseReference.child(genreId).setValue(genreData)
            .addOnSuccessListener {
                dialogLoading.hide()
                Toast.makeText(this, "berhasil edit genre", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal edit genre", Toast.LENGTH_SHORT).show()
            }
    }
}