package com.example.bookverse.admin


import Books
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
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivityManageBookBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Console

class ManageBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageBookBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var bookList: MutableList<Books>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManageBookBinding.inflate(layoutInflater)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("books")

        bookList = mutableListOf()

        setContentView(binding.root)

        binding.listBook.layoutManager = LinearLayoutManager(this)
        binding.listBook.adapter = AdapterBuku(bookList)

        LoadBooks()

        binding.addBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }
    }

    private fun LoadBooks(){
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()

                for(bookSnapShot in snapshot.children){
                    val book = bookSnapShot.getValue(Books::class.java)
                    if(book != null){
                        bookList.add(book)
                    }
                }

                binding.listBook.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageBookActivity, "Gagal memuat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    class AdapterBuku(val dataBuku: List<Books>) : RecyclerView.Adapter<AdapterBuku.HolderBuku>(){
        class HolderBuku(val viewItem: View) : RecyclerView.ViewHolder(viewItem){
            fun BindData(data: Books){
                val title = viewItem.findViewById<TextView>(R.id.titleBook)
                val author = viewItem.findViewById<TextView>(R.id.Penulis)
                val year = viewItem.findViewById<TextView>(R.id.TahunTerbit)
                val cover = viewItem.findViewById<ImageView>(R.id.imgBook)

                title.text = data.judul
                author.text = data.penulis
                year.text = data.tahun_terbit.toString()
                Log.d("Cover URL", "Cover image URL: ${data.cover}")
                Log.d("idNyaa", data.id.toString())

//

                val coverUrl = data.cover // Pastikan ini berisi URL yang benar dan tidak null
                if (coverUrl != null) {
                    Glide.with(viewItem.context)
                        .load(coverUrl)
                        .into(cover)
                } else {
                    Log.e("GlideError", "Cover image URL is null")
                    cover.setImageResource(R.drawable.teka_teki_rumah_aneh)  // Set default image
                }

                val btnDelete = viewItem.findViewById<Button>(R.id.btnDelete)
                val btnEdit = viewItem.findViewById<Button>(R.id.btnEdit)

                btnEdit.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val bookEdit = data

                        val intent = Intent(viewItem.context, AddBookActivity::class.java)
                        intent.putExtra("BookId", bookEdit.id)
                        Log.d("debugIdbuku", bookEdit.id?:"null")
                        intent.putExtra("Judul", bookEdit.judul)
                        intent.putExtra("sinopsis", bookEdit.sinopsis)
                        intent.putExtra("genre", bookEdit.genre)
                        intent.putExtra("cover", bookEdit.cover)
                        intent.putExtra("penulis", bookEdit.penulis)
                        intent.putExtra("tahunTerbit", bookEdit.tahun_terbit)
                        intent.putExtra("status", "Edit buku")
                        viewItem.context.startActivity(intent);
                    }
                }


                btnDelete.setOnClickListener {
                    val position = adapterPosition

                    if(position != RecyclerView.NO_POSITION){
                        val bookId = data.id
                        deleteBooksFromDatabase(bookId?: "")
                    }
                }
            }

            fun deleteBooksFromDatabase(bookId: String){
                val databaseReference = FirebaseDatabase.getInstance().getReference("books")
                databaseReference.child(bookId).removeValue().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Log.d("DeleteBook", "Buku berhasil dihapus")
                    }else{
                        Log.e("DeleteBook", "Gagal menghapus buku", task.exception)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBuku {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.items_book, parent, false)
            return HolderBuku(view)
        }

        override fun getItemCount(): Int {
            return dataBuku.size
        }

        override fun onBindViewHolder(holder: HolderBuku, position: Int) {
            holder.BindData(dataBuku[position])
        }
    }
}