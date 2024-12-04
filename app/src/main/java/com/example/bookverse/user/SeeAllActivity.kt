package com.example.bookverse.user

import Books
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.bookverse.R
import com.example.bookverse.databinding.ActivitySeeAllBinding

class SeeAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeeAllBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("listName")
        binding.listName.text = title

        val books: ArrayList<Books>? = intent.getParcelableArrayListExtra("books")
        Log.d("tagBuku", books.toString())

        val parentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val displayMetrics = resources.displayMetrics
        val cardWidth = (displayMetrics.widthPixels / 2) - 20

        books?.let {
            for (i in it.indices step 2) {
                val rowLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(10, 10, 10, 10)
                        setPadding(10, 10, 10, 10)
                    }
                }

                // CardView pertama
                val book1 = it[i]
                val cardView1 = createCardView(book1.judul.toString(), book1.cover.toString())
                rowLayout.addView(cardView1)

                // CardView kedua jika ada
                if (i + 1 < it.size) {
                    val book2 = it[i + 1]
                    val cardView2 = createCardView(book2.judul.toString(), book2.cover.toString())
                    rowLayout.addView(cardView2)
                } else {
                    // Tambahkan View kosong jika hanya ada satu CardView di baris ini
                    val emptyView = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(cardWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                    }
                    rowLayout.addView(emptyView)
                }

                // Tambahkan rowLayout ke parentLayout
                parentLayout.addView(rowLayout)

                // Set onClickListener untuk cardView1
                cardView1.setOnClickListener {
                    openDetailBookActivity(book1)
                }

                // Set onClickListener untuk cardView2 jika ada
                if (i + 1 < it.size) {
                    val book2 = it[i + 1]
                    rowLayout.getChildAt(1)?.setOnClickListener {
                        openDetailBookActivity(book2)
                    }
                }
            }
        }

        binding.layoutSeeAll.addView(parentLayout)
    }

    private fun createCardView(judul: String, imageResource: String): CardView {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 8, 8, 8)
            }
            radius = 12f
            cardElevation = 8f
            this.id = id
        }

        val innerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(8, 8, 8, 8)
        }

        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                400
            ).apply {
                bottomMargin = 8
            }
            Glide.with(this)
                .load(imageResource)
                .into(this)
            contentDescription = judul
        }

        val textView = TextView(this).apply {
            text = judul
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }

        innerLayout.addView(imageView)
        innerLayout.addView(textView)
        cardView.addView(innerLayout)

        return cardView
    }

    private fun openDetailBookActivity(book: Books) {
        val intent = Intent(this, DetailBookActivity::class.java).apply {
            putExtra("bookId", book.id.toString())
            putExtra("title", book.judul.toString())
            putExtra("penulis", book.penulis.toString())
            putExtra("tahunTerbit", book.tahun_terbit.toString())
            putExtra("cover", book.cover.toString())
            putExtra("sinopsis", book.sinopsis.toString())
            putExtra("genre", book.genre.toString())
        }
        startActivity(intent)
    }
}
