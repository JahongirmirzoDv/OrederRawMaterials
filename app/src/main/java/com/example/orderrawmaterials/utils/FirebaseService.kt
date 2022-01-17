package com.example.orderrawmaterials.utils

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.firebase.database.GenericTypeIndicator
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass


object FirebaseService : ViewModel() {
    lateinit var reference: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase
    var liveData = MediatorLiveData<MutableIterable<DataSnapshot>>()
    private val TAG = "FirebaseService"

    fun getData(path: String): MediatorLiveData<MutableIterable<DataSnapshot>> {
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference(path)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                liveData.value = children
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
        return liveData
    }
}