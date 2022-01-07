package com.example.orderrawmaterials.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orderrawmaterials.databinding.TableRvItemBinding
import com.example.orderrawmaterials.models.Data

class TableRv(private var list: ArrayList<Data>) : RecyclerView.Adapter<TableRv.Vh>() {
    inner class Vh(private var itemview: TableRvItemBinding) :
        RecyclerView.ViewHolder(itemview.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Data) {
            itemview.name.text = data.name
            itemview.count.text = "${data.count} ${data.type}"
            itemview.sum.text = "${data.summ} so'm"
            itemview.type.text = data.sum_type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(TableRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}