package com.example.officeapp.Adapters

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.officeapp.R
import com.example.officeapp.modelClasses.Announcements
import com.example.officeapp.modelClasses.Holiday

class AnnouncementsAdpater(
    private val announcemenlist: List<Announcements> // Replace String with your model type
) : RecyclerView.Adapter<AnnouncementsAdpater.HolidaysViewHolder>() {



    // Create and inflate the view for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidaysViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.announcement_holder, parent, false)
        return HolidaysViewHolder(view)
    }

    // Bind data to each item view
    override fun onBindViewHolder(holder: HolidaysViewHolder, position: Int) {
        val announcement = announcemenlist[position]
        holder.dateText.text = announcement.date
        holder.message.text = announcement.message
        holder.itemView.setOnClickListener(){
            Log.d("TAG", "onBindViewHolder: "+holder.message.maxLines)
            holder.details.rotation = if (holder.message.maxLines == 1) 180f else 0f
            val isExpanded = holder.message.maxLines == 1
            animateTextViewExpansion(holder.message, isExpanded)
        }
        
    }
    // Function to animate the TextView's expansion and collapse
    fun animateTextViewExpansion(textView: TextView, isExpanded: Boolean) {

        val animationDuration = 200L // Animation duration in milliseconds

        if (isExpanded) {
            // Expanding animation
            textView.animate()
                .setDuration(animationDuration)
                .withStartAction {
                    textView.maxLines = 50 // Set to a fixed reasonable maxLines
                    textView.ellipsize = null
                }
                .start()
        } else {
            // Collapsing animation
            textView.animate()
                .setDuration(animationDuration)
                .withEndAction {
                    textView.maxLines = 1
                    textView.ellipsize = TextUtils.TruncateAt.END
                }
                .start()
        }
    }


    // Return the total number of items
    override fun getItemCount(): Int {
        return announcemenlist.size
    }

    // ViewHolder class holds the views for each item
    class HolidaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.date)
        val message: TextView = itemView.findViewById(R.id.message)
        val details: ImageView = itemView.findViewById(R.id.details)

    }
}
