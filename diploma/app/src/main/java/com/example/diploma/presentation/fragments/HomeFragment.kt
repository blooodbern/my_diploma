package com.example.diploma.presentation.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.voice.VoiceInteractionSession.VisibleActivityCallback
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diploma.R
import com.example.diploma.data.SETTINGS
import com.example.diploma.databinding.ListHomeBinding
import com.example.diploma.presentation.adapters.ListAdapter
import com.example.diploma.presentation.adapters.ListFtAdapter
import com.example.diploma.domain.ListItem
import com.example.diploma.data.STORAGE
import com.example.diploma.data.database.DatabaseMain
import com.example.diploma.data.tables.AllTasksByAllTime
import com.example.diploma.data.tables.SetupData
import com.example.diploma.data.tables.TodayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.reflect.KProperty

class HomeFragment : Fragment() {

    private lateinit var adapter: ListAdapter
    private lateinit var adapterFT: ListFtAdapter
    private val dataList = mutableListOf<ListItem>()
    private val dataListFT = mutableListOf<ListItem>()

    private var _binding: ListHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListHomeBinding.inflate(inflater, container, false)

        setCurDate()
        initAdapters()
        CoroutineScope(Dispatchers.IO).launch{setupDefaultSettings()}

        return binding.root
    }

    private fun setCurDate(){
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        var formattedDate = dateFormat.format(currentDate)
        binding.curDate.text = formattedDate
    }

    private fun initAdapters(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ListAdapter(dataList, requireContext())
        binding.recyclerView.adapter = adapter

        binding.recyclerViewFT.layoutManager = LinearLayoutManager(requireContext())
        adapterFT = ListFtAdapter(dataListFT, requireContext())
        binding.recyclerViewFT.adapter = adapterFT
    }

    private suspend fun setupDefaultSettings(){
        val settingsAmount = SETTINGS().settingsList.size
        if (rowsInSetupTable()<settingsAmount){
            withContext(Dispatchers.IO){
                val database = DatabaseMain.getDatabase(requireContext())
                for (i in 0 until settingsAmount){
                    val setting = SetupData(
                        null,
                        SETTINGS().settingsList[0],
                        0
                    )
                    database.getDaoSetupData().insertSettings(setting)
                }
            }
        }
    }

    private suspend fun rowsInSetupTable():Int = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoSetupData().checkIfSettingsTableEmpty()
    }


}
    /*

    private lateinit var adapter: ListAdapter
    private lateinit var adapterFT: ListFtAdapter
    private val dataList = mutableListOf<ListItem>()
    private val dataListFT = mutableListOf<ListItem>()

    private val handler = Handler(Looper.getMainLooper())
    private val pollingIntervalMillis: Long = 500

    private lateinit var formattedDate: String


    private var _binding: ListHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        fillTodayDataBase()
        setCurDate()
        createList()
        addBtnClicked()

        showVisibleItems()

        createFTList()
        fillFTList()
        btn_showFTlist()


        return root
    }

    override fun onResume() {
        super.onResume()
        startPolling()
        showVisibleItems()
        fillFTList()
    }


    private fun init(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ListAdapter(dataList, requireContext())
        binding.recyclerView.adapter = adapter

        binding.recyclerViewFT.layoutManager = LinearLayoutManager(requireContext())
        adapterFT = ListFtAdapter(dataListFT, requireContext())
        binding.recyclerViewFT.adapter = adapterFT
    }

    private fun setCurDate(){
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        formattedDate = dateFormat.format(currentDate)
        binding.curDate.text = formattedDate

    }

    private fun saveItems(){
        CoroutineScope(Dispatchers.Main).launch{
            val items = dataList
            val database = DatabaseMain.getDatabase(requireContext())
            for (item in items){
                database.getDaoTodayList().setItemName(item.text,item.id)
                database.getDaoTodayList().setItemDesc(item.description,item.id)
                database.getDaoTodayList().setItemTime(item.currentTime,item.id)
                database.getDaoTodayList().setItemStatus(item.status,item.id)
                database.getDaoTodayList().updateItemStopStatus(item.isStop,item.id)
                // database.getDaoTodayList().updateItemVisibleStatus(item.isVisible,item.id)
            }
        }
        showDataList()
        showTodayListTable()

        /*
        var threadSaveItems = Thread{
            val items = dataList
            val database = DatabaseMain.getDatabase(requireContext())
            for (item in items){
                database.getDaoTodayList().setItemName(item.text,item.id)
                database.getDaoTodayList().setItemDesc(item.description,item.id)
                database.getDaoTodayList().setItemTime(item.currentTime,item.id)
                database.getDaoTodayList().setItemStatus(item.status,item.id)
                database.getDaoTodayList().updateItemStopStatus(item.isStop,item.id)
               // database.getDaoTodayList().updateItemVisibleStatus(item.isVisible,item.id)
            }
        }
        threadSaveItems.start()
        threadSaveItems.join()
         */

    }

    private suspend fun getMaxID():Int = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        //Log.d("TAG", "idMax = ${idMax}")
        return@withContext database.getDaoTodayList().getMaxID()
    }

    private suspend fun getMinInvisID():Int = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        //Log.d("TAG", "minId = ${minId}")
        return@withContext database.getDaoTodayList().getMinInvisibleID()
    }

    private fun showDataList(){
        for(i in 0 until dataList.size){
            Log.d("TAG", "dataList[${i}] text: ${dataList[i].text} isVisible: ${dataList[i].isVisible}")
        }
    }

    private fun itemIsVisible(itemId: Int){
        dataList[itemId].isVisible = true
       // Log.d("TAG", "--- dataList[${itemId}] text: ${dataList[itemId].text} isVisible: ${dataList[itemId].isVisible}")
        showDataList()
        CoroutineScope(Dispatchers.Main).launch{
            val database = DatabaseMain.getDatabase(requireContext())
            database.getDaoTodayList().setItemVisible(itemId)
        }

        /*
        var thread4 = Thread{
            val database = DatabaseMain.getDatabase(requireContext())
            database.getDaoTodayList().setItemVisible(itemId)
        }
        thread4.start()
        thread4.join()

         */
    }

    private suspend fun checkLimit():Int =  withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
       // Log.d("TAG", "visibleItemsAmount = ${invisibleItemsAmount}")
        return@withContext database.getDaoTodayList().checkInvisibleItems()
    }

    private fun fillTodayDataBase(){
        CoroutineScope(Dispatchers.IO).launch{
            if (!STORAGE.DatabaseTodayListAlreadyCreated && getMaxID() != STORAGE.LIST_LIMIT-1){
                for (i in 0 until STORAGE.LIST_LIMIT){
                    val task = TodayList(
                        i,
                        "",
                        "",
                        0L,
                        0,
                        false,
                        false,
                        false,
                        false
                    )
                    CoroutineScope(Dispatchers.Main).launch{
                        val database = DatabaseMain.getDatabase(requireContext())
                        database.getDaoTodayList().insertItem(task)
                    }

                    /*
                    var thread1 = Thread{
                        val database = DatabaseMain.getDatabase(requireContext())
                        database.getDaoTodayList().insertItem(task)
                    }
                    thread1.start()
                    thread1.join()

                     */
                }
                STORAGE.DatabaseTodayListAlreadyCreated = true
            }
        }
        showTodayListTable()
    }

    private fun showTodayListTable(){
        Log.d("showDatabaseToday", "showTodayListTable() is called, DatabaseTodayListAlreadyCreated = ${STORAGE.DatabaseTodayListAlreadyCreated}")
        CoroutineScope(Dispatchers.IO).launch{
            val database = DatabaseMain.getDatabase(requireContext())
            val items: List<TodayList> = database.getDaoTodayList().getAllTasks()
            withContext(Dispatchers.Main){
                for (item in items){
                    Log.d("showDatabaseToday", "ID: ${item.id} TASK: ${item.task} " +
                            "DESC: ${item.description} STATUS: ${item.status} TIME: ${item.time/1000}s " +
                            "isPlaying: ${item.isPlaying} isStop: ${item.isStop} " +
                            "isVisible: ${item.isVisible} lastChanged: ${item.lastChanged}")
                }
            }
        }

/*
        Log.d("showDatabaseToday", "showTodayListTable() is called, DatabaseTodayListAlreadyCreated = ${STORAGE.DatabaseTodayListAlreadyCreated}")
        var thread2 = Thread {
            val database = DatabaseMain.getDatabase(requireContext())
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

 */
    }

    private fun addBtnClicked(){
        binding.btnAddItem.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
                if(checkLimit()>0) showNewItem()
                else Toast.makeText(requireContext(), "limit exceeded", Toast.LENGTH_SHORT).show()
            }
           // Log.d("TAG", "addBtnClicked2() current limit = ${checkLimit()}")
            showTodayListTable()
        }
    }

    private fun showNewItem(){
        CoroutineScope(Dispatchers.Main).launch{
        var id = getMinInvisID()
        itemIsVisible(id)
        //Log.d("TAG", "showNewItem() call itemIsVisible(${id})")
        val newItem = ListItem(id, getTaskText(id), getTaskDescr(id), getTaskStatus(id),true)
        dataList.set(id, newItem)
        adapter.notifyItemChanged(id)
        binding.recyclerView.scrollToPosition(id)
        }
    }

    private fun reShowItem(){
        CoroutineScope(Dispatchers.Main).launch {
            var id = getLastChangeId()
            itemIsVisible(id)
            val newItem = ListItem(id, getTaskText(id), getTaskDescr(id), getTaskStatus(id), true, getTaskTime(id), true, false)
            dataList.set(id, newItem)
            adapter.notifyItemChanged(id)
            binding.recyclerView.scrollToPosition(id)
        }
    }

    private fun showVisibleItems(){
        CoroutineScope(Dispatchers.Main).launch{
            val visibleItems = getVisibleItems()
            val visibleItem = ListItem(-1,"", "", 0)
            for (item in visibleItems){
                visibleItem.id = item.id
                visibleItem.text = item.task
                visibleItem.description = item.description
                visibleItem.currentTime = item.time
                visibleItem.status = item.status
                visibleItem.isVisible = item.isVisible
                visibleItem.showItem = true
                dataList.set(visibleItem.id,visibleItem)
            }
            adapter.notifyDataSetChanged()
        }

    }

    private suspend fun getVisibleItems():List<TodayList> = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getVisibleItems()
    }

    private fun createFTList(){
        if(!STORAGE.TodayFTListAlreadyCreated){
            val newItem = ListItem(-1,"", "")
            for (i in 0 until STORAGE.LIST_LIMIT) {
                newItem.id = i
                dataListFT.add(newItem)
            }
            adapterFT.notifyDataSetChanged()
            STORAGE.TodayFTListAlreadyCreated = true
        }
       // Log.d("TAG", "createFTList() TodayFTListAlreadyCreated = ${STORAGE.TodayFTListAlreadyCreated}")
    }

    private fun fillFTList(){
        CoroutineScope(Dispatchers.Main).launch{
            val items = getStopItems()
            val newItem = ListItem(-1,"", "", 0)
            for (item in items){
                newItem.id = item.id
                newItem.text = item.task
                newItem.description = item.description
                newItem.status = item.status
                dataListFT.add(newItem)
            }
        }
        adapterFT.notifyDataSetChanged()
    }

    private fun btn_showFTlist(){
        binding.ibFinishedList.setOnClickListener {
            if (STORAGE.ftListVisible){
                STORAGE.ftListVisible=false
                binding.ibFinishedList.setImageResource(R.drawable.ic_show_more_36)
            }
            else {
                STORAGE.ftListVisible=true
                binding.ibFinishedList.setImageResource(R.drawable.ic_show_less_36)
            }
            adapterFT.notifyDataSetChanged()
        }
    }


    private fun addNewFT(){
        CoroutineScope(Dispatchers.Main).launch {
            var id = getLastChangeId()
            itemIsVisible(id)
            if (STORAGE.FTid < STORAGE.LIST_LIMIT-1) STORAGE.FTid++
            val newItem = ListItem(STORAGE.FTid, getTaskText(id), getTaskDescr(id),getTaskStatus(id), false,getTaskTime(id),true,true)
            dataListFT.set(STORAGE.FTid, newItem)
            adapterFT.notifyItemChanged(STORAGE.FTid)
        }
    }

    private suspend fun getTaskText(id:Int):String = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemName(id)
    }

    private suspend fun getTaskDescr(id:Int):String = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemDescription(id)
    }

    private suspend fun getTaskTime(id:Int):Long = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemTime(id)
    }

    private suspend fun getTaskStatus(id:Int):Int = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemStatus(id)
    }

    private suspend fun getStopItems():List<TodayList> = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getStopItems()
    }

    private suspend fun getLastChangeId():Int = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        database.getDaoTodayList().getLastChangedItemId()
        return@withContext database.getDaoTodayList().getLastChangedItemId()
    }


    private fun createList(){
        if(!STORAGE.TodayListAlreadyCreated){
            val newItem = ListItem(-1,"", "")
            for (i in 0 until STORAGE.LIST_LIMIT) {
                newItem.id = i
                newItem.isVisible = false
                dataList.add(newItem)
            }
            adapter.notifyDataSetChanged()
            STORAGE.TodayListAlreadyCreated = true
            showDataList()
        }
       // Log.d("TAG", "createList() TodayListAlreadyCreated = ${STORAGE.TodayListAlreadyCreated}")
    }

    //***********
    private fun checkConfirmDialog(){
        if(STORAGE.IsPressed) {
            showConfirmDialog()
            STORAGE.IsPressed = false
        }
    }

    private fun startPolling() {
        handler.post(pollRunnable)
    }

    private fun stopPolling() {
        handler.removeCallbacks(pollRunnable)
    }

    private val pollRunnable = object : Runnable {
        override fun run() {
            if(STORAGE.returnPressed){
                reShowItem()
                STORAGE.returnPressed = false
            }
            showFtListFrame()
            checkConfirmDialog()
            handler.postDelayed(this, pollingIntervalMillis)
        }
    }

    private fun showFtListFrame(){
        val items = dataListFT
        var cnt = 0
        for (item in items){
            if(item.text!="")cnt++
        }
        if(cnt!=0) {
            binding.containerFt.visibility = VISIBLE
            binding.ftContainer.visibility = VISIBLE
        }
        else {
            binding.containerFt.visibility = GONE
            binding.ftContainer.visibility = GONE
        }
    }

    private fun showConfirmDialog(){

        val alertDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(alertDialogView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)

        val cancelButton = alertDialogView.findViewById<TextView>(R.id.buttonCancel)
        val unsatisButton = alertDialogView.findViewById<TextView>(R.id.buttonUnsatisfactory)
        val partlyButton = alertDialogView.findViewById<TextView>(R.id.buttonPartially)
        val successButton = alertDialogView.findViewById<TextView>(R.id.buttonSuccess)

        cancelButton.setOnClickListener {
            reShowItem()
            alertDialog.dismiss()
        }

        unsatisButton.setOnClickListener {
            updateTaskStatus(1)
            alertDialog.dismiss()
            addNewFT()
        }

        partlyButton.setOnClickListener {
            updateTaskStatus(2)
            alertDialog.dismiss()
            addNewFT()
        }

        successButton.setOnClickListener {
            updateTaskStatus(3)
            alertDialog.dismiss()
            addNewFT()
        }

        alertDialog.show()
    }

    private fun updateTaskStatus(status:Int){
        CoroutineScope(Dispatchers.Main).launch{
            var id = getLastChangeId()
            dataList[id].status = status
            val database = DatabaseMain.getDatabase(requireContext())
            database.getDaoTodayList().setItemStatus(status, id)
        }
    }
    //***********

    override fun onPause() {
        super.onPause()
        stopPolling()
        saveItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopPolling()
        saveItems()
    }
    */