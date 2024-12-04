package com.example.bookverse.fragment

import Books
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.bookverse.databinding.FragmentGenresBinding
import com.example.bookverse.user.DetailBookActivity
import com.example.bookverse.user.SeeAllActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.sql.DataSource

class GenresFragment : Fragment() {
    private lateinit var binding: FragmentGenresBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseStorage: FirebaseStorage
    private val bookList = mutableListOf<Books>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenresBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference.child("genre")
        databaseReference = firebaseDatabase.reference.child("books") // Access the "books" table

        val view = binding.root
        binding.progressBar.visibility = View.VISIBLE

//        searchBooks("") { bookTitles ->
//            displayBooks(bookTitles)
//        }

        val genres = arrayOf("fantasi", "horror", "Aksi", "Komedi", "Romance", "Misteri")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genres)

        for (genre in genres) {
            val imageRef = storageReference.child("$genre.png")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val genreCardView = createCardView(genre, uri.toString())
                binding.linearGenre.addView(genreCardView)

                genreCardView.setOnClickListener {
                    val intent = Intent(requireContext(), SeeAllActivity::class.java).apply {
                        putExtra("listName", genre)
                    }
                    startActivity(intent)
                }
            }.addOnCompleteListener {
                if (it.isComplete) {
                    binding.progressBar.visibility = View.GONE
                }
            }.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
            }
        }

        setupSearchView()

        return view
    }

    private fun setupSearchView() {
        val autoCompleteTextView = binding.autoCompleteTextView // Assuming this is AutoCompleteTextView

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchBooks(query) { bookTitles ->
                        adapter.clear()
                        adapter.addAll(bookTitles)
                        adapter.notifyDataSetChanged()
                        displayBooks(bookTitles)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedBookTitle = parent.getItemAtPosition(position).toString()
            val selectedBook = findBookByTitle(selectedBookTitle)
            if(selectedBook != null){
                val intent = Intent(requireContext(), DetailBookActivity::class.java).apply {
                    putExtra("bookId", selectedBook.id)
                    putExtra("title", selectedBook.judul.toString())
                    putExtra("penulis", selectedBook.penulis.toString())
                    putExtra("tahunTerbit", selectedBook.tahun_terbit.toString())
                    putExtra("cover", selectedBook.cover.toString())
                    putExtra("sinopsis", selectedBook.sinopsis.toString())
                    putExtra("genre", selectedBook.genre.toString())
                }
                startActivity(intent)
            }
        }
    }

    private fun findBookByTitle(title: String): Books? {
        return bookList.firstOrNull { it.judul == title }
    }

    private fun searchBooks(query: String, callback: (List<String>) -> Unit) {

        databaseReference.orderByChild("judul")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookTitles = mutableListOf<String>()
                    for (data in snapshot.children) {
                        val title = data.child("judul").getValue(String::class.java)
                        title?.let { bookTitles.add(it) }


                        val book = data.getValue(Books::class.java)
                        book?.let { bookList.add(it) }
                    }


                    callback(bookTitles)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayBooks(bookTitles: List<String>) {
        binding.linearGenre.removeAllViews()

        if (bookTitles.isNotEmpty()) {
            for (title in bookTitles) {
                val bookCard = createCardViewBook(title)
                binding.linearGenre.addView(bookCard)
            }
        } else {

            Toast.makeText(requireContext(), "No books found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createCardViewBook(title: String): CardView {
        val cardView = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                100
            ).apply { setMargins(8, 8, 8, 8) }
            radius = 12f
            cardElevation = 4f
            setBackgroundColor(Color.WHITE)
        }

        val textView = TextView(requireContext()).apply {
            text = title
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.black, null))
            gravity = Gravity.CENTER
        }

        cardView.addView(textView)
        return cardView
    }

    private fun createCardView(genre: String, imageUrl: String): CardView {
        val cardView = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            radius = 12f
            cardElevation = 4f
        }

        val frameLayout = FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val progressBar = ProgressBar(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            visibility = View.VISIBLE
        }

        val imageView = ImageView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.FIT_XY
            contentDescription = genre

            Glide.with(this@GenresFragment)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(this)
        }

        val textView = TextView(requireContext()).apply {
            text = genre
            textSize = 40f
            setPadding(15, 15, 15, 15)
            setTextColor(resources.getColor(android.R.color.black, null))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
                topMargin = 35
            }
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTypeface(typeface, Typeface.BOLD)
        }

        frameLayout.addView(imageView)
        frameLayout.addView(textView)
        cardView.addView(frameLayout)

        return cardView
    }
}
