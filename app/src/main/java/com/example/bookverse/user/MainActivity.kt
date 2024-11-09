package com.example.bookverse.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityMainBinding
import com.example.bookverse.fragment.GenresFragment
import com.example.bookverse.fragment.HomeFragment
import com.example.bookverse.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        gotoFragment(HomeFragment())

        binding.btnBook.setOnClickListener {
            gotoFragment(GenresFragment())
        }

        binding.btnHome.setOnClickListener {
            gotoFragment(HomeFragment())
        }

        binding.btnProfile.setOnClickListener {
            gotoFragment(ProfileFragment())
        }

//        binding.btnSaved.setOnClickListener {
//            gotoFragment(SavedFragment())
//        }

    }

    private fun gotoFragment(fragment: Fragment){
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.containerFragment, fragment).commit()
    }
}