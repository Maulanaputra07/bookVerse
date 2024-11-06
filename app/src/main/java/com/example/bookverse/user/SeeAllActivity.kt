package com.example.bookverse.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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

        val idBooks = arrayOf(1, 2, 3, 4, 5)
        val titles = arrayOf("Title 1", "Title 2", "Title 3", "Title 4", "Title 5")
        val images = arrayOf(
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh
        )

        val parentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
//            setBackgroundColor(ContextCompat.getColor(this@SeeAllActivity, R.color.red))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val displayMetrics = resources.displayMetrics
        val cardWidth = (displayMetrics.widthPixels / 2) - 20

        for (i in titles.indices step 2) {
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

            // CardView pertama dengan lebar setengah layar
            val cardView1 = createCardView(titles[i], images[i], idBooks[i])
//            cardView1.layoutParams = LinearLayout.LayoutParams(
//                cardWidth,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
            rowLayout.addView(cardView1)

            // CardView kedua dengan lebar setengah layar jika ada
            if (i + 1 < titles.size) {
                val cardView2 = createCardView(titles[i + 1], images[i + 1], idBooks[i + 1])
//                cardView2.layoutParams = LinearLayout.LayoutParams(
//                    cardWidth,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
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
                val intent = Intent(this, DetailBookActivity::class.java).apply {
                    putExtra("bookId", idBooks[i])
                    putExtra("title", titles[i])
                }
                startActivity(intent)
            }

            // Set onClickListener untuk cardView2 jika ada
            if (i + 1 < titles.size) {
                rowLayout.getChildAt(1)?.setOnClickListener {
                    val intent = Intent(this, DetailBookActivity::class.java).apply {
                        putExtra("bookId", idBooks[i + 1])
                        putExtra("title", titles[i + 1])
                    }
                    startActivity(intent)
                }
            }
        }

        binding.layoutSeeAll.addView(parentLayout)

    }

    private fun createCardView(title: String, image: Int, id: Int): CardView {
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
                400 // Atur sesuai kebutuhan, bisa disesuaikan agar proporsional dengan teks
            ).apply {
                bottomMargin = 8
            }
            setImageResource(image)
            contentDescription = title
        }

        val textView = TextView(this).apply {
            text = title
            textSize = 16f // Atur ukuran teks agar lebih proporsional
            setTextColor(resources.getColor(android.R.color.black, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8 // Memberikan jarak antara gambar dan teks
            }
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER // Agar teks berada di tengah
        }

        innerLayout.addView(imageView)
        innerLayout.addView(textView)
        cardView.addView(innerLayout)

        return cardView
    }


}