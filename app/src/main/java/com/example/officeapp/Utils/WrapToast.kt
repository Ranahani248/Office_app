package com.example.officeapp.Utils

import android.app.Activity
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.example.officeapp.R


    fun Toast.showCustomToast(activity: Activity, message: String, color: Int)
    {
        val layout = activity.layoutInflater.inflate (
            R.layout.toast_container,
            activity.findViewById(R.id.toast_container)
        )

        // set the text of the TextView of the message
        val textView = layout.findViewById<TextView>(R.id.toast_text)

        textView.text = message


        val button_accent_border = layout.findViewById<FrameLayout>(R.id.button_accent_border)
        button_accent_border.setBackgroundResource(color)

        // use the application extension function
        this.apply {
            setGravity(Gravity.BOTTOM, 0, 40)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
