package com.example.tryexcel5

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoggedInActivity : AppCompatActivity() {

    private lateinit var user: User
    private var startTime: Long = 0
    private var stopTime: Long = 0
    private var startDateTime: String = ""
    private var endDateTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logged_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize UI elements
        val usernameTV = findViewById<TextView>(R.id.userNameTV)
        val userIdTV = findViewById<TextView>(R.id.userIdTV)
        val buyerNameTV = findViewById<TextView>(R.id.buyerNameTV)
        val userRoleTV = findViewById<TextView>(R.id.userRoleTV)
        val workOrderTV = findViewById<TextView>(R.id.workOrderTV)
        val styleCodeTV = findViewById<TextView>(R.id.styleCodeTV)
        val stepNameTV = findViewById<TextView>(R.id.stepNameTV)
        val stepNumTV = findViewById<TextView>(R.id.stepNumTV)
        val totalQtyTV = findViewById<TextView>(R.id.totalQtyTV)
        val startButton = findViewById<Button>(R.id.startButton)
        val stopButton = findViewById<Button>(R.id.stopButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val startNPTButton = findViewById<Button>(R.id.startNPTButton)
        val stopNPTButton = findViewById<Button>(R.id.stopNPTButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)


        startNPTButton.setOnClickListener {
            stopNPTButton.visibility = View.VISIBLE
            startNPTButton.visibility = View.INVISIBLE

            startButton.isEnabled = false
            stopButton.isEnabled = false
            logoutButton.isEnabled = false

            //
            //
        }

        stopNPTButton.setOnClickListener {
            startNPTButton.visibility = View.VISIBLE
            stopNPTButton.visibility = View.INVISIBLE

            startButton.isEnabled = true
            stopButton.isEnabled = true
            logoutButton.isEnabled = true

            //
            //
        }

        logoutButton.setOnClickListener {
            finish()
        }

        // Retrieve user data from the intent
        val username = intent.getStringExtra("username") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val role = intent.getStringExtra("role") ?: ""
        val buyer = intent.getStringExtra("buyer") ?: ""
        val workOrder = intent.getStringExtra("workOrder") ?: ""
        val styleCode = intent.getStringExtra("styleCode") ?: ""
        val stepName = intent.getStringExtra("stepName") ?: ""
        val stepNumber = intent.getStringExtra("stepNumber") ?: ""
        val totalQty = intent.getStringExtra("totalQty") ?: ""


        // Create User object
        user = User(
            username,
            userId,
            password,
            role,
            buyer,
            workOrder,
            styleCode,
            stepName,
            stepNumber,
            totalQty
        )

        // Display the user data
        usernameTV.text = user.username
        userIdTV.text = user.userId
        userRoleTV.text = user.role
        buyerNameTV.text = user.buyer
        workOrderTV.text = user.workOrder
        styleCodeTV.text = user.styleCode
        stepNameTV.text = user.stepName
        stepNumTV.text = user.stepNumber
        totalQtyTV.text = user.totalQty

        // Handle start button click
        startButton.setOnClickListener {

            startNPTButton.isEnabled = false
            startButton.isEnabled = false
            logoutButton.isEnabled = false

            // Set ProgressBar max to 5 seconds (5000 ms)
//            progressBar.max = 5000
            progressBar.max = 50
            progressBar.progress = 0

            val handler = Handler(Looper.getMainLooper())

            val runnable = object : Runnable {
                var timeElapsed = 0
                override fun run() {
                    timeElapsed += 50
                    progressBar.progress = timeElapsed

//                    if (timeElapsed < 5000) {
                    if (timeElapsed < 50) {
                        handler.postDelayed(this, 50)
                    } else {
                        startButton.visibility = View.GONE
                        stopButton.visibility = View.VISIBLE
                        startButton.isEnabled = true
                    }
                }
            }

            handler.post(runnable)
            startTime = SystemClock.elapsedRealtime()
            startDateTime = getCurrentDateTime()
            Log.d("startButton_TimeCount","Time count started")
        }

        // Handle stop button click
        stopButton.setOnClickListener {

            stopButton.visibility = View.GONE
            startButton.visibility = View.VISIBLE
            startButton.isEnabled = true
            startNPTButton.isEnabled = true
            logoutButton.isEnabled = true

            if (startTime != 0L) {
                stopTime = SystemClock.elapsedRealtime()
                endDateTime = getCurrentDateTime()
                Log.d("stopButton_TimeCount","Time count stopped")


                saveDateTime(user, startDateTime, endDateTime)
                startTime = 0L // Reset startTime
                Log.d("stopButton_DateTime","Date and Time saved.")

            } else {
                Toast.makeText(this, "Please start the timer first", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun saveDateTime(user: User, startTime: String, endTime: String) {

        val baseFolder = File(Environment.getExternalStorageDirectory(), "SmartCount")
        val updatedFolder = File(baseFolder, "MyFiles")

        if (!updatedFolder.exists()) {
            updatedFolder.mkdirs()
        }

        val file = File(updatedFolder, "SmartCount_Updated.xlsx")

        val updatedUser = user.copy(startTime = startTime, endTime = endTime)

        ExcelUtil.appendUser(file, updatedUser)

        Log.d("LoggedInActivity_Time Count","Time count sent to ExcelUtil")
    }
}
