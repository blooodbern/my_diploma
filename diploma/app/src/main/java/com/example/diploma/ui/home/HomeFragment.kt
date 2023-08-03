package com.example.diploma.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.R
import com.example.diploma.databinding.ListHomeBinding
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
        setCurDate()
        AddBtnClicked()


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

    private fun AddBtnClicked(){
        binding.btnAddItem.setOnClickListener {
            addNewItemToList()
            fillFinishedTasksLis()
        }
    }

    private fun addNewItemToList(){
        val newItem = ListItem("")
        dataList.add(newItem)
        adapter.notifyItemInserted(dataList.size - 1)
        binding.recyclerView.scrollToPosition(dataList.size - 1)
    }

    private fun addNewItemToFTList(){
        val newItem = ListItem("")
        dataListFT.add(newItem)
        adapterFT.notifyItemInserted(dataListFT.size - 1)
        binding.recyclerViewFT.scrollToPosition(dataListFT.size - 1)
    }

    //***********
    private fun fillFinishedTasksLis(){
        if(STORAGE.IsPressed) {
            customConfirmDialog()
            addNewItemToFTList()
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
            fillFinishedTasksLis()
            handler.postDelayed(this, pollingIntervalMillis)
        }
    }

    private fun customConfirmDialog(){
        val alertDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(alertDialogView)
        val alertDialog = alertDialogBuilder.create()

        val cancelButton = alertDialogView.findViewById<TextView>(R.id.buttonCancel)
        val unsatisButton = alertDialogView.findViewById<TextView>(R.id.buttonUnsatisfactory)
        val partlyButton = alertDialogView.findViewById<TextView>(R.id.buttonPartially)
        val successButton = alertDialogView.findViewById<TextView>(R.id.buttonSuccess)

        cancelButton.setOnClickListener {

            alertDialog.dismiss()
        }

        unsatisButton.setOnClickListener {

            alertDialog.dismiss()
        }

        partlyButton.setOnClickListener {

            alertDialog.dismiss()
        }

        successButton.setOnClickListener {

            alertDialog.dismiss()
        }

        alertDialog.show()

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