package com.example.orderrawmaterials

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.example.orderrawmaterials.databinding.ActivityDirectorViewBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.example.orderrawmaterials.adapters.TableRv
import com.example.orderrawmaterials.notification.APIService
import com.example.orderrawmaterials.notification.Client
import com.example.orderrawmaterials.notification.models.NotifyData
import com.example.orderrawmaterials.notification.models.Responce
import com.example.orderrawmaterials.notification.models.Sender
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class DirectorView : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var orders_reference: DatabaseReference
    lateinit var rector_reference: DatabaseReference
    lateinit var orderList: ArrayList<Data>
    lateinit var adminsList: ArrayList<User>
    lateinit var rectorsList: ArrayList<User>
    lateinit var admins_reference: DatabaseReference
    private val TAG = "DirectorView"
    lateinit var binding: ActivityDirectorViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectorViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        orderList = ArrayList()
        adminsList = ArrayList()
        rectorsList = ArrayList()

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
        admins_reference = firebaseDatabase.getReference("Admins")


        admins_reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                adminsList.clear()
                for (i in children) {
                    val value = i.getValue(User::class.java)!!
                    adminsList.add(value)
                }
            }

            override fun onCancelled(error: DatabaseError) {

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
//                Log.d(TAG, "onDataChange: ${orderList.size}")
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
//        Log.d(TAG, "caculate: ${orderList.size}")
        for (it in orderList){
            if (it.sum_type == "Qarz") {
                var a = it.summ.toString().replace("\\s".toRegex(), "")
                qarz += a.toDouble()
            } else {
                var a = it.summ.toString().replace("\\s".toRegex(), "")
                naqt += a.toDouble()
            }
//            Log.d(TAG, "caculate: ${it.summ}")
    }
//        Log.d(TAG, "caculate: qarz = $qarz")
//        Log.d(TAG, "caculate: naqt = $naqt")
        val decim = DecimalFormat("#,###")
        binding.name1.text = "${decim.format(naqt)} so'm naqt"
        binding.count1.text = "${decim.format(qarz)} so'm qarz"

//    binding.name1.text = "${naqt.toInt()} 000 so'm naqt"
//    binding.count1.text = "${qarz.toInt()} 000 so'm qarz"
}


}