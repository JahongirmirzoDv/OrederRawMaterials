package com.example.orderrawmaterials

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toolbar
import com.example.orderrawmaterials.adapters.AdminUsersRvAdapter
import com.example.orderrawmaterials.adapters.UsersRvAdapter
import com.example.orderrawmaterials.databinding.ActivityAdminPanelBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminPanel : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var orders_reference: DatabaseReference
    lateinit var binding: ActivityAdminPanelBinding
    lateinit var m1: ArrayList<Data>
    lateinit var m2: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
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
        orders_reference = firebaseDatabase.getReference("Users")

        getValue()
        binding.add.setOnClickListener {
            var ft = supportFragmentManager.beginTransaction()
            var fg = DialogFragment()
            fg.show(ft, "dialog")
        }
    }

    private fun getValue() {

        orders_reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                m2 = ArrayList()
                val children = snapshot.children
                m2.clear()
                for (i in children) {
                    val value = i.getValue(User::class.java)!!
                    m2.add(value)
                }
                val adminUsersRvAdapter =
                    AdminUsersRvAdapter(m2, object : AdminUsersRvAdapter.onPress {
                        override fun onClick(user: User, position: Int) {
                            var intent = Intent(this@AdminPanel, AdminEditPAnel::class.java)
                            intent.putExtra("s", user.uid)
                            startActivity(intent)
                        }
                    })
                binding.adminRv.adapter = adminUsersRvAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun addDirector() {

    }
}