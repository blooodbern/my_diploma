package com.example.diploma.presentation.adapters

import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.EditText
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.diploma.R
import com.example.diploma.domain.ListItem
import com.example.diploma.data.STORAGE


class ListAdapter(private val data: List<ListItem>, private val context: Context) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    // *******************
    private val isButtonPressedList = MutableList(100) { false }
    private var INDEX = 0
    // *******************

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        setupItem(holder, item)
        setCursor(holder, position)
        editDescription(holder, item)
        play(holder, item, position)
        stopChronometer(holder, item, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun setupItem(holder: ViewHolder, item: ListItem) {
        holder.etTask.setText(item.text)
        holder.butStop.setColorFilter(ContextCompat.getColor(context, R.color.custom2))
        holder.butPlayPause.setColorFilter(ContextCompat.getColor(context, R.color.custom2))
        holder.butDescr.setColorFilter(ContextCompat.getColor(context, R.color.custom2))
       //listListAdapt holder.chronometer.setTextColor(ContextCompat.getColor(context, R.color.custom4))
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

    private fun play(holder: ViewHolder, item: ListItem, position: Int){
        holder.butPlayPause.setOnClickListener {
            item.isRunning = !item.isRunning
            changePlayButt(holder, item, position)
        }
    }

    private fun changePlayButt(holder: ViewHolder, item: ListItem, position: Int){
        if (item.isRunning) {
            holder.butPlayPause.setImageResource(R.drawable.ic_pause_36)
            startChronometer(holder, position)
        } else {
            holder.butPlayPause.setImageResource(R.drawable.ic_play_36)
            pauseChronometer(holder, position)
        }
    }

    private fun startChronometer(holder: ViewHolder, position: Int) {
        holder.chronometer.base = SystemClock.elapsedRealtime() - data[position].currentTime
        holder.chronometer.start()
    }

    private fun pauseChronometer(holder: ViewHolder, position: Int) {
        holder.chronometer.stop()
        data[position].currentTime = SystemClock.elapsedRealtime() - holder.chronometer.base
    }

    private fun stopChronometer(holder: ViewHolder, item: ListItem, position: Int) {
        holder.butStop.setOnClickListener {
            item.isStop = !item.isStop

            // *****************

            INDEX = position
            isButtonPressedList[INDEX] = true
            STORAGE.IsPressed = true
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val etTask: EditText = view.findViewById(R.id.et_task)
        val butDescr: ImageButton = view.findViewById(R.id.ib_descr)
        val etDescription: EditText = view.findViewById(R.id.et_description)
        val descriptionForm: CardView = view.findViewById(R.id.cv_description)
        val chronometer: Chronometer = view.findViewById(R.id.chronometer)
        val butStop: ImageButton = view.findViewById(R.id.ib_stop)
        val butPlayPause: ImageButton = view.findViewById(R.id.ib_play_pause)
    }
}
