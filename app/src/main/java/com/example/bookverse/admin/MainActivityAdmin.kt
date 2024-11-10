package com.example.bookverse.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookverse.LoginActivity
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityMainAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivityAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdminBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)

        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().getReference("books")

        fetchBooksCount()

        binding.btnLogout.setOnClickListener {
            logout()
        }

//        binding.totalBuku.text =

        binding.layoutBuku.setOnClickListener {
            startActivity(Intent(this, ManageBookActivity::class.java))
        }
    }

    private fun fetchBooksCount(){
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookCount = snapshot.childrenCount
                Log.d("total buku", bookCount.toString())
                binding.totalBuku.text = bookCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivityAdmin, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout(){
        clearLoginStatus()
        Toast.makeText(this@MainActivityAdmin, "Logout berhasil", Toast.LENGTH_SHORT).show()

        val intent = Intent(this@MainActivityAdmin, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun clearLoginStatus(){
        val sharedPreferences = getSharedPreferences("userSession", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.clear()
        editor?.apply()
    }
}