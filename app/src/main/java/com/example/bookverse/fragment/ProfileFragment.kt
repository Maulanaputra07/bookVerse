package com.example.bookverse.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookverse.LoginActivity
import com.example.bookverse.R
import com.example.bookverse.databinding.FragmentProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPreferences = activity?.getSharedPreferences("userSession", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPreferences?.getString("username", "Guest")
        val name = sharedPreferences?.getString("name", "")
        val email = sharedPreferences?.getString("email", "")

        binding.txtUsername.text = username
        binding.txtName.text = name
        binding.txtEmail.text = email

        binding.btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout(){
        clearLoginStatus()
        Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun clearLoginStatus(){
        val sharedPreferences = activity?.getSharedPreferences("userSession", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
            editor?.clear()
            editor?.apply()
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}