package com.example.laba2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class RegistrationActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText

    private val tag = "RegistrationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")
        setContentView(R.layout.activity_registration)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        loginEditText = findViewById(R.id.loginRegistrationEditText)
        passwordEditText = findViewById(R.id.passwordRegistrationEditText)

        findViewById<Button>(R.id.saveRegistrationButton).setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val login = loginEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                login.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    R.string.login_required,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("user_data", MODE_PRIVATE)

            prefs.edit {
                putString("name", name)
                putString("email", email)
                putString("phone", phone)
                putString("login", login)
                putString("password", password)
            }

            Toast.makeText(
                this,
                R.string.save_button,
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(tag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(tag, "onResume")
    }

    override fun onPause() {
        Log.i(tag, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.i(tag, "onStop")
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(tag, "onRestart")
    }

    override fun onDestroy() {
        Log.i(tag, "onDestroy")
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("saved_name", nameEditText.text.toString())
        outState.putString("saved_email", emailEditText.text.toString())
        outState.putString("saved_phone", phoneEditText.text.toString())
        outState.putString("saved_login", loginEditText.text.toString())
        outState.putString("saved_password", passwordEditText.text.toString())

        Log.i(tag, "onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        nameEditText.setText(savedInstanceState.getString("saved_name", ""))
        emailEditText.setText(savedInstanceState.getString("saved_email", ""))
        phoneEditText.setText(savedInstanceState.getString("saved_phone", ""))
        loginEditText.setText(savedInstanceState.getString("saved_login", ""))
        passwordEditText.setText(savedInstanceState.getString("saved_password", ""))

        Log.i(tag, "onRestoreInstanceState")
    }
}