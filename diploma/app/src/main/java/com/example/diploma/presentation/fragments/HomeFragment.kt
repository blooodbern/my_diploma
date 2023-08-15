package com.example.diploma.presentation.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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

    private var task_status = "" // getString(R.string.task_success)
    private var STATUS:Int = 0


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
        addBtnClicked2()

       // AddBtnClicked()

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
                        "DESC: ${item.description} STATUS: ${item.status} TIME: ${item.time} " +
                        "isPlaying: ${item.isPlaying} isStop: ${item.isStop} " +
                        "isVisible: ${item.isVisible} lastChanged: ${item.lastChanged}")
            }
        }
        thread2.start()
        thread2.join()
    }

    private fun addBtnClicked2(){
        binding.btnAddItem.setOnClickListener {
            if(checkLimit()>0) showNewItem2()
            else Toast.makeText(requireContext(), "limit exceeded", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "addBtnClicked2() current limit = ${checkLimit()}")
            showTodayListTable()
        }
    }

    private fun showNewItem2(){
        var id = getMinInvisID()
        itemIsVisible(id)
        val newItem = ListItem("", "", true)
        dataList.set(id, newItem)
        adapter.notifyItemChanged(id)
        binding.recyclerView.scrollToPosition(id)
    }

    private fun reShowItem2(){
        CoroutineScope(Dispatchers.Main).launch {
            var id = getLastChangeId()
            itemIsVisible(id)
            val newItem = ListItem(getTaskText2(id), getTaskDescr2(id), true, getTaskTime2(id))
            dataList.set(id, newItem)
            adapter.notifyItemChanged(id)
            binding.recyclerView.scrollToPosition(id)
        }

    }

    private suspend fun getTaskText2(id:Int):String = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemName(id)
    }

    private suspend fun getTaskDescr2(id:Int):String = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemDescription(id)
    }

    private suspend fun getTaskTime2(id:Int):Long = withContext(Dispatchers.IO){
        val database = DatabaseMain.getDatabase(requireContext())
        return@withContext database.getDaoTodayList().getItemTime(id)
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

    private fun AddBtnClicked(){
        binding.btnAddItem.setOnClickListener {
            if (STORAGE.maxPosition<STORAGE.LIST_LIMIT) STORAGE.maxPosition++
            if (STORAGE.maxPosition in 0..STORAGE.LIST_LIMIT-1) showNewItem()
            Log.d("TAG", "AddBtnClicked() maxPosition = ${STORAGE.maxPosition}")
        }
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

    private fun addNewItemToList(returnItem: Boolean){
        val newItem = ListItem("", "", returnItem)
        dataList.add(newItem)
        adapter.notifyItemInserted(dataList.size - 1)
        Log.d("TAG", "addNewItemToList() maxPosition = ${STORAGE.maxPosition}")
    }

    private fun showNewItem(){
        adapter.notifyItemInserted(STORAGE.maxPosition)
        binding.recyclerView.scrollToPosition(STORAGE.maxPosition)
    }

    private fun addNewItemToFTList(){
        task_status = getTaskStatus(STATUS)
        val newItem = ListItem(STORAGE.curTask, task_status)
        dataListFT.add(newItem)
        adapterFT.notifyItemInserted(dataListFT.size - 1)
        binding.recyclerViewFT.scrollToPosition(dataListFT.size - 1)
    }

    //***********
    private fun checkConfirmDialog(){
        if(STORAGE.IsPressed) {
            showConfirmDialog2()
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
            checkConfirmDialog()
            handler.postDelayed(this, pollingIntervalMillis)
        }
    }

    private fun showConfirmDialog2(){
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
            reShowItem2()
            alertDialog.dismiss()
        }

        unsatisButton.setOnClickListener {
            updateTaskStatus2(getString(R.string.task_unsatis))
            alertDialog.dismiss()
        }

        partlyButton.setOnClickListener {
            updateTaskStatus2(getString(R.string.task_partly))
            alertDialog.dismiss()
        }

        successButton.setOnClickListener {
            updateTaskStatus2(getString(R.string.task_success))
            alertDialog.dismiss()
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

    private fun showConfirmDialog(){
        val alertDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(alertDialogView)
        val alertDialog = alertDialogBuilder.create()

        val cancelButton = alertDialogView.findViewById<TextView>(R.id.buttonCancel)
        val unsatisButton = alertDialogView.findViewById<TextView>(R.id.buttonUnsatisfactory)
        val partlyButton = alertDialogView.findViewById<TextView>(R.id.buttonPartially)
        val successButton = alertDialogView.findViewById<TextView>(R.id.buttonSuccess)

        cancelButton.setOnClickListener {
            addNewItemToList(true)
            alertDialog.dismiss()
        }

        unsatisButton.setOnClickListener {
            STATUS = STORAGE.UNSATISFACTORY
            addNewItemToFTList()
            alertDialog.dismiss()
        }

        partlyButton.setOnClickListener {
            STATUS = STORAGE.PARTLY
            addNewItemToFTList()
            alertDialog.dismiss()
        }

        successButton.setOnClickListener {
            STATUS = STORAGE.SUCCESS
            addNewItemToFTList()
            alertDialog.dismiss()
        }

        alertDialog.show()

    }

    private fun getTaskStatus(status:Int):String{
        var curTaskSatus:String = ""
        when(status){
            STORAGE.IN_PROCESS -> curTaskSatus = getString(R.string.task_inProcess)
            STORAGE.UNSATISFACTORY -> curTaskSatus = getString(R.string.task_unsatis)
            STORAGE.PARTLY -> curTaskSatus = getString(R.string.task_partly)
            STORAGE.SUCCESS -> curTaskSatus = getString(R.string.task_success)
            STORAGE.NOT_STARTED -> curTaskSatus = getString(R.string.task_notStarted)
        }
        return curTaskSatus
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