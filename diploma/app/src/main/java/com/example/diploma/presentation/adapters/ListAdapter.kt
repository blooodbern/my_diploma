package com.example.diploma.presentation.adapters

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
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
import com.example.diploma.data.tables.TodayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.LocalDate
import java.time.chrono.ChronoLocalDateTime
import java.util.Locale


class ListAdapter(private val data: MutableList<ListItem>, private val context: Context) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var cnt = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        setTheme(1,holder)
        displayItems(holder, item)
        showLogTodayListAdapter(false)
        btnStopClicked(holder, position, item)
    }

    private fun setTheme(theme: Int, holder: ViewHolder){
        when(theme){
            1 -> {
                holder.butStop.setColorFilter(ContextCompat.getColor(context, R.color.custom21))
                holder.butPlayPause.setColorFilter(ContextCompat.getColor(context, R.color.custom21))
                holder.butDescr.setColorFilter(ContextCompat.getColor(context, R.color.custom21))
            }
        }
    }

    private fun displayItems(holder: ViewHolder, item: ListItem){
        if (item.isVisible && !item.isStop) {
            showFrame(holder)
        }
        if (!item.isVisible)
        {
            hideDescription(holder)
            hideFrame(holder)
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

    private fun showLogTodayListAdapter(show:Boolean){
        if(!show){
            cnt++
            if (cnt==STORAGE.LIST_LIMIT+1) cnt = 0
        }
        if (cnt==STORAGE.LIST_LIMIT || show){
            Log.d("dataAdapter", "showLogTodayListAdapter(): data.size = ${data.size}, " +
                    "cnt = ${cnt}")
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            for(i in 0 until data.size){
                Log.d("dataList", "dataList[${i}] id: ${data[i].id} " +
                        "text: ${data[i].text} description: ${data[i].description} " +
                        "date: ${dateFormat.format(data[i].date)} isVisible: ${data[i].isVisible} " +
                        "isPlaying: ${data[i].isPlaying} isStop: ${data[i].isStop} " +
                        "status: ${data[i].status} isVisible: ${data[i].isVisible} " +
                        "lastChanged: ${data[i].lastChanged}")
            }
        }
    }

    private fun btnStopClicked(holder: ViewHolder, position: Int, item: ListItem){
        holder.butStop.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
            if (fieldIsEmpty(holder)){
                hideDescription(holder)
                hideFrame(holder)
                withContext(Dispatchers.IO){
                    //item.isStop = true
                    //item.lastChanged = true
                    item.isVisible = false
                    if (getAmountOfVisibleItemsNext(position)!=0)reorderVisibleItems(position)
                }
                Log.d("btnStop", "btnStopClicked() called, amountOfVisibleItemsNext = ${getAmountOfVisibleItemsNext(position)}")
            }
                withContext(Dispatchers.IO){
                    saveDataInTableTodayList()
                }
                showLogTodayListAdapter(true)
            }
        }
    }

    private fun fieldIsEmpty(holder: ViewHolder):Boolean{
        val task = holder.etTask.text.toString()
        if(task=="") return true
        return false
    }

    private suspend fun saveDataInTableTodayList() {
        Log.d("todayList", "[Adapter] saveDataInTableTodayList() called, data.size = ${data.size}")
        withContext(Dispatchers.IO){
            val database = DatabaseMain.getDatabase(context)
            for (i in 0 until data.size) {
                val itemID = i+1
                database.getDaoTodayList().setItemName(data[i].text, itemID)
                database.getDaoTodayList().setItemDesc(data[i].description, itemID)
                database.getDaoTodayList().setItemTime(data[i].time, itemID)
                database.getDaoTodayList().setItemStatus(data[i].status, itemID)
                database.getDaoTodayList().setItemIsPlaying(data[i].isPlaying, itemID)
                database.getDaoTodayList().setItemIsStop(data[i].isStop, itemID)
                database.getDaoTodayList().setItemIsVisible(data[i].isVisible, itemID)
                database.getDaoTodayList().setItemLastChanged(data[i].lastChanged, itemID)
            }
        }
    }

    private fun reorderVisibleItems(position: Int){
        val tempItem = data[position].copy()
        for (i in position until STORAGE.LIST_LIMIT-1){
            data[i] = data[i+1]
            data[i].id = i+1
        }
        data[STORAGE.LIST_LIMIT-1] = tempItem
        data[STORAGE.LIST_LIMIT-1].id = STORAGE.LIST_LIMIT
    }

    private suspend fun getAmountOfVisibleItemsNext(id:Int):Int = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(context)
        return@withContext database.getDaoTodayList().amountOfVisibleItemsAfterItem(id+1)
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
/*
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_model, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        setupItem(holder, item)
        hideItems(holder)
        showItem(holder, item)
        stopChronometer(holder, position, item)
        setCursor(holder)
        editDescription(holder, item)
        timer(holder, position, item)
        saveUserTask(holder, position, item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun timer(holder: ViewHolder, position: Int, item: ListItem){
        onBtnPlayPressed(holder, position, item)
    }

    private fun onBtnPlayPressed(holder: ViewHolder, position: Int, item: ListItem){
        holder.butPlayPause.setOnClickListener {
            setItemLastChanged(position)
            chronometerPlay(holder, position, item)
        }
    }

    private fun chronometerPlay(holder: ViewHolder, position: Int, item: ListItem){
        CoroutineScope(Dispatchers.Main).launch {
            if (thereIsTask(holder)) {
                if (chronometerIsPlaying(position)) {
                    setItemPlaying(false, position)
                    pauseChronometer(holder, position, item)
                } else {
                    setItemPlaying(true, position)
                    startChronometer(holder, position, item)
                }
            }
        }
    }

    private fun startChronometer(holder: ViewHolder, position: Int, item: ListItem){
        holder.butPlayPause.setImageResource(R.drawable.ic_pause_36)
        CoroutineScope(Dispatchers.Main).launch{
            if (!item.timerStarted) {
                item.timerStarted = true
                holder.chronometer.base = SystemClock.elapsedRealtime()
            } else {
                holder.chronometer.base = SystemClock.elapsedRealtime() - getCurTime(position)
            }
            holder.chronometer.start()
            item.onPause = false
        }
    }

    private suspend fun getCurTime(position: Int):Long = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(context)
        return@withContext database.getDaoTodayList().getItemTime(position)
    }

    private fun pauseChronometer(holder: ViewHolder, position: Int,item: ListItem){
        holder.butPlayPause.setImageResource(R.drawable.ic_play_36)
        val currentTime = getCurItemTime(holder, item)
        saveCurTime(currentTime, position)
        holder.chronometer.stop()
        item.onPause = true
        item.currentTime = currentTime
    }

    private fun getCurItemTime(holder: ViewHolder, item: ListItem):Long{
        var diff = 0L
        if (item.timerStarted) {
            diff = SystemClock.elapsedRealtime() - holder.chronometer.base
        }
        //Log.d("Vojt", "get current = diff: ${diff/1000}s")
        return diff
    }

    private fun chronometerNotStarted(holder: ViewHolder, item: ListItem):Boolean{
        if (getCurItemTime(holder, item) == 0L) return true
        return false
    }

    private fun saveCurTime(time:Long, position: Int){
        //Log.d("TAG", "saveCurTime2() time = ${time/1000} s position: ${position}")
        var threadSaveTime = Thread{
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().setItemTime(time, position)
        }
        threadSaveTime.start()
        threadSaveTime.join()
    }

    private fun setItemPlaying(isPlaying:Boolean, position: Int){
        var threadSetPlayItem = Thread{
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().changeItemPlayingStatus(isPlaying, position)
        }
        threadSetPlayItem.start()
        threadSetPlayItem.join()
    }

    private suspend fun chronometerIsPlaying(position: Int):Boolean = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(context)
        return@withContext database.getDaoTodayList().checkItemIsPlaying(position)
    }



    private fun setCursor(holder: ViewHolder){
        holder.etTask.clearFocus()
        CoroutineScope(Dispatchers.Main).launch {
            if (isVisibleItem()) {
                holder.etTask.requestFocus()
            }
        }
    }

    private suspend fun isVisibleItem():Boolean = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(context)
        val visibleCount = database.getDaoTodayList().checkVisibleItems()
        return@withContext visibleCount > 0
    }

    private fun hideItems(holder: ViewHolder){
        hideDescription(holder)
        hideFrame(holder)
    }

    private fun showItem(holder: ViewHolder, item: ListItem){
        if (item.showItem && !item.isStop) {
            showFrame(holder)
            item.showItem = false
        }
    }

    private fun setupItem(holder: ViewHolder, item: ListItem){
        holder.etTask.setText(item.text)
        holder.etDescription.setText(item.description)
        holder.chronometer.base = SystemClock.elapsedRealtime() - item.currentTime
        setTheme(1, holder)
    }

    private fun showTodayListTable(){
        Log.d("showDatabaseToday", "showTodayListTable2() is called")
        var thread2 = Thread {
            val database = DatabaseMain.getDatabase(context)
            val items: List<TodayList> = database.getDaoTodayList().getAllTasks()
            for (item in items){
                Log.d("showDatabaseToday", "ID: ${item.id} TASK: ${item.task} " +
                        "DESC: ${item.description} STATUS: ${item.status} TIME: ${item.time/1000}s " +
                        "isPlaying: ${item.isPlaying} isStop: ${item.isStop} " +
                        "isVisible: ${item.isVisible} lastChanged: ${item.lastChanged}")
            }
        }
        thread2.start()
        thread2.join()
    }

    private fun stopChronometer(holder: ViewHolder, position: Int, item: ListItem) {
        holder.butStop.setOnClickListener {
            setItemIsStop(position)
            setItemLastChanged(position)
            CoroutineScope(Dispatchers.Main).launch {
                if (getStopItem(position) == 1) {
                    hideItems(holder)
                }
                if (thereIsTask(holder)) {
                    if (!item.onPause) pauseChronometer(holder, position, item)
                    item.isStop = true
                    writeTaskToDB(holder, position)
                    showTodayListTable()
                    callConfirmDialog(holder, position, item)
                } else {
                    deleteThisItem(position)
                    setItemNotVisible(position)
                    item.isVisible=false
                    //Log.d("TAG", "stopChronometer() set item[${position}] isVisible=false")
                }
            }
        }
    }

    private fun deleteThisItem(position: Int){
        var threadDeleteItem = Thread{
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().setItemName("", position)
            database.getDaoTodayList().setItemDesc("", position)
            database.getDaoTodayList().setItemTime(0L, position)
            database.getDaoTodayList().setItemStatus(0, position)
        }
        threadDeleteItem.start()
        threadDeleteItem.join()
    }

    private fun setItemNotVisible(position: Int){
        var threadSetInvisible = Thread{
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().setItemInvisible(position)
            database.getDaoTodayList().setItemNotStop(position)
        }
        threadSetInvisible.start()
        threadSetInvisible.join()
    }

    private fun callConfirmDialog(holder: ViewHolder, position: Int, item: ListItem){
        CoroutineScope(Dispatchers.Main).launch{
            if(chronometerNotStarted(holder, item)) updateTaskStatus(0, position)
            else STORAGE.IsPressed = true
        }
    }

    private fun writeTaskToDB(holder: ViewHolder, position: Int){
        val userTask = holder.etTask.text.toString()
        val userDescription = holder.etDescription.text.toString()
        val threadInsertTodayList = Thread{
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().setItemName(userTask, position)
            database.getDaoTodayList().setItemDesc(userDescription, position)
        }
        threadInsertTodayList.start()
        threadInsertTodayList.join()
    }

    private fun updateTaskStatus(status:Int, position: Int){
        var threadUpdateStatus = Thread{
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().setItemStatus(status, position)
        }
        threadUpdateStatus.start()
        threadUpdateStatus.join()
    }

    private fun thereIsTask(holder: ViewHolder):Boolean{
        val task:String = holder.etTask.text.toString()
        if (task=="") return false
        return true
    }

    private fun setItemIsStop(position: Int){
        var threadStopItem = Thread {
            val database = DatabaseMain.getDatabase(context)
            database.getDaoTodayList().setItemStop(position)
            database.getDaoTodayList().setItemNotPlaying(position)
        }
        threadStopItem.start()
        threadStopItem.join()
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

    private suspend fun getStopItem(position: Int):Int = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(context)
        return@withContext database.getDaoTodayList().checkItemStop(position)
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


    private fun saveUserTask(holder: ViewHolder, position: Int, item: ListItem){
        holder.etTask.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item.id = position
                item.text = holder.etTask.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


    }


    //******************
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurDate(): Long{
        return LocalDate.now().toEpochDay()
    }
    //******************


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

    */