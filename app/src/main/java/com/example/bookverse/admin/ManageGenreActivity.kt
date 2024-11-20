package com.example.bookverse.admin

import Genre
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookverse.R
import com.example.bookverse.admin.ManageBookActivity.AdapterBuku
import com.example.bookverse.databinding.ActivityManageGenreBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageGenreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageGenreBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var genreList: MutableList<Genre>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageGenreBinding.inflate(layoutInflater)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("genres")

        genreList = mutableListOf()
        setContentView(binding.root)

        binding.listGenre.layoutManager = LinearLayoutManager(this)
        binding.listGenre.adapter = AdapterGenre(genreList)

        LoadGenres()

        binding.addGenre.setOnClickListener {
            startActivity(Intent(this, AddGenreActivity::class.java))
        }

    }

    private fun LoadGenres(){
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                genreList.clear()

                for(genreSnapShot in snapshot.children){
                    val genre = genreSnapShot.getValue(Genre::class.java)
                    if(genre != null){
                        genreList.add(genre)
                    }
                }

                binding.listGenre.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageGenreActivity, "Gagal memuat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class AdapterGenre(val dataGenre: List<Genre>) : RecyclerView.Adapter<AdapterGenre.HolderGenre>(){
        class HolderGenre(val viewItem: View) : RecyclerView.ViewHolder(viewItem){
            fun BindData(data: Genre){
                val title = viewItem.findViewById<TextView>(R.id.titleGenre)

                title.text = data.genre_name

                val btnDelete = viewItem.findViewById<Button>(R.id.btnDelete)
                val btnEdit = viewItem.findViewById<Button>(R.id.btnEdit)

                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val genreEdit = data

                        val intent = Intent(viewItem.context, AddGenreActivity::class.java)
                        intent.putExtra("genreId", genreEdit.id)
                        intent.putExtra("Judul", genreEdit.genre_name)
                        intent.putExtra("status", "Edit genre")
                        viewItem.context.startActivity(intent);
                    }
                }

                btnDelete.setOnClickListener {
                    val position = adapterPosition

                    if(position != RecyclerView.NO_POSITION){
                        val genreId = data.id
                        deleteBooksFromDatabase(genreId?: "")
                    }
                }
            }

            fun deleteBooksFromDatabase(genreId: String){
                val databaseReference = FirebaseDatabase.getInstance().getReference("genres")
                databaseReference.child(genreId).removeValue().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Log.d("DeleteBook", "Genre berhasil dihapus")
                    }else{
                        Log.e("DeleteBook", "Gagal menghapus genre", task.exception)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderGenre {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.items_genre, parent, false)
            return HolderGenre(view)
        }

        override fun getItemCount(): Int {
            return dataGenre.size
        }

        override fun onBindViewHolder(holder: HolderGenre, position: Int) {
            holder.BindData(dataGenre[position])
        }
    }
}