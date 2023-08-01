package com.example.diploma.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.databinding.ListHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var adapter: ListAdapter
    private lateinit var adapterFT: ListFtAdapter
    private val dataList = mutableListOf<ListItem>()
    private val dataListFT = mutableListOf<ListItem>()

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


        binding.btnAddItem.setOnClickListener {
            addNewItemToList()

            //***
            Log.d("TAG", "Fragment: IsPressed = ${STORAGE.IsPressed}")
            if(STORAGE.IsPressed) {
                addNewItemToFTList()
                Log.d("TAG", "Fragment: we are after addNewItemToFTList()")
                STORAGE.IsPressed = false
                Log.d("TAG", "Fragment: now IsPressed = ${STORAGE.IsPressed}")
            }
        }


        return root
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

        //****
        Log.d("TAG", "Fragment: we are in addNewItemToFTList()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}