
package com.example.laba2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
        }

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.reportButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, "Сообщение о проблеме")
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.no_email_app, Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.menuExitButton).setOnClickListener {
            finishAffinity()
        }
    }
}
