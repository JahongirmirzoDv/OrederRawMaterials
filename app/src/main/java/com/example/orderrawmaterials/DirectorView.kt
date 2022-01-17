package com.example.orderrawmaterials

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.orderrawmaterials.adapters.TableRv
import com.example.orderrawmaterials.databinding.ActivityDirectorViewBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User
import com.example.orderrawmaterials.utils.FirebaseService
import com.example.orderrawmaterials.utils.Statusbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DecimalFormat

class DirectorView : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var orders_reference: DatabaseReference
    private lateinit var rector_reference: DatabaseReference
    private lateinit var orderList: ArrayList<Data>
    private lateinit var adminsList: ArrayList<User>
    private lateinit var rectorsList: ArrayList<User>
    private lateinit var admins_reference: DatabaseReference
    private val TAG = "DirectorView"
    private lateinit var binding: ActivityDirectorViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectorViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        orderList = ArrayList()
        adminsList = ArrayList()
        rectorsList = ArrayList()

        Statusbar.startStatus(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        orders_reference = firebaseDatabase.getReference("Orders")
        admins_reference = firebaseDatabase.getReference("Admins")

        FirebaseService.getData("Admins").observe(this, {
            adminsList.clear()
            for (i in it) {
                var user = i.getValue(User::class.java)
                if (user != null) {
                    adminsList.add(user)
                }
            }
        })

        orders_reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                val children = snapshot.children
                for (i in children) {
                    val value = i.children
                    value.forEach {
                        val value1 = it.getValue(Data::class.java)
                        if (value1!!.isCompleted) {
                            orderList.add(value1)
                        }
                    }
                }
                val tableRv = TableRv(orderList)
                binding.tableRv.adapter = tableRv
                caculate(orderList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun caculate(orderList: java.util.ArrayList<Data>) {
        var qarz = 0.0
        var naqt = 0.0
        for (it in orderList) {
            if (it.sum_type == "Qarz") {
                var a = it.summ.toString().replace("\\s".toRegex(), "")
                qarz += a.toDouble()
            } else {
                var a = it.summ.toString().replace("\\s".toRegex(), "")
                naqt += a.toDouble()
            }
        }
        val decim = DecimalFormat("#,###")
        binding.name1.text = "${decim.format(naqt)} so'm naqt"
        binding.count1.text = "${decim.format(qarz)} so'm qarz"
    }
}