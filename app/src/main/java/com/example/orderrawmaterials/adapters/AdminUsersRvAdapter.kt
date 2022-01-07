package com.example.orderrawmaterials.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orderrawmaterials.databinding.AdminItemBinding
import com.example.orderrawmaterials.databinding.UserItemBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User

class AdminUsersRvAdapter(var list: ArrayList<User>, var onpress: onPress) :
    RecyclerView.Adapter<AdminUsersRvAdapter.Vh>() {
    inner class Vh(var itemview: AdminItemBinding) : RecyclerView.ViewHolder(itemview.root) {
        fun Bind(user: User,position: Int) {
            itemview.userName.text = user.name
            itemview.userLastName.text = user.last_name
            itemview.container.setOnClickListener {
                onpress.onClick(user, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(AdminItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.Bind(list[position],position)
    }

    override fun getItemCount(): Int = list.size

    interface onPress {
        fun onClick(user: User, position: Int)
    }
}