
package com.example.laba2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText

    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate")
        setContentView(R.layout.activity_main)

        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val login = loginEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show()
            } else {
                val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
                prefs.edit().putString("login", login).apply()

                startActivity(Intent(this, MenuActivity::class.java))
            }
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finishAffinity()
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
        outState.putString("saved_login", loginEditText.text.toString())
        outState.putString("saved_password", passwordEditText.text.toString())
        Log.i(tag, "onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        loginEditText.setText(savedInstanceState.getString("saved_login", ""))
        passwordEditText.setText(savedInstanceState.getString("saved_password", ""))
        Log.i(tag, "onRestoreInstanceState")
    }
}
