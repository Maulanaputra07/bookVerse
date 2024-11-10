package com.example.bookverse.fragment

import Books
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.bookverse.user.DetailBookActivity
import com.example.bookverse.R
import com.example.bookverse.user.SeeAllActivity
import com.example.bookverse.databinding.FragmentHomeBinding
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private val bookList = mutableListOf<Books>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().getReference("books")

        loadbooks()
        loadLatesBook()

        val view = binding.root

        val sharedPreferences = activity?.getSharedPreferences("userSession", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPreferences?.getString("username", "Guest")
        binding.textUsername.text = username

        binding.greetingTime.text = GreetingTime()

        binding.seeAllRec.setOnClickListener {

            val intent = Intent(requireContext(), SeeAllActivity::class.java).apply {
                putExtra("listName", "Rekomendasi")
                putExtra("books", ArrayList(bookList))

            }
            startActivity(intent)
        }

        binding.seeAllNew.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000)

            val recentBooks = ArrayList(bookList.filter { book ->
                book.createDate != null && book.createDate >= oneWeekAgo
            })

            val intent = Intent(requireContext(), SeeAllActivity::class.java).apply {
                putExtra("listName", "Terbaru")
                putExtra("books", recentBooks)
            }
            startActivity(intent)
        }

        return view
    }

    private fun loadLatesBook(){
        databaseReference.orderByChild("createDate").limitToLast(1)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val latesBook = snapshot.children.last().getValue(Books::class.java)
                        if(latesBook != null){
                            displayNotif(latesBook)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun displayNotif(book: Books){
        book.cover.let {
            Glide.with(this)
                .load(it)
                .into(binding.imgNotifBook)
        }

        binding.text2.text = book.sinopsis

        binding.linkNotif.setOnClickListener {
            val intent = Intent(requireContext(), DetailBookActivity::class.java).apply {
                putExtra("bookId", book.id)
                putExtra("title", book.judul)
                putExtra("penulis", book.penulis)
                putExtra("tahunTerbit", book.tahun_terbit.toString())
                putExtra("cover", book.cover)
                putExtra("sinopsis", book.sinopsis)
                putExtra("genre", book.genre)
            }
            startActivity(intent)
        }
    }

    private fun loadbooks(){
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                for(bookSnapShot in snapshot.children){
                    val book = bookSnapShot.getValue(Books::class.java)
                    if(book != null){
                        bookList.add(book)
                    }
                }
                displayBook()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database error: ${error.message}")
                Toast.makeText(requireContext(), "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayBook(){
        binding.linearBookNew.removeAllViews()
        binding.linearBookRec.removeAllViews()

        val currentTime = System.currentTimeMillis()
        val oneWeekAgo = currentTime - (7 * 24 * 60 * 60 * 1000)

        val recentBooks = bookList.filter { book ->
            book.createDate != null && book.createDate >= oneWeekAgo
        }

        for(book in recentBooks){
            val cardView = CreateCardView(book)
            binding.linearBookNew.addView(cardView)
        }

        for(book in bookList){
            val cardViewRec = CreateCardView(book)
            binding.linearBookRec.addView(cardViewRec)
        }
    }

    private fun CreateCardView(book : Books): CardView{
        val cardView = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(380, 500).apply {
                setMargins(15, 15, 15, 15)
            }
            radius = 12f
            cardElevation = 12f
            this.id = id
        }

        val innerLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(15, 15, 15, 15)
        }

        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400
            ).apply {
                bottomMargin = 8
            }
            Glide.with(this)
                .load(book.cover)
                .into(this)
            contentDescription = book.judul
        }

        val textView = TextView(requireContext()).apply {
            Log.d("judulBuku", book.judul.toString())
            text = book.judul
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.black, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        innerLayout.addView(imageView)
        innerLayout.addView(textView)
        cardView.addView(innerLayout)

        cardView.setOnClickListener {
            val intent = Intent(requireContext(), DetailBookActivity::class.java).apply {
                putExtra("bookId", book.id)
                putExtra("title", book.judul)
                putExtra("penulis", book.penulis)
                putExtra("tahunTerbit", book.tahun_terbit.toString())
                putExtra("cover", book.cover)
                putExtra("sinopsis", book.sinopsis)
                putExtra("genre", book.genre)
            }
            startActivity(intent)
        }

        return cardView
    }

    private fun GreetingTime(): String {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        return when (currentHour){
            in 0..11 -> "Selamat Pagi,"
            in 12..15-> "Selamat Siang,"
            in 16..18 -> "Selamat Sore,"
            else -> "Selamat Malam,"
        }


    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

