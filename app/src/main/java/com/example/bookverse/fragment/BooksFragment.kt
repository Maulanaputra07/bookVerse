package com.example.bookverse.fragment

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.bookverse.R
import com.example.bookverse.user.SeeAllActivity
import com.example.bookverse.databinding.FragmentBooksBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BooksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BooksFragment : Fragment() {
    private lateinit var binding: FragmentBooksBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBooksBinding.inflate(inflater, container, false)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        databaseReference = firebaseDatabase.reference.child("genre")
        storageReference = firebaseStorage.reference.child("genre")


        val view = binding.root
        binding.progressBar.visibility = View.VISIBLE

        val genres = arrayOf("fantasi", "horror", "Aksi", "Komedi", "Romance", "Misteri", "tes", "1", "2")
        val images = arrayOf(R.drawable.fantasi, R.drawable.fantasi, R.drawable.fantasi, R.drawable.fantasi, R.drawable.fantasi, R.drawable.fantasi, R.drawable.fantasi, R.drawable.fantasi, R.drawable.fantasi)

        for(genre in genres){
            val imageRef = storageReference.child("$genre.png")

            imageRef.downloadUrl.addOnSuccessListener {
                uri ->
                val genresList  = CreateCardView(genre, uri.toString())
                binding.linearGenre.addView(genresList)

                genresList.setOnClickListener {
                    val intent = Intent(requireContext(), SeeAllActivity::class.java).apply {
                        putExtra("listName", genre)
                    }
                    startActivity(intent)
                }

            }.addOnCompleteListener {
                if(it.isComplete){
                    binding.progressBar.visibility = View.GONE
                }
            }.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
            }
        }

        return view
    }

    private fun CreateCardView(genre: String, imageUrl: String): CardView {
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

            Glide.with(this@BooksFragment)
                .load(imageUrl)
//                .placeholder(R.drawabl)
//                .error()
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
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
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
//        frameLayout.addView(progressBar)
        cardView.addView(frameLayout)

        return cardView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BooksFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BooksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}