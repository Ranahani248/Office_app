package com.example.officeapp.Activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.officeapp.R
import com.example.officeapp.Utils.ApiLinks
import com.example.officeapp.Utils.ApiService
import com.example.officeapp.Utils.showCustomToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LeavesActivity : AppCompatActivity() {
    var leavesBack : ImageView? = null
    var fromText: TextView? = null
    var toText: TextView? = null
    var applyLeave : TextView? = null
    var titleLeaves : EditText? = null
    var descriptionLeaves : EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaves)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        leavesBack= findViewById(R.id.leavesBack)

        fromText = findViewById(R.id.fromDate)
        toText = findViewById(R.id.toDate)
        applyLeave = findViewById(R.id.applyLeave)
        titleLeaves = findViewById(R.id.titleLeave)
        descriptionLeaves = findViewById(R.id.descriptionLeave)

        fromText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                if (selectedDate.before(calendar)) {
                    Toast(this).showCustomToast(this, "Selected date cannot be in the past", R.color.yellow)
                } else {
                    // Format date as yyyy-MM-dd
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = sdf.format(selectedDate.time)
                    fromText?.text = formattedDate
                }
            }, year, month, day)

            datePicker.show()
        }


        toText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                if (selectedDate.before(calendar)) {
                    Toast(this).showCustomToast(this, "Selected date cannot be in the past", R.color.yellow)
                } else {
                    // Format date as yyyy-MM-dd
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = sdf.format(selectedDate.time)
                    toText?.text = formattedDate
                }
            }, year, month, day)

            datePicker.show()
        }

        applyLeave?.setOnClickListener {
            applyLeave()
        }





        leavesBack?.setOnClickListener {
           finish()
        }
    }
    fun applyLeave(){
        if(titleLeaves?.text.toString().isEmpty()){
            Toast(this).showCustomToast(this, "Please enter title", R.color.yellow)
            return
        }
        if(descriptionLeaves?.text.toString().isEmpty()){
            Toast(this).showCustomToast(this, "Please enter description", R.color.yellow)
            return
        }

        if (fromText?.text.toString().isEmpty() || toText?.text.toString().isEmpty()) {
            Toast(this).showCustomToast(this, "Please select dates", R.color.yellow)
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val fromDate = sdf.parse(fromText?.text.toString())
        val toDate = sdf.parse(toText?.text.toString())

        if (toDate != null && fromDate != null) {
            if (toDate.before(fromDate)) {
                Toast(this).showCustomToast(this, "To date cannot be before from date", R.color.yellow)
                return
            }
        }

        val parameters = HashMap<String, String>()

        parameters["title"] = titleLeaves?.text.toString()
        parameters["body"] = descriptionLeaves?.text.toString()
        parameters["from"] = fromText?.text.toString()
        parameters["to"] = toText?.text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.IO) {
                ApiService.post(ApiLinks.APPLY_LEAVAES_URL, null, parameters)
            }

            response.onSuccess {responseString ->
                Toast(this@LeavesActivity).showCustomToast(this@LeavesActivity, "Applied Successfully ", R.color.green)
                Log.d("TAG", "applyLeave: $responseString")
            }
                .onFailure {error ->
                    Toast(this@LeavesActivity).showCustomToast(this@LeavesActivity, "Failed $error", R.color.red)
                    Log.d("TAG", "applyLeave: $error")
                }


        }
    }
}