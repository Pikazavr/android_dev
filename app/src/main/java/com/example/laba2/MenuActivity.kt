
package com.example.laba2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {

    private var isAdmin by mutableStateOf(false)

    private lateinit var userRepository: UserRepository
    private lateinit var databaseTask: DatabaseThreadTask

    private val tag = "MenuActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRepository = UserRepository(
            DatabaseProvider.getDatabase(this)
        )

        // Создаём обработчик для получения результата в главном потоке
        val handler = DatabaseThreadTask.createMainHandler { message ->

            if (message.what == 1) {
                val result = message.obj as DatabaseTaskResult

                if (result.error != null) {
                    Log.e(tag, "Ошибка фонового запроса: ${result.error}")

                    Toast.makeText(
                        this,
                        result.error,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val count = result.result as Int

                    Log.i(tag, "Количество пользователей: $count")
                    Log.i(
                        tag,
                        "Результат получен в потоке: ${Thread.currentThread().name}"
                    )

                    Toast.makeText(
                        this,
                        "Запрос выполнен! Пользователей в базе: $count",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Создаём исполнитель фоновых задач с задержкой 3 секунды
        databaseTask = DatabaseThreadTask(handler)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MenuScreen(
                        isAdmin = isAdmin,

                        onTestDatabase = {
                            Toast.makeText(
                                this@MenuActivity,
                                "Запрос к базе запущен...",
                                Toast.LENGTH_SHORT
                            ).show()

                            databaseTask.execute(1) {
                                Log.i(
                                    tag,
                                    "Запрос выполняется в потоке: ${Thread.currentThread().name}"
                                )

                                userRepository.getUsers().size
                            }
                        },

                        onAdministration = {
                            startActivity(
                                Intent(
                                    this@MenuActivity,
                                    UsersActivity::class.java
                                )
                            )
                        },

                        onProfile = {
                            startActivity(
                                Intent(
                                    this@MenuActivity,
                                    ProfileActivity::class.java
                                )
                            )
                        },

                        onSettings = {
                            startActivity(
                                Intent(
                                    this@MenuActivity,
                                    SettingsActivity::class.java
                                )
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

    override fun onResume() {
        super.onResume()

        val preferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userId = preferences.getInt("current_user_id", -1)

        if (userId == -1) {
            isAdmin = false
            return
        }

        lifecycleScope.launch {
            val user = userRepository.getUserById(userId)
            isAdmin = user?.isAdmin == true
        }
    }
}

@Composable
fun MenuScreen(
    isAdmin: Boolean,
    onTestDatabase: () -> Unit,
    onAdministration: () -> Unit,
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

        if (isAdmin) {
            Button(
                onClick = onAdministration,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Администрирование")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

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
            onClick = onTestDatabase,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Проверить фоновый поток")
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
