package com.example.bookverse.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookverse.LoginActivity
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityMainAdminBinding

class MainActivityAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.layoutBuku.setOnClickListener {
            startActivity(Intent(this, ManageBookActivity::class.java))
        }
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