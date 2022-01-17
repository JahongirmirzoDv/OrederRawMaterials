package com.example.orderrawmaterials.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orderrawmaterials.R
import com.example.orderrawmaterials.databinding.UserItemBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User

class UsersRvAdapter(var list: ArrayList<Data>, var context: Context, var onpress: onPress) :
    RecyclerView.Adapter<UsersRvAdapter.Vh>() {
    inner class Vh(var itemview: UserItemBinding) : RecyclerView.ViewHolder(itemview.root) {
        @SuppressLint("SetTextI18n")
        fun Bind(data: Data, position: Int) {
            itemview.text1.text = data.name
            itemview.text2.text = "${data.count} ${data.type}"
            itemview.container.setOnClickListener {
                onpress.onClick(data, position)
            }
            if (data.isCompleted) {
                itemview.completedImg.setImageResource(R.drawable.check)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.Bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    interface onPress {
        fun onClick(data: Data, position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}