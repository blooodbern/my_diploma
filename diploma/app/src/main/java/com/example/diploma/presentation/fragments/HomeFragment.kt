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
import com.example.diploma.databinding.ListHomeBinding
import com.example.diploma.presentation.adapters.ListAdapter
import com.example.diploma.presentation.adapters.ListFtAdapter
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
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var adapter: ListAdapter
    private lateinit var adapterFT: ListFtAdapter
    private val dataList = mutableListOf<ListItem>()
    private val dataListFT = mutableListOf<ListItem>()

    private val handler = Handler(Looper.getMainLooper())
    private val pollingIntervalMillis: Long = 1000

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

        createFTList()
        btn_showFTlist()


        return root
    }

    override fun onResume() {
        super.onResume()
        startPolling()
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

    private fun getMaxID2():Int{
        var idMax:Int=-1
        var thread2 = Thread{
            val database = DatabaseMain.getDatabase(requireContext())
            idMax = database.getDaoTodayList().getMaxID()
        }
        thread2.start()
        thread2.join()
        Log.d("TAG", "idMax = ${idMax}")        
        return idMax
    }

    private fun getMinInvisID():Int{
        var minId:Int = -1
        var thread3 = Thread{
            val database = DatabaseMain.getDatabase(requireContext())
            minId = database.getDaoTodayList().getMinInvisibleID()
        }
        thread3.start()
        thread3.join()
        Log.d("TAG", "minId = ${minId}")
        return minId
    }

    private fun itemIsVisible(itemId: Int){
        var thread4 = Thread{
            val database = DatabaseMain.getDatabase(requireContext())
            database.getDaoTodayList().setItemVisible(itemId)
        }
        thread4.start()
        thread4.join()
    }

    private fun checkLimit():Int{
        var invisibleItemsAmount:Int = -1
        var thread5 = Thread{
            val database = DatabaseMain.getDatabase(requireContext())
            invisibleItemsAmount = database.getDaoTodayList().checkInvisibleItems()
        }
        thread5.start()
        thread5.join()
        Log.d("TAG", "visibleItemsAmount = ${invisibleItemsAmount}")
        return invisibleItemsAmount
    }

    private fun fillTodayDataBase(){
        if (!STORAGE.DatabaseTodayListAlreadyCreated && getMaxID2() != STORAGE.LIST_LIMIT-1){
            for (i in 0 until STORAGE.LIST_LIMIT){
                val task = TodayList(
                    i,
                    "",
                    "",
                    0L,
                    "",
                    false,
                    false,
                    false,
                    false
                )
                var thread1 = Thread{
                    val database = DatabaseMain.getDatabase(requireContext())
                    database.getDaoTodayList().insertItem(task)
                }
                thread1.start()
                thread1.join()
            }
            STORAGE.DatabaseTodayListAlreadyCreated = true
        }
        showTodayListTable()
    }

    private fun showTodayListTable(){
        Log.d("showDatabaseToday", "showTodayListTable() is called")
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
    }

    private fun addBtnClicked(){
        binding.btnAddItem.setOnClickListener {
            if(checkLimit()>0) showNewItem()
            else Toast.makeText(requireContext(), "limit exceeded", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "addBtnClicked2() current limit = ${checkLimit()}")
            showTodayListTable()
        }
    }

    private fun showNewItem(){
        var id = getMinInvisID()
        itemIsVisible(id)
        val newItem = ListItem("", "", "",true)
        dataList.set(id, newItem)
        adapter.notifyItemChanged(id)
        binding.recyclerView.scrollToPosition(id)
    }

    private fun reShowItem(){
        CoroutineScope(Dispatchers.Main).launch {
            var id = getLastChangeId()
            itemIsVisible(id)
            val newItem = ListItem(getTaskText(id), getTaskDescr(id),"", true, getTaskTime(id), true)
            dataList.set(id, newItem)
            adapter.notifyItemChanged(id)
            binding.recyclerView.scrollToPosition(id)
        }
    }

    private fun createFTList(){
        if(!STORAGE.TodayFTListAlreadyCreated){
            val newItem = ListItem("", "", "")
            for (i in 0 until STORAGE.LIST_LIMIT) dataListFT.add(newItem)
            adapterFT.notifyDataSetChanged()
            STORAGE.TodayFTListAlreadyCreated = true
        }
        Log.d("TAG", "createFTList() TodayFTListAlreadyCreated = ${STORAGE.TodayFTListAlreadyCreated}")
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
            val newItem = ListItem(getTaskText(id), getTaskDescr(id),getTaskStatus(id))
            if (STORAGE.FTid < STORAGE.LIST_LIMIT-1) STORAGE.FTid++
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

    private suspend fun getTaskStatus(id:Int):String = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemStatus(id)
    }

    private fun getLastChangeId():Int{
        var id:Int = -1
        var threadLastChange = Thread{
            val database = DatabaseMain.getDatabase(requireContext())
            id = database.getDaoTodayList().getLastChangedItemId()
        }
        threadLastChange.start()
        threadLastChange.join()
        return id
    }


    private fun createList(){
        if(!STORAGE.TodayListAlreadyCreated){
            val newItem = ListItem("", "")
            for (i in 0 until STORAGE.LIST_LIMIT) dataList.add(newItem)
            adapter.notifyDataSetChanged()
            STORAGE.TodayListAlreadyCreated = true
        }
        Log.d("TAG", "createList() TodayListAlreadyCreated = ${STORAGE.TodayListAlreadyCreated}")
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
            updateTaskStatus2(getString(R.string.task_unsatis))
            alertDialog.dismiss()
            addNewFT()
        }

        partlyButton.setOnClickListener {
            updateTaskStatus2(getString(R.string.task_partly))
            alertDialog.dismiss()
            addNewFT()
        }

        successButton.setOnClickListener {
            updateTaskStatus2(getString(R.string.task_success))
            alertDialog.dismiss()
            addNewFT()
        }

        alertDialog.show()
    }

    private fun updateTaskStatus2(status:String){
        var threadUpdateStatus = Thread{
            val database = DatabaseMain.getDatabase(requireContext())
            database.getDaoTodayList().setItemStatus(status)
        }
        threadUpdateStatus.start()
        threadUpdateStatus.join()
    }
    //***********

    override fun onPause() {
        super.onPause()
        stopPolling()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopPolling()
    }
}