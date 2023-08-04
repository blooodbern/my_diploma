package com.example.diploma.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.R
import com.example.diploma.domain.ListItem

class ListFtAdapter (private val data: List<ListItem>, private val context: Context):
    RecyclerView.Adapter<ListFtAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.homeft_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        setupItem(holder, item)
    }

    override fun getItemCount(): Int = data.size

    private fun setupItem(holder: ViewHolder, item: ListItem){
        holder.ftName.setText(item.text)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val butRenew: ImageButton = view.findViewById(R.id.ib_renew)
        val ftName: TextView = view.findViewById(R.id.tv_finished_task)
        val ftStatus: TextView = view.findViewById(R.id.status_ft)
    }
}