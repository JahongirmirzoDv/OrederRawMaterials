package com.example.orderrawmaterials

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.orderrawmaterials.databinding.ActivityAddBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User
import com.example.orderrawmaterials.notification.APIService
import com.example.orderrawmaterials.notification.Client
import com.example.orderrawmaterials.notification.models.NotifyData
import com.example.orderrawmaterials.notification.models.Responce
import com.example.orderrawmaterials.notification.models.Sender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Add : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var orders_reference: DatabaseReference
    lateinit var admins_reference: DatabaseReference
    lateinit var apiService: APIService
    private val TAG = "Add"
    var m1 = arrayListOf("Metr", "Kilometr", "Dona", "Ta")
    lateinit var m2: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
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
        admins_reference = firebaseDatabase.getReference("Admins")
        m2 = ArrayList()
        apiService =
            Client.getRetrofit("https://fcm.googleapis.com/").create(APIService::class.java)

        admins_reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                m2.clear()
                for (i in children) {
                    val value = i.getValue(User::class.java)!!
                    m2.add(value)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, m1)
        binding.spinner.adapter = arrayAdapter

        binding.add.setOnClickListener {
            if (m2.isNotEmpty()) {
                val c = Calendar.getInstance().time
                val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                val formattedDate: String = df.format(c)
                Log.d(TAG, "onCreate: ${m2[0].name}")
                val name = binding.name.text.toString()
                val lastName = binding.number.text.toString()
                val type = binding.spinner.selectedItem.toString()
                var data = Data(name, type, lastName,formattedDate, false)
                val key = orders_reference.push().key
                orders_reference.child(firebaseAuth.currentUser!!.uid).child(key.toString())
                    .setValue(data)
                finish()
                sendNotify(name)
            }else{
                Toast.makeText(this, "Admin yo'q", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotify(name: String) {
        apiService.sendNotification(
            Sender(
                NotifyData(
                    firebaseAuth.currentUser!!.uid, R.drawable.ic_launcher_foreground,
                    name, "New Message", firebaseAuth.currentUser!!.uid
                ),
                m2[0].token.toString()
            )
        )
            .enqueue(object : Callback<Responce> {
                override fun onResponse(
                    call: Call<Responce>,
                    response: Response<Responce>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@Add, "Success", Toast.LENGTH_SHORT)
                            .show()
                        Log.d(TAG, "Success:")
                    }
                }

                override fun onFailure(call: Call<Responce>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "onFailure: ${t.message}")
                }

            })
    }
}