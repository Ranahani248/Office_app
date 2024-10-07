package com.example.officeapp.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.officeapp.Adapters.HolidaysAdapter
import com.example.officeapp.R
import com.example.officeapp.Utils.ApiLinks
import com.example.officeapp.Utils.ApiService
import com.example.officeapp.Utils.GsonHelper
import com.example.officeapp.modelClasses.Holiday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HolidaysActivity : AppCompatActivity() {
    var holidayBack : ImageView? = null
    var holidayRecycler : androidx.recyclerview.widget.RecyclerView? = null
    val holidaysList = ArrayList<Holiday>()
    val holidaysAdapter = HolidaysAdapter(holidaysList)
    var noHolidays  : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_holidays)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        holidayBack = findViewById(R.id.holidayBack)
        holidayRecycler = findViewById(R.id.holidayRecycler)
        noHolidays = findViewById(R.id.noHoliday)
        noHolidays?.visibility = View.GONE


        holidayRecycler?.adapter = holidaysAdapter

        holidayList()
        holidayBack?.setOnClickListener {
            finish()
        }

    }
    fun holidayList(){

        CoroutineScope(Dispatchers.Main).launch {

            val response = withContext(Dispatchers.IO) {
                ApiService.get(ApiLinks.HOLIDAYS_URL, null)
            }

            if(response.isSuccess){
                val responseData = response.getOrNull()
                holidaysList.clear()
                GsonHelper.deserializeFromJson<List<Map<String, String>>>(responseData!!)?.let { holidayMaps ->
                    Log.d("TAG", "holidayList: $holidayMaps")
                    holidayMaps.forEach { holidayMap ->
                        if (holidayMap["title"] != null && holidayMap["from"] != null && holidayMap["to"] != null) {
                            holidaysList.add(Holiday(holidayMap["from"]!!, holidayMap["to"]!!, holidayMap["title"]!!))
                        }
                    }
                    if(holidaysList.size == 0) {
                        noHolidays?.visibility = View.VISIBLE
                    }
                    else{
                        noHolidays?.visibility = View.GONE
                    }
                    holidaysAdapter.notifyDataSetChanged()
                }

            }else{
                Log.d("TAG", "holidayList: ${response.exceptionOrNull()?.message}")
                Log.d("TAG", "holidayList: $response")
                noHolidays?.visibility = View.VISIBLE

            }

            if(holidaysList.size == 0){
                noHolidays?.visibility = View.VISIBLE
            }
        }


    }
}