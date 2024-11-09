package com.example.bookverse.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityAddBookBinding
import com.example.bookverse.model.Books
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.sin

class AddBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference.child("cover")
        databaseReference = firebaseDatabase.reference.child("books")

        setContentView(binding.root)

        val bookId = intent.getStringExtra("bookId")
        val bookTitle = intent.getStringExtra("Judul")
        val sinopsis = intent.getStringExtra("sinopsis")
        val genre = intent.getStringExtra("genre")
        val bookAuthor = intent.getStringExtra("penulis")
        val bookYear = intent.getIntExtra("tahunTerbit", 0)
        val bookCover = intent.getStringExtra("cover")
        val status = intent.getStringExtra("status")


        binding.imgCover.setOnClickListener {
            Toast.makeText(this, "img clicked", Toast.LENGTH_SHORT).show()
        }

        val staticGenres = listOf("Misteri", "Horror", "Action", "Comedy", "Romance" )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, staticGenres)

        binding.btnSave.text = status?:"Tambah buku"
        binding.title.text = status?:"Tambah buku"
        binding.etJudul.setText(bookTitle)
        binding.etSinopsis.setText(sinopsis)
        genre.let {
            val position = staticGenres.indexOf(it)
            if(position >= 0){
                binding.listGenre.setSelection(position)
            }
        }

        bookCover?.let {
            Glide.with(this)
                .load(it)
                .into(binding.imgCover)
        }

        binding.etPenulis.setText(bookAuthor)
        binding.etTahunTerbit.setText(bookYear.toString())

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.listGenre.adapter = adapter

        binding.listGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentView:  AdapterView<*>, view: View?, position: Int, id: Long){
                val selectedData = parentView.getItemAtPosition(position) as String
//                log.d()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle jika tidak ada yang dipilih
            }
        }

        binding.btnSelect.setOnClickListener {
            SelectImage()
        }

        binding.btnSave.setOnClickListener {
            if(status != "Edit buku"){
                if (imageUri != null) {
                    uploadImage(imageUri!!)
                } else {
                    Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }else{
                if(bookId != null){
                    if(imageUri != null){
                        updateImage(imageUri!!, bookId)
                    }else{
                        UpdateBookData(bookId, bookCover.toString())
                    }
                }
            }
        }
    }

    private fun updateImage(uri: Uri, bookId: String){
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val fireReference = storageReference.child(fileName)
        fireReference.putFile(uri)
            .addOnSuccessListener { task ->
                task.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    uri ->
                    val imageUrl = uri.toString()
                    UpdateBookData(bookId, imageUrl)
                    Toast.makeText(this, "Berhasil edit cover buku", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                ex ->
                Toast.makeText(this, "Gagal edit cover buku: "+ex.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun UpdateBookData(bookId: String, imageUrl: String){
        val judul = binding.etJudul.text.toString()
        val sinopsis = binding.etSinopsis.text.toString()
        val penulis = binding.etPenulis.text.toString()
        val genre = binding.listGenre.selectedItem.toString()
        val tahunTerbit = binding.etTahunTerbit.text.toString().toIntOrNull()

        val bookData = Books(
            id = bookId,
            judul = judul,
            sinopsis = sinopsis,
            penulis = penulis,
            genre = genre,
            cover = imageUrl,
            tahun_terbit = tahunTerbit
        )

        databaseReference.child(bookId).setValue(bookData)
            .addOnSuccessListener {
                Toast.makeText(this, "berhasil edit buku", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal edit buku", Toast.LENGTH_SHORT).show()
            }
    }

    private fun SelectImage(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    private fun uploadImage(uri: Uri){
//        storageReference.putFile()
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val fireReference = storageReference.child(fileName)
        fireReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    SaveBookData(imageUrl)
                    Toast.makeText(this, "Gambar berhasil di upload", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "gagal upload errorl: " + exception.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun SaveBookData(imageUrl :String){
        val judul = binding.etJudul.text.toString()
        val sinopsis = binding.etSinopsis.text.toString()
        val penulis = binding.etPenulis.text.toString()
        val genre = binding.listGenre.selectedItem.toString()
        val tahunTerbit = binding.etTahunTerbit.text.toString().toIntOrNull()

        val bookId = databaseReference.push().key

        val bookData = Books(
            id = bookId,
            judul = judul,
            sinopsis = sinopsis,
            penulis = penulis,
            genre = genre,
            cover = imageUrl,
            tahun_terbit = tahunTerbit
        )

        bookId?.let {
            databaseReference.child(it).setValue(bookData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil menambahkan buku", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menambahkan buku", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            binding.imgCover.setImageURI(imageUri)
            // Lakukan sesuatu dengan imageUri, seperti menampilkannya di ImageView
        }
    }
}