package com.example.orderrawmaterials

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.orderrawmaterials.adapters.UsersRvAdapter
import com.example.orderrawmaterials.databinding.ActivityControlPanelBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.utils.Statusbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserPanel : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var orders_reference: DatabaseReference
    private lateinit var binding: ActivityControlPanelBinding
    private val TAG = "UserPanel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Statusbar.startStatus(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        orders_reference = firebaseDatabase.getReference("Orders")

        binding.add.setOnClickListener {
            startActivity(Intent(this, Add::class.java))
            Log.d(TAG, "onCreate: ${firebaseAuth.currentUser!!.uid}")
        }
        getValue()
    }


    private fun getValue() {
        orders_reference.addValueEventListener(object : ValueEventListener {
            var m1 = arrayListOf<Data>()
            override fun onDataChange(snapshot: DataSnapshot) {
                m1.clear()
                val children = snapshot.children
                for (i in children) {
                    val value = i.children
                    if (i.key == firebaseAuth.currentUser!!.uid) {
                        value.forEach {
                            val value1 = it.getValue(Data::class.java)
                            m1.add(value1!!)
                        }
                    }
                }
                val usersRvAdapter =
                    UsersRvAdapter(m1, this@UserPanel, object : UsersRvAdapter.onPress {
                        override fun onClick(data: Data, position: Int) {

                        }
                    })
                binding.userRv.adapter = usersRvAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}