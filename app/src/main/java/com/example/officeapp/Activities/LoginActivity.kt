package com.example.officeapp.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.TextView
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
import com.example.officeapp.Utils.PreferencesManager
import com.example.officeapp.Utils.showCustomToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    lateinit var loginButton : AppCompatButton
     var email : EditText? = null
     var password : EditText? = null
    var progresslayout : androidx.constraintlayout.widget.ConstraintLayout? = null
    var loadingText : TextView? = null
    var handler: Handler? = null
    var dotCount = 0
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        preferencesManager = PreferencesManager(this)


        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        progresslayout = findViewById(R.id.progresslayout)
        loadingText = findViewById(R.id.loadingText)

        autoLogin()

        loginButton.setOnClickListener {
            login()
        }
    }
    private fun autoLogin() {
        val savedEmail = preferencesManager.getString("loginDetails", "userEmail")
        val savedPassword = preferencesManager.getString("loginDetails", "password")

        // Check if saved credentials exist
        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            Log.d("TAG", "Saved credentials: $savedEmail, $savedPassword")
            email?.setText(savedEmail)
            password?.setText(savedPassword)
            login()
        }
    }
    fun progress(progress: Boolean) {
        if (progress) {
            progresslayout?.visibility = android.view.View.VISIBLE

            // Create a handler for updating text
            handler = Handler(Looper.getMainLooper())
            handler?.post(object : Runnable {
                override fun run() {
                    val dots = ".".repeat(dotCount % 4) // Add up to 3 dots
                    loadingText?.text = "Logging in$dots"
                    dotCount++
                    if (progress) {
                        handler?.postDelayed(this, 500L) // Update every 500ms
                    }
                }
            })
        } else {
            progresslayout?.visibility = android.view.View.GONE
            handler?.removeCallbacksAndMessages(null)
        }
    }

    fun login() {
        if (email?.text.toString().isEmpty() || password?.text.toString().isEmpty()) {
            Toast(this).showCustomToast(this, "Please enter email and password", R.color.yellow)
            return
        }
        progress(true)

        val parameters: Map<String, String> = mapOf(
            "email" to email?.text.toString(),
            "password" to password?.text.toString()
        )
        Log.d("TAG", "login: $parameters")
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
                 var USERID:String = ""
                var NAME:String = ""
                GsonHelper.deserializeFromJson<Map<String, Any>>(responseString)?.let {
                    val id = it["id"]
                    USERID = if (id is Double) {
                        id.toInt().toString()  // Convert Double to Int and then to String
                    } else {
                        id.toString()  // For other types, use the toString() method
                    }
                     NAME = it["name"].toString()
                }
                progress(false)

                preferencesManager.saveString("loginDetails", "userEmail", email?.text.toString())
                preferencesManager.saveString("loginDetails", "password", password?.text.toString())

                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("id", USERID)
                MainActivity.NAME = NAME
                startActivity(intent)
                finish()
            }.onFailure { error ->
                // Update UI on the Main thread
                progress(false)

                Toast(activity).showCustomToast(activity, "Login Failed $error", R.color.red)
                Log.d("Login Failed", error.toString())
            }
        }
    }
}