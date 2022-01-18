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
import com.example.orderrawmaterials.utils.FirebaseService
import com.example.orderrawmaterials.utils.Statusbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdminEditPAnel : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var orders_reference: DatabaseReference
    private lateinit var rector_reference: DatabaseReference
    private lateinit var rectorsList: ArrayList<User>
    private lateinit var binding: ActivityAdminEditPanelBinding
    private lateinit var messageUid: ArrayList<String>
    private lateinit var apiService: APIService
    private lateinit var data: FirebaseService
    private val TAG = "AdminEditPAnel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Statusbar.startStatus(this)

        rectorsList = ArrayList()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        orders_reference = firebaseDatabase.getReference("Orders")
        rector_reference = firebaseDatabase.getReference("Director")

        apiService =
            Client.getRetrofit("https://fcm.googleapis.com/").create(APIService::class.java)

        getValue()
    }


    private fun getValue() {
        val uid = intent.getStringExtra("s")
        FirebaseService.getData("Orders").observe(this, {
            var m1 = arrayListOf<Data>()
            messageUid = ArrayList()
            messageUid.clear()
            for (i in it) {
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
        FirebaseService.getData("Director").observe(this, {
            for (i in it) {
                var user = i.getValue(User::class.java)
                if (user != null) {
                    rectorsList.add(user)
                }
            }
        })
        for (i in rectorsList) {
            apiService.sendNotification(
                Sender(
                    NotifyData(
                        firebaseAuth.currentUser!!.uid,
                        R.drawable.logo,
                        name,
                        "New Message",
                        firebaseAuth.currentUser!!.uid
                    ), i.token.toString()
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