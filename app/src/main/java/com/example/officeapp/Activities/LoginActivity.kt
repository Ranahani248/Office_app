package com.example.officeapp.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.officeapp.R
import com.example.officeapp.Utils.ApiLinks
import com.example.officeapp.Utils.ApiService
import com.example.officeapp.Utils.GsonHelper
import com.example.officeapp.Utils.showCustomToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    lateinit var loginButton : AppCompatButton
    lateinit var email : EditText
    lateinit var password : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            login()
        }
    }
    fun login() {
        // Check if email or password is empty
        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
            Toast(this).showCustomToast(this, "Please enter email and password", R.color.yellow)
            return
        }

        val parameters: Map<String, String> = mapOf(
            "email" to email.text.toString(),
            "password" to password.text.toString()
        )
        val activity = this
        // Use CoroutineScope to launch the network call
        CoroutineScope(Dispatchers.Main).launch {
            // Perform the network request on IO dispatcher
            val response = withContext(Dispatchers.IO) {
                ApiService.post(ApiLinks.LOGIN_URL, null, parameters)
            }

            // Handle the response
            response.onSuccess { responseString ->
                // Update UI on the Main thread
                Toast(activity).showCustomToast(
                    activity,
                    "Login Successful",
                    R.color.green
                )
                lateinit var USERID:String
                GsonHelper.deserializeFromJson<Map<String, Any>>(responseString)?.let {
                    val id = it["id"]
                    USERID = if (id is Double) {
                        id.toInt().toString()  // Convert Double to Int and then to String
                    } else {
                        id.toString()  // For other types, use the toString() method
                    }
                }

                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("id", USERID)
                startActivity(intent)
            }.onFailure { error ->
                // Update UI on the Main thread
                Toast(activity).showCustomToast(activity, "Login Failed $error", R.color.red)
            }
        }
    }
}