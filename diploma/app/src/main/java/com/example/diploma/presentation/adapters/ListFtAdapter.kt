package com.example.diploma.presentation.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.R
import com.example.diploma.data.database.DatabaseMain
import com.example.diploma.domain.ListItem
import java.time.LocalDate

class ListFtAdapter (private val data: List<ListItem>, private val context: Context):
    RecyclerView.Adapter<ListFtAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.homeft_list_model, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        setupItem(holder, item)
        updateDatabase(holder, item)
    }

    override fun getItemCount(): Int = data.size

    private fun setupItem(holder: ViewHolder, item: ListItem){
        holder.ftName.setText(item.text)
        //holder.ftStatus.setText(item.status)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDatabase(holder: ViewHolder, item: ListItem){
        val userTask = holder.ftName.text.toString()
        val curDate = getCurDate()
       //val taskStatus = item.status
        var thread3 = Thread {
            val database = DatabaseMain.getDatabase(context)
            if(database.getDaoMain().checkIfTaskExists(userTask, curDate) > 0) {
                //database.getDaoMain().changeTaskStatus(userTask, curDate, taskStatus)
            }
        }
        thread3.start()
        thread3.join()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurDate(): Long{
        return LocalDate.now().toEpochDay()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val butRenew: ImageButton = view.findViewById(R.id.ib_renew)
        val ftName: TextView = view.findViewById(R.id.tv_finished_task)
        val ftStatus: TextView = view.findViewById(R.id.status_ft)
    }
}