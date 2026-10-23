
package com.example.laba2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MenuScreen(
                        onUsers = {
                            startActivity(
                                Intent(this@MenuActivity, UsersActivity::class.java)
                            )
                        },
                        onProfile = {
                            startActivity(
                                Intent(this@MenuActivity, ProfileActivity::class.java)
                            )
                        },
                        onSettings = {
                            startActivity(
                                Intent(this@MenuActivity, SettingsActivity::class.java)
                            )
                        },
                        onReport = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "Сообщение о проблеме"
                                )
                            }

                            try {
                                startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MenuActivity,
                                    R.string.no_email_app,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onReturn = {
                            val intent = Intent(
                                this@MenuActivity,
                                MainActivity::class.java
                            ).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }

                            startActivity(intent)
                            finish()
                        },
                        onExit = {
                            finishAffinity()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    onUsers: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onReport: () -> Unit,
    onReturn: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Главное меню",
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUsers,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Все пользователи")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Профиль")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Настройки")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onReport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сообщить о проблеме")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onReturn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вернуться к авторизации")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выход")
        }
    }
}
