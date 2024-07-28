package com.example.tryexcel5

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts



private const val TAG = "PermissionCheck" // Log tag for debugging
private const val STORAGE_PERMISSION_CODE = 100 // Request code for storage permissions

class MainActivity : AppCompatActivity() {

    private val storagePermissionsArray = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var permissionGrant = false // Flag to check if permissions are granted

    private lateinit var userIdET: EditText
    private lateinit var passwordET: EditText
    private lateinit var loginButton: Button
    private lateinit var users: List<User>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize UI elements
        userIdET = findViewById(R.id.userIdET)
        passwordET = findViewById(R.id.passwordET)
        loginButton = findViewById(R.id.loginButton)


        // Check if storage permissions are already granted
        if (checkReadWritePermission()) {
            permissionGrant = true
        } else {
            requestStoragePermission() // Request storage permissions if not granted
        }

        // IF storage permissions is granted
        if (permissionGrant) {
            createFolders()// Create necessary folders
        } else {
            requestStoragePermission() // Request permissions if not granted
        }



        readUserData()// Load user data from Excel file

    }

    /**
     * Loads user data from the Excel file in the BossFiles folder.
     */
    private fun readUserData() {

        val baseFolder = File(Environment.getExternalStorageDirectory(), "SmartCount")
        val bossFilesFolder = File(baseFolder, "BossFiles")
        val file = File(bossFilesFolder, "SmartCount.xlsx")

        try {
            if (file.exists()) {
                users = ExcelUtil.readExcel(file)

                // Handle login button click
                loginButton.setOnClickListener {
                    userLogin()
                }

                Log.d("LoginActivity", "User data loaded successfully")
            } else {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
                loginButton.setOnClickListener {
                    Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
                }
                Log.d("LoginActivity", "File not found: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error loading user data: ${e.message}", e)
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles user login by validating credentials.
     */
    private fun userLogin() {
        val userId = userIdET.text.toString()
        val password = passwordET.text.toString()
        val user = validateCredentials(userId, password)

        if (user != null) {
            val intent = Intent(this, LoggedInActivity::class.java).apply {
                putExtra("username", user.username)
                putExtra("userId", user.userId)
                putExtra("password", user.password)
                putExtra("role", user.role)
                putExtra("buyer", user.buyer)
                putExtra("workOrder", user.workOrder)
                putExtra("styleCode", user.styleCode)
                putExtra("stepName", user.stepName)
                putExtra("stepNumber", user.stepNumber)
                putExtra("totalQty", user.totalQty)
            }
            startActivity(intent)
            Log.d("LoginActivity","Data intent to LoggedInActivity")
            userIdET.text.clear() // Clear the userIdET field
            passwordET.text.clear() // Clear the passwordET field

        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Validates the given username and password.
     * @param userId The userId to validate.
     * @param password The password to validate.
     * @return The User object if valid, null otherwise.
     */

    private fun validateCredentials(userId: String, password: String): User? {
        Log.d("LoginActivity", "Validating login credential")
        return users.find { it.userId == userId && it.password == password }
    }


    /**
     * Take the necessary permissions and handle result
     * Creates the necessary folders if they do not exist.
     */

    // Function to create the required folders
    private fun createFolders() {
        val baseFolder = File(Environment.getExternalStorageDirectory(), "SmartCount")
        val bossFilesFolder = File(baseFolder, "BossFiles")
        val myFilesFolder = File(baseFolder, "MyFiles")

        // Check if the base folder already exists
        if (baseFolder.exists() && baseFolder.isDirectory) {
            toast("Base folder 'SmartCount' already exists")
        } else {
            if (baseFolder.mkdirs()) {
                toast("Base folder 'SmartCount' created")
            } else {
                toast("Failed to create base folder 'SmartCount'")
                return
            }
        }

        // Create subfolders
        createSubfolder(bossFilesFolder, "BossFiles")
        createSubfolder(myFilesFolder, "MyFiles")
    }

    // Helper function to create a subfolder
    private fun createSubfolder(folder: File, name: String) {
        if (folder.exists() && folder.isDirectory) {
//            toast("Subfolder '$name' already exists")
        } else {
            if (folder.mkdirs()) {
                toast("Subfolder '$name' created")
            } else {
                toast("Failed to create subfolder '$name'")
            }
        }
    }

    // Function to request storage permissions
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                sdkUpperActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "error ", e)
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                sdkUpperActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                storagePermissionsArray,
                STORAGE_PERMISSION_CODE
            )
        }
    }

    // ActivityResultLauncher to handle the result of the permission request
    private val sdkUpperActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Log.d(TAG, "Manage External Storage Permission is granted")
                permissionGrant = true
                createFolders()
            } else {
                Log.d(TAG, "Permission is denied")
                toast("Manage External Storage Permission is denied")
            }
        }
    }

    // Function to check if read/write permissions are granted
    private fun checkReadWritePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            storagePermissionsArray.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d(TAG, "External Storage Permission granted")
                permissionGrant = true
                createFolders()
            } else {
                Log.d(TAG, "Some Permission denied...")
                toast("Some Storage Permission denied...")
            }
        }
    }

    // Helper function to show a toast message
    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}