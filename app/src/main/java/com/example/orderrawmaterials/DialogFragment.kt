package com.example.orderrawmaterials

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.orderrawmaterials.databinding.FragmentDialogBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DialogFragment : androidx.fragment.app.DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentDialogBinding
    private val TAG = "DialogFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDialogBinding.inflate(inflater, container, false)

        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("Directors")

        binding.save.setOnClickListener {
            val number = binding.phoneInput.text.toString()
            if (number.length > 7) {
                val key = reference.push().key
                reference.child(key!!).setValue(number)
                Toast.makeText(requireContext(), "Saqalndi", Toast.LENGTH_SHORT).show()
                dialog?.cancel()
            } else {
                Toast.makeText(requireContext(), "Bo'sh", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cancel.setOnClickListener {
            dialog?.cancel()
        }



        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}