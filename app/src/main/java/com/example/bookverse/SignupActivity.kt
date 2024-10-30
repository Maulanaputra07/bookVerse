package com.example.bookverse

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookverse.databinding.ActivitySigninBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.link.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun SignUp(){

    }
}