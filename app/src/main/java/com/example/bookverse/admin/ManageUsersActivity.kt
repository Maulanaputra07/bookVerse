package com.example.bookverse.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityManageUsersBinding
import com.example.bookverse.model.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageUsersBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userList: MutableList<UserData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        userList = mutableListOf()
        setContentView(binding.root)

        binding.listUsers.layoutManager = LinearLayoutManager(this)
        binding.listUsers.adapter = AdapterUser(userList)

        LoadUsers()

        binding.addUsers.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }
    }

    private fun LoadUsers(){
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for(userSnapShost in snapshot.children){
                    val users = userSnapShost.getValue(UserData::class.java)
                    if(users != null){
                        userList.add(users)
                    }
                }

                binding.listUsers.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageUsersActivity, "Gagal memuat", Toast.LENGTH_SHORT).show()
            }

        })
    }

    class AdapterUser(val dataUsers: List<UserData>) : RecyclerView.Adapter<AdapterUser.HolderUser>(){
        class HolderUser(val viewItem : View): RecyclerView.ViewHolder(viewItem){
            fun bindData(data: UserData){
                val username = viewItem.findViewById<TextView>(R.id.Username)
                username.text = data.username

                val btnEdit = viewItem.findViewById<Button>(R.id.btnEditUser)
                val btnDelete = viewItem.findViewById<Button>(R.id.btnDeleteUser)

                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val userEdit = data

                        val intent = Intent(viewItem.context, AddUserActivity::class.java)
                        intent.putExtra("userId", userEdit.id)
                        intent.putExtra("Username", userEdit.username)
                        intent.putExtra("name", userEdit.name)
                        intent.putExtra("role", userEdit.role)
                        intent.putExtra("password", userEdit.password)
                        intent.putExtra("email", userEdit.email)
                        viewItem.context.startActivity(intent);
                    }
                }

                btnDelete.setOnClickListener {

                }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUser {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.items_users, parent, false)
            return HolderUser(view)
        }

        override fun getItemCount(): Int {
            return dataUsers.size
        }

        override fun onBindViewHolder(holder: HolderUser, position: Int) {
            holder.bindData(dataUsers[position])
        }
    }
}