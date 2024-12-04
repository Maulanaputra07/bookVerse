package com.example.bookverse.admin

import Books
import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.bookverse.Notification
import com.example.bookverse.R
import com.example.bookverse.channelID
import com.example.bookverse.databinding.ActivityAddBookBinding
import com.example.bookverse.messageExtra
import com.example.bookverse.notificationId
import com.example.bookverse.titleExtra
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.sin

class AddBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var dialogLoading: Dialog

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference.child("cover")
        databaseReference = firebaseDatabase.reference.child("books")

        setContentView(binding.root)

//        createNotificationChannel()


        dialogLoading = Dialog(this)
        dialogLoading.window?.setBackgroundDrawable(getDrawable(R.drawable.notif_card))
        dialogLoading.setCancelable(true)

        val bookId = intent.getStringExtra("BookId")
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
            if(status == "Edit buku"){
            Log.d("statuss", status.toString())
                if(bookId != null){
                    Log.d("idBuku", bookId.toString())
                    if(imageUri != null){
                        updateImage(imageUri!!, bookId)
                    }else{
                        UpdateBookData(bookId, bookCover.toString())
                    }
                }
            }else{
                if (imageUri != null) {
                    uploadImage(imageUri!!)
                } else {
                    Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateImage(uri: Uri, bookId: String){
        dialogLoading.setContentView(R.layout.loading_alert)
        dialogLoading.show()
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
                dialogLoading.hide()
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
        dialogLoading.setContentView(R.layout.loading_alert)
        dialogLoading.show()
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
                dialogLoading.setContentView(R.layout.failed_alert)
                dialogLoading.show()
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
            tahun_terbit = tahunTerbit,
            createDate = System.currentTimeMillis()
        )
        bookId?.let {
            databaseReference.child(it).setValue(bookData)
                .addOnSuccessListener {
                    dialogLoading.hide()
                    Toast.makeText(this, "Berhasil menambahkan buku", Toast.LENGTH_SHORT).show()
                    finish()
                    scheduleNotification()
                }
                .addOnFailureListener {
                    dialogLoading.hide()
                    Toast.makeText(this, "Gagal menambahkan buku", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "aaaa"
        val message = "upupup"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val time = getTime()
    }

//    private fun getTime(): Long {
//        val minute =
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "notif channel"
        val desc = "cihuyy"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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