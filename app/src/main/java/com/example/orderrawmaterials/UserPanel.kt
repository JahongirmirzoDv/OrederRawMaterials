package com.example.orderrawmaterials

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.example.orderrawmaterials.adapters.AdminUsersRvAdapter
import com.example.orderrawmaterials.adapters.UsersRvAdapter
import com.example.orderrawmaterials.databinding.ActivityControlPanelBinding
import com.example.orderrawmaterials.databinding.BottomDialogBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserPanel : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var orders_reference: DatabaseReference
    lateinit var binding: ActivityControlPanelBinding
    private val TAG = "UserPanel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

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