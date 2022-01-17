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

class DialogFragment : androidx.fragment.app.DialogFragment() {
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference

    lateinit var binding: FragmentDialogBinding
    private val TAG = "DialogFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
}