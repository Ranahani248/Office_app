package com.example.officeapp.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.officeapp.Activities.MainActivity
import com.example.officeapp.Adapters.AnnouncementsAdpater
import com.example.officeapp.R
import com.example.officeapp.Utils.ApiLinks
import com.example.officeapp.Utils.ApiService
import com.example.officeapp.Utils.GsonHelper
import com.example.officeapp.modelClasses.Announcements
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.coroutines.coroutineContext


class AnnounementsFragment : Fragment() {


    var NAME : TextView? = null
    var announcementRecycler : androidx.recyclerview.widget.RecyclerView? = null
    var announcementsAdpater : AnnouncementsAdpater? = null
    var announcementsList = ArrayList<Announcements>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_announements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NAME = view.findViewById(R.id.name)
        NAME?.text = MainActivity.NAME

        announcementRecycler = view.findViewById(R.id.announcementRecycler)
        announcementsAdpater = AnnouncementsAdpater(announcementsList)
        announcementRecycler?.adapter = announcementsAdpater
        announcementList()
    }
    fun announcementList(){

        CoroutineScope(Dispatchers.Main).launch {

            val response = withContext(Dispatchers.IO) {
                ApiService.get(ApiLinks.ANNOUNCEMENT_URL, null)
            }
            if (response.isSuccess) {
                val responsedata = response.getOrNull()
                announcementsList.clear()

                GsonHelper.deserializeFromJson<List<Map<String, String>>>(responsedata!!)?.let { announcementMaps ->

                    Log.d("TAG", "announcementList: $announcementMaps")
                    if (announcementMaps.isNotEmpty()) {
                        announcementMaps.forEach { map ->
                            if (map.isNotEmpty()) {
                                if(map["created_at"] != null && map["message"] != null){
                                    val date = formatDateString(map["created_at"]!!)
                                    announcementsList.add(
                                        Announcements(
                                            date,
                                            map["message"]!!
                                        )
                                    )
                                }
                            }
                        }
                        announcementsAdpater?.notifyDataSetChanged()
                    }
                }
            }
            else{
                Log.d ("error", response.toString())
            }
        }
    }
    fun formatDateString(dateString: String): String {
        // Define the input format matching the response
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormatter.timeZone = java.util.TimeZone.getTimeZone("UTC 5") // Set time zone if needed

        // Parse the date string into a Date object
        val date = inputFormatter.parse(dateString)

        // Define the output format (only the date part)
        val outputFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

        // Return the formatted date string
        return outputFormatter.format(date!!)
    }

}