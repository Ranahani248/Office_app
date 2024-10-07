package com.example.officeapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.officeapp.R
import com.example.officeapp.modelClasses.Holiday

class HolidaysAdapter(
    private val holidaysList: List<Holiday> // Replace String with your model type
) : RecyclerView.Adapter<HolidaysAdapter.HolidaysViewHolder>() {



    // Create and inflate the view for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidaysViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.holidays_recycler_holder, parent, false)
        return HolidaysViewHolder(view)
    }

    // Bind data to each item view
    override fun onBindViewHolder(holder: HolidaysViewHolder, position: Int) {
        val holiday = holidaysList[position]
        holder.fromHoliday.text = holiday.from
        holder.toHoliday.text = holiday.to
        holder.title.text = holiday.title
    }

    // Return the total number of items
    override fun getItemCount(): Int {
        return holidaysList.size
    }

    // ViewHolder class holds the views for each item
    class HolidaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromHoliday: TextView = itemView.findViewById(R.id.fromHoliday)
        val toHoliday: TextView = itemView.findViewById(R.id.toHoliday)
        val title: TextView = itemView.findViewById(R.id.title)

    }
}
