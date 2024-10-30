package com.example.bookverse.fragment

import android.content.Intent
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActivityChooserView.InnerLayout
import androidx.cardview.widget.CardView
import androidx.core.view.marginTop
import com.example.bookverse.DetailBookActivity
import com.example.bookverse.R
import com.example.bookverse.databinding.FragmentHomeBinding
import java.time.LocalTime

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPreferences?.getString("username", "Guest")
        binding.textUsername.text = username

        binding.greetingTime.text = GreetingTime()

        // Data contoh
        val idBooks = arrayOf(1, 2, 3, 4, 5)
        val titles = arrayOf("Title 1", "Title 2", "Title 3", "Title 4", "Title 5")
        val images = arrayOf(
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh,
            R.drawable.teka_teki_rumah_aneh
        )



        // Looping untuk menambahkan card
        for (i in titles.indices) {
            val cardViewRec = CreateCardView(titles[i], images[i], idBooks[i])
            val cardViewNew = CreateCardView(titles[i], images[i], idBooks[i])
            binding.linearBookRec.addView(cardViewRec)
            binding.linearBookNew.addView(cardViewNew)

            cardViewRec.setOnClickListener {
                val intent = Intent(requireContext(), DetailBookActivity::class.java).apply {
                    putExtra("bookId", idBooks[i])
                    putExtra("title", titles[i])
                }
                startActivity(intent)
            }

            cardViewNew.setOnClickListener {
                val intent = Intent(requireContext(), DetailBookActivity::class.java).apply {
                    putExtra("bookId", idBooks[i])
                }
                startActivity(intent)
            }


        }

        return view
    }

    private fun CreateCardView(title: String, image: Int, id: Int): CardView{
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
            setImageResource(image)
            contentDescription = title
        }

        val textView = TextView(requireContext()).apply {
            text = title
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

        return cardView
    }

    private fun GreetingTime(): String {
        val currentHour = LocalTime.now().hour

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

