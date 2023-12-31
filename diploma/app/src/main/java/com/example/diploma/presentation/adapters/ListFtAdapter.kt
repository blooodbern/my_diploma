package com.example.diploma.presentation.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.R
import com.example.diploma.data.STORAGE
import com.example.diploma.data.database.DatabaseMain
import com.example.diploma.domain.ListItem
import java.time.LocalDate

class ListFtAdapter (private val data: List<ListItem>, private val context: Context):
    RecyclerView.Adapter<ListFtAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFtAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ListFtAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val butRenew: ImageButton = view.findViewById(R.id.ib_renew)
        val ftName: TextView = view.findViewById(R.id.tv_finished_task)
        val ftStatus: TextView = view.findViewById(R.id.status_ft)
        val ftFrame: CardView = view.findViewById(R.id.finished_task)
    }
}
        /*

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.homeft_list_model, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        setupItem(holder, item)
        displayingItems(holder,item)
        btn_renew(holder, position, item)
    }

    override fun getItemCount(): Int = data.size

    private fun setupItem(holder: ViewHolder, item: ListItem){
        holder.ftName.text = item.text
        holder.ftStatus.text = getStatus(item.status)
    }

    private fun displayingItems(holder: ViewHolder,item: ListItem){
        if (STORAGE.ftListVisible) showItems(holder,item)
        else hideItems(holder)
    }


    private fun hideItems(holder: ViewHolder){
        holder.ftFrame.visibility = GONE
    }

    private fun showItems(holder: ViewHolder, item: ListItem){
        if(item.text!="" && item.isStop) holder.ftFrame.visibility = VISIBLE
        else hideItems(holder)
    }

    private fun btn_renew(holder: ViewHolder, position: Int, item: ListItem){
        holder.butRenew.setOnClickListener {
            setItemLastChanged(position)
            clearItem(holder, item)
            STORAGE.returnPressed = true
        }
    }

    private fun clearItem(holder: ViewHolder, item: ListItem){
        item.text = ""
        item.status = 3
        holder.ftName.text = item.text
        holder.ftStatus.text = getStatus(item.status)
        holder.ftFrame.visibility = GONE
    }

    private fun getStatus(statusId:Int):String{
        when(statusId){
            1 -> return context.getString(R.string.task_unsatis)
            2 -> return context.getString(R.string.task_partly)
            3 -> return context.getString(R.string.task_success)
            4 -> return context.getString(R.string.task_inProcess)
        }
        return context.getString(R.string.task_notStarted)
    }

    private fun setItemLastChanged(position: Int){
        var threadItemLastChanged = Thread {
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().unsetItemLastChanged()
            database.getDaoTodayList().setItemLastChanged(position)
        }
        threadItemLastChanged.start()
        threadItemLastChanged.join()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurDate(): Long{
        return LocalDate.now().toEpochDay()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val butRenew: ImageButton = view.findViewById(R.id.ib_renew)
        val ftName: TextView = view.findViewById(R.id.tv_finished_task)
        val ftStatus: TextView = view.findViewById(R.id.status_ft)
        val ftFrame: CardView = view.findViewById(R.id.finished_task)
    }
*/