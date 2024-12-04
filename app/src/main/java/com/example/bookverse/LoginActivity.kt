package com.example.bookverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookverse.admin.MainActivityAdmin
import com.example.bookverse.databinding.ActivityLoginBinding
import com.example.bookverse.model.UserData
import com.example.bookverse.user.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        checkLoginStatus()

        binding.link.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()){
                Login(username, password)
            }else{
                Toast.makeText(this, "harap isi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Login(username: String, password: String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val userData = userSnapshot.getValue(UserData::class.java)

                        if(userData != null && userData.password == password){
                            saveLoginStatus(userData.id.toString() ,userData.username.toString(), userData.role.toString(), userData.name.toString(), userData.email.toString())
                            checkLoginStatus()
                            return
                        }
                    }
                    Toast.makeText(this@LoginActivity, "gagal login", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveLoginStatus(id: String, username: String, role: String, name: String, email:String) {
        val sharedPref = getSharedPreferences("userSession", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("id", id)
        editor.putString("username", username)
        editor.putString("role", role)
        editor.putString("name", name)
        editor.putString("email", email)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun checkLoginStatus() {
        val sharedPref = getSharedPreferences("userSession", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        var role = sharedPref.getString("role", "")

        if (isLoggedIn && role == "member") {
            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else if(isLoggedIn && role == "admin"){
            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivityAdmin::class.java))
            finish()
        }
    }

}