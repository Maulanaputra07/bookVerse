package com.example.bookverse.admin

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityAddUserBinding
import com.example.bookverse.model.UserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddUserBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialogLoading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddUserBinding.inflate(layoutInflater)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        setContentView(binding.root)

        val staticGenres = listOf("admin", "member" )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, staticGenres)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.listRole.adapter = adapter

        dialogLoading = Dialog(this)
        dialogLoading.window?.setBackgroundDrawable(getDrawable(R.drawable.notif_card))
        dialogLoading.setCancelable(true)

        binding.btnSave.setOnClickListener {
                SaveData()
//            if(status != "Edit genre"){

//            }else{
//                if (genreId != null) {
//                    UpdateGenreData(genreId)
//                }
//            }
//        }

        }

    }

    private fun SaveData(){
        dialogLoading.setContentView(R.layout.loading_alert)
        dialogLoading.show()
        val id = databaseReference.push().key
        val username = binding.etUsername.text.toString()
        val nama = binding.etName.text.toString()
        val role = binding.listRole.selectedItem.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val usersData = UserData(
            id = id,
            username = username,
            name = nama,
            role = role,
            email = email,
            password = password
        )

        id?.let {
            databaseReference.child(it).setValue(usersData)
                .addOnSuccessListener {
                    dialogLoading.hide()
                    Toast.makeText(this, "Berhasil menambahkan user baru", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal untuk menambahkan user", Toast.LENGTH_SHORT).show()
//                    finish()
                }
        }
    }
}