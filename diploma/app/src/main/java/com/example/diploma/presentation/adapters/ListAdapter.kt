package com.example.diploma.presentation.adapters

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.R
import com.example.diploma.domain.ListItem
import com.example.diploma.data.STORAGE
import com.example.diploma.data.database.DatabaseMain
import com.example.diploma.data.tables.AllTasksByAllTime
import java.time.LocalDate


class ListAdapter(private val data: MutableList<ListItem>, private val context: Context) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    // *******************
    private val isButtonPressedList = MutableList(100) { false }
    private var INDEX = 0
    private var choronoTime:Long = 0L
    // *******************

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_model, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        setupItem2(holder, item)
        hideItems2(holder)
        showItem2(holder, item)
        stopChronometer2(holder, position)

        /*
        setupItem(holder, item, position)
        setCursor(holder, position)
        editDescription(holder, item)
        onBtnPlayPressed(holder, item, position)
        stopChronometer(holder, item, position)
         */
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun hideItems2(holder: ViewHolder){
        hideFrame(holder)
        hideDescription(holder)
    }

    private fun showItem2(holder: ViewHolder, item: ListItem){
        if (item.showItem) showFrame(holder)
    }
    private fun setupItem2(holder: ViewHolder, item: ListItem){
        holder.etTask.setText(item.text)
        setTheme(1, holder)
    }

    private fun stopChronometer2(holder: ViewHolder, position: Int) {
        holder.butStop.setOnClickListener {
            Log.d("TAG", "stopChronometer2() position = ${position}")
        }
    }

    private fun hideAllCreatedItem2(holder: ViewHolder){
        hideFrame(holder)
    }

    private fun setupItem(holder: ViewHolder, item: ListItem, position: Int) {
        if(item.returnItem){
            var thread4 = Thread {
                val database = DatabaseMain.getDatabase(context)
                val tasks: List<AllTasksByAllTime> = database.getDaoMain().getItem(STORAGE.curTask, STORAGE.curDate)
                for (task in tasks){
                    holder.etTask.setText(task.task)
                    choronoTime = task.time
                    holder.chronometer.base = SystemClock.elapsedRealtime() - choronoTime
                }
            }
            thread4.start()
            thread4.join()
        }
        else holder.etTask.setText(item.text)

        if (STORAGE.maxPosition<0) {
            hideDescription(holder)
            hideFrame(holder)
        }
        else {
            if(position == STORAGE.maxPosition) showFrame(holder)
            else hideFrame(holder)
        }

        Log.d("TAG", "setupItem() position = ${position}")
        Log.d("TAG", "setupItem() maxPosition = ${STORAGE.maxPosition}")

        setTheme(1, holder)
    }

    private fun setTheme(theme: Int, holder: ViewHolder){
        when(theme){
            1 -> {
                holder.butStop.setColorFilter(ContextCompat.getColor(context, R.color.custom2))
                holder.butPlayPause.setColorFilter(ContextCompat.getColor(context, R.color.custom2))
                holder.butDescr.setColorFilter(ContextCompat.getColor(context, R.color.custom2))
            }
        }
    }

    private fun setCursor(holder: ViewHolder, position: Int){
        if (position == data.size - 1) {
            holder.etTask.requestFocus()
        } else {
            holder.etTask.clearFocus()
        }
    }

    private fun editDescription(holder: ViewHolder, item: ListItem){
        holder.butDescr.setOnClickListener {
            item.isDescription = !item.isDescription
            changeDescButt(holder, item)
        }
    }

    private fun changeDescButt(holder: ViewHolder, item: ListItem){
        if (item.isDescription) {
            holder.butDescr.setImageResource(R.drawable.ic_show_less_36)
            showDescription(holder)
        } else {
            holder.butDescr.setImageResource(R.drawable.ic_show_more_36)
            hideDescription(holder)
        }
    }

    private fun showDescription(holder: ViewHolder){
        holder.descriptionForm.visibility = VISIBLE
        holder.descriptionForm.requestFocus()
    }

    private fun hideDescription(holder: ViewHolder){
        holder.descriptionForm.visibility = GONE
    }

    private fun showFrame(holder: ViewHolder){
        holder.itemFrame.visibility = VISIBLE
    }

    private fun hideFrame(holder: ViewHolder){
        holder.itemFrame.visibility = GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun play(holder: ViewHolder, item: ListItem, position: Int){
            val userTask = holder.etTask.text.toString()
            if(userTask!=""){
                item.isRunning = !item.isRunning
                changePlayButt(holder, item, position)
                Log.d("TAG", "isRunning = ${item.isRunning}")
                //***********
                updateDatabase(holder, item, item.isRunning, item.isStop)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onBtnPlayPressed(holder: ViewHolder, item: ListItem, position: Int){
        holder.butPlayPause.setOnClickListener {play(holder, item, position)}
    }

    //******************
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDatabase(holder: ViewHolder, item: ListItem, isRunning: Boolean, isStop: Boolean){
        val userTask = holder.etTask.text.toString()
        if(userTask!=""){
            val userDescription = holder.etDescription.text.toString()
            val time = item.currentTime
            val curDate = getCurDate()
            var btnStatus = STORAGE.IS_RUNNING
            var taskStatus:String = ""
            STORAGE.curDate = curDate
            if (item.isStop){
                btnStatus = STORAGE.STOP
                STORAGE.curTask = userTask
                taskStatus = context.getString(R.string.task_success)
                if(time == 0L) taskStatus = context.getString(R.string.task_notStarted)
            }
            else if (item.isRunning){
                btnStatus = STORAGE.IS_RUNNING
                taskStatus = context.getString(R.string.task_inProcess)
            }
            else{
                btnStatus = STORAGE.ON_PAUSE
                taskStatus = context.getString(R.string.task_partly)
            }
            val task = AllTasksByAllTime(
                null,
                curDate,
                userTask,
                userDescription,
                btnStatus,
                time,
                taskStatus
            )
            var thread1 = Thread {
                val database = DatabaseMain.getDatabase(context)
                if(database.getDaoMain().checkIfTaskExists(userTask, curDate) > 0) {
                    database.getDaoMain().changeBtnStatus(userTask, curDate, btnStatus)
                    database.getDaoMain().changeTaskStatus(userTask, curDate, taskStatus)
                    database.getDaoMain().changeTaskTime(userTask, curDate, time)
                } else database.getDaoMain().insertItem(task)
            }
            thread1.start()
            thread1.join()
        }
        showAllTasksTable()
    }

    private fun showAllTasksTable(){
        Log.d("TAG", "showAllTasksTable() is called")
        var thread2 = Thread {
            val database = DatabaseMain.getDatabase(context)
            val items: List<AllTasksByAllTime> = database.getDaoMain().getAllTasks()
            for (item in items){
                Log.d("TAG", "ID: ${item.id} DATA: ${item.date} TASK: ${item.task} STATUS: ${item.btn_status} TIME: ${item.time} STATUS: ${item.task_status}")
            }
        }
        thread2.start()
        thread2.join()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurDate(): Long{
        return LocalDate.now().toEpochDay()
    }
    //******************

    private fun changePlayButt(holder: ViewHolder, item: ListItem, position: Int){
        if (item.isRunning) {
            holder.butPlayPause.setImageResource(R.drawable.ic_pause_36)
            if(item.returnItem){continueChronometer(holder)}
            else startChronometer(holder, position)
        } else {
            holder.butPlayPause.setImageResource(R.drawable.ic_play_36)
            pauseChronometer(holder, position)
        }
    }

    private fun continueChronometer(holder: ViewHolder){
        holder.chronometer.base = SystemClock.elapsedRealtime() - choronoTime
        holder.chronometer.start()
    }

    private fun startChronometer(holder: ViewHolder, position: Int) {
        holder.chronometer.base = SystemClock.elapsedRealtime() - data[position].currentTime
        holder.chronometer.start()
    }

    private fun pauseChronometer(holder: ViewHolder, position: Int) {
        holder.chronometer.stop()
        data[position].currentTime = SystemClock.elapsedRealtime() - holder.chronometer.base
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopChronometer(holder: ViewHolder, item: ListItem, position: Int) {
        holder.butStop.setOnClickListener {
            var time = item.currentTime
           // Log.d("TAG", "IsPressed up = ${STORAGE.IsPressed}")
          //  Log.d("TAG", "isRunning in  stopChronometer() up = ${item.isRunning}")
           // Log.d("TAG", "currentTime up = ${time}")
            if(item.isRunning) {
                play(holder, item, position)
            }
            time = item.currentTime
           // Log.d("TAG", "currentTime above = ${time}")
            if(time != 0L){
                item.isStop = !item.isStop
                Log.d("TAG", "i'm in 'else'")
                STORAGE.IsPressed = true
                STORAGE.curTask = item.text
                updateDatabase(holder, item, item.isRunning, item.isStop)
            }
            removeItem(holder, item, position)
            //Log.d("TAG", "IsPressed above = ${STORAGE.IsPressed}")
           // Log.d("TAG", "isRunning in stopChronometer() above = ${item.isRunning}")
        }
    }

    private fun removeItem(holder: ViewHolder, item: ListItem, position: Int){

        hideDescription(holder)
        hideFrame(holder)
        if (STORAGE.maxPosition==STORAGE.LIST_LIMIT)STORAGE.maxPosition--
        if (STORAGE.maxPosition>0) STORAGE.maxPosition--

        Log.d("TAG", "removeItem(): position = ${position}")
        Log.d("TAG", "removeItem() maxPosition = ${STORAGE.maxPosition}")


        /*
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()

         */
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val etTask: EditText = view.findViewById(R.id.et_task)
        val butDescr: ImageButton = view.findViewById(R.id.ib_descr)
        val etDescription: EditText = view.findViewById(R.id.et_description)
        val descriptionForm: CardView = view.findViewById(R.id.cv_description)
        val chronometer: Chronometer = view.findViewById(R.id.chronometer)
        val butStop: ImageButton = view.findViewById(R.id.ib_stop)
        val butPlayPause: ImageButton = view.findViewById(R.id.ib_play_pause)
        val itemFrame: CardView = view.findViewById(R.id.finished_task)
    }
}
