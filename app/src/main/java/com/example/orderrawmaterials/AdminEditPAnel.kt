package com.example.orderrawmaterials

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.orderrawmaterials.adapters.UsersRvAdapter
import com.example.orderrawmaterials.databinding.ActivityAdminEditPanelBinding
import com.example.orderrawmaterials.databinding.CustomDialogBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User
import com.example.orderrawmaterials.notification.APIService
import com.example.orderrawmaterials.notification.Client
import com.example.orderrawmaterials.notification.models.NotifyData
import com.example.orderrawmaterials.notification.models.Responce
import com.example.orderrawmaterials.notification.models.Sender
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdminEditPAnel : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var orders_reference: DatabaseReference
    lateinit var rector_reference: DatabaseReference
    lateinit var rectorsList: ArrayList<User>
    lateinit var binding: ActivityAdminEditPanelBinding
    lateinit var messageUid: ArrayList<String>
    lateinit var apiService: APIService
    private val TAG = "AdminEditPAnel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        rector_reference = firebaseDatabase.getReference("Director")

        apiService =
            Client.getRetrofit("https://fcm.googleapis.com/").create(APIService::class.java)

        rector_reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                rectorsList.clear()
                for (i in children) {
                    val value = i.getValue(User::class.java)!!
                    rectorsList.add(value)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        getValue()
    }

    private fun getValue() {
        val uid = intent.getStringExtra("s")
        orders_reference.addValueEventListener(object : ValueEventListener {
            var m1 = arrayListOf<Data>()
            override fun onDataChange(snapshot: DataSnapshot) {
                messageUid = ArrayList()
                messageUid.clear()
                m1.clear()
                val children = snapshot.children
                for (i in children) {
                    val value = i.children
                    if (i.key == uid) {
                        value.forEach {
                            messageUid.add(it.key.toString())
                            val value1 = it.getValue(Data::class.java)
                            m1.add(value1!!)
                        }
                    }
                }
                val usersRvAdapter =
                    UsersRvAdapter(m1, this@AdminEditPAnel, object : UsersRvAdapter.onPress {
                        override fun onClick(data: Data, position: Int) {
                            if (!data.isCompleted) {
                                fragmentDialog(data, position, uid)
                            } else {
                                MaterialAlertDialogBuilder(this@AdminEditPAnel)
                                    .setMessage("${data.name} ${data.count} ${data.type} ${data.summ} so'm ${data.sum_type}")
                                    .setPositiveButton(
                                        "ok"
                                    )
                                    { dialogInterface, i ->
                                        dialogInterface.cancel()
                                    }
                                    .setNegativeButton(
                                        "edit"
                                    ) { dialogInterface, i ->
                                        fragmentDialog(data, position, uid)
                                    }
                                    .show()
                            }
                        }
                    })
                binding.rv.adapter = usersRvAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun fragmentDialog(data: Data, position: Int, uid: String?) {
        var binding = CustomDialogBinding.inflate(
            LayoutInflater.from(this@AdminEditPAnel), null, false
        )
        var dialog = Dialog(this@AdminEditPAnel, R.style.Theme_AppCompat_Dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(binding.root)

        val arrayAdapter = ArrayAdapter(
            this@AdminEditPAnel, R.layout.support_simple_spinner_dropdown_item,
            listOf("Naqt", "Qarz", "Per")
        )
        binding.spinner.adapter = arrayAdapter
        binding.lastName.hint = data.summ
        binding.cancel.setOnClickListener {
            dialog.cancel()
        }
        binding.save.setOnClickListener {
            val name = binding.lastName.text.toString()
            val type = binding.spinner.selectedItem
            if (name.isNotEmpty()) {
                val data1 = Data(
                    data.name,
                    data.type,
                    data.count,
                    true,
                    name,
                    type.toString()
                )
                orders_reference.child(uid.toString())
                    .child(messageUid[position])
                    .setValue(data1)
                sendNotify(data.name.toString())
                Toast.makeText(
                    this@AdminEditPAnel,
                    "Saqlandi",

                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            dialog.cancel()
        }
        dialog.show()
    }

    private fun sendNotify(name: String) {
        for (i in rectorsList) {
            apiService.sendNotification(
                Sender(
                    NotifyData(
                        firebaseAuth.currentUser!!.uid, R.drawable.logo,
                        name, "New Message", firebaseAuth.currentUser!!.uid
                    ),
                    i.token.toString()
                )
            )
                .enqueue(object : Callback<Responce> {
                    override fun onResponse(
                        call: Call<Responce>,
                        response: Response<Responce>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminEditPAnel, "Success", Toast.LENGTH_SHORT)
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
}