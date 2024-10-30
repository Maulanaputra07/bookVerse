package com.example.bookverse

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookverse.databinding.ActivitySignupBinding
import com.example.bookverse.model.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.link.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignup.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(username.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                if(password.length >= 8){
                    SignUp(username, name, email, password)
                }else{
                    Toast.makeText(this, "password minimal 8 karakter", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "harap isi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun SignUp(username:String, name:String, email:String, password:String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    val id = databaseReference.push().key ?: return
                    val role = ""
                    val userData = UserData(id, username, name, email, password, role)
                    databaseReference.child(id).setValue(userData).apply {
                        Toast.makeText(this@SignupActivity, "Berhasil melakukan signup", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        finish()
                    }
                }else{
                    Toast.makeText(this@SignupActivity, "user telah digunakan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignupActivity, "database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}