package com.example.diploma.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.R
import com.example.diploma.databinding.FragmentHomeBinding
import com.example.diploma.databinding.ListHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var adapter: ListAdapter
    private lateinit var recyclerView: RecyclerView
    private val dataList = mutableListOf<ListItem>()


    //private var _binding: FragmentHomeBinding? = null
    private var _binding: ListHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = ListHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        setCurDate()


        binding.btnAddItem.setOnClickListener {
            addNewItemToList()
        }

        /*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    private fun init(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ListAdapter(dataList, requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setCurDate(){
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        binding.curDate.text = formattedDate
    }

    private fun addNewItemToList(){
        val newItem = ListItem("")
        dataList.add(newItem)
        adapter.notifyItemInserted(dataList.size - 1)
        binding.recyclerView.scrollToPosition(dataList.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}