package com.example.officeapp.Fragments

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.officeapp.Activities.MainActivity
import com.example.officeapp.R
import com.example.officeapp.Utils.ApiLinks
import com.example.officeapp.Utils.ApiService
import com.example.officeapp.Utils.GsonHelper
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    lateinit var name: TextView
    lateinit var email: TextView
    lateinit var designation: TextView
    lateinit var mobile: EditText
    lateinit var address: EditText
    lateinit var nameEditText: EditText
    lateinit var image: CircleImageView


    lateinit var activity1 :MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            val view = inflater.inflate(R.layout.fragment_profile, container, false)

        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)

        image = view.findViewById(R.id.profile_image)
        designation = view.findViewById(R.id.designation)
        mobile = view.findViewById(R.id.numberEditText)
        address = view.findViewById(R.id.addressEditText)
        nameEditText = view.findViewById(R.id.nameEditText)

        activity1 = activity as MainActivity

        name.setText(activity1.name1)
        email.setText(activity1.email1)
        designation.setText(activity1.designation1)
        mobile.setText(activity1.mobile1)
        address.setText(activity1.address1)
        nameEditText.setText(activity1.name1)

        Glide.with(this)
            .load(activity1.imageUrl)
            .into(image)

        return view
    }


}