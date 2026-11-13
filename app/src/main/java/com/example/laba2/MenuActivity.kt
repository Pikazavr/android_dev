
package com.example.laba2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

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

    private var isAlertRunning by mutableStateOf(false)

    private lateinit var userRepository: UserRepository

    private lateinit var databaseTask: DatabaseThreadTask

    private lateinit var alertTask: AlertBackgroundTask

    private var alertDialog: AlertDialog? = null

    private val tag = "MenuActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRepository = UserRepository(
            DatabaseProvider.getDatabase(this)
        )

        // Обработчик результатов фоновой работы с базой данных
        val handler = Handler(Looper.getMainLooper()) { message: android.os.Message ->

            if (message.what == 1) {

                val result = message.obj as DatabaseTaskResult

                if (result.error != null) {

                    Log.e(
                        tag,
                        "Ошибка фонового запроса: ${result.error}"
                    )

                    Toast.makeText(
                        this,
                        result.error,
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    val count = result.result as Int

                    Log.i(
                        tag,
                        "Количество пользователей: $count"
                    )

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

            true
        }

        // Создаём исполнитель фоновых задач для базы данных
        databaseTask = DatabaseThreadTask(handler)

        // Обработчик сообщений для AlertBackgroundTask
        val alertHandler = Handler(Looper.getMainLooper()) { message ->

            if (message.what == 2) {
                showAlert()
            }

            true
        }

        // Создаём отдельный поток для сообщений
        alertTask = AlertBackgroundTask(
            handler = alertHandler,
            delayMillis = 5000L
        )

        setContent {
            AppTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MenuScreen(
                        isAdmin = isAdmin,

                        isAlertRunning = isAlertRunning,

                        onToggleAlert = {
                            toggleAlert()
                        },

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

                            val intent = Intent(
                                Intent.ACTION_SENDTO
                            ).apply {

                                data = "mailto:".toUri()

                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "Сообщение о проблеме"
                                )
                            }

                            try {

                                startActivity(intent)

                            } catch (_: Exception) {

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


    // Включение и выключение фонового потока
    private fun toggleAlert() {

        if (isAlertRunning) {

            alertTask.stop()

            isAlertRunning = false

            alertDialog?.dismiss()
            alertDialog = null

            Toast.makeText(
                this,
                "Показ сообщений выключен",
                Toast.LENGTH_SHORT
            ).show()

            Log.i(tag, "Фоновый поток остановлен")

        } else {

            isAlertRunning = true

            alertTask.start()

            Toast.makeText(
                this,
                "Показ сообщений включен",
                Toast.LENGTH_SHORT
            ).show()

            Log.i(tag, "Фоновый поток запущен")
        }
    }


    // Показ AlertDialog в главном потоке
    private fun showAlert() {

        if (!isAlertRunning || isFinishing || isDestroyed) {
            return
        }

        alertDialog?.dismiss()

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Напоминание")
            .setMessage("Купи слона")
            .setPositiveButton("OK", null)
            .create()

        alertDialog?.show()

        Log.i(
            tag,
            "Сообщение показано. Поток интерфейса: ${Thread.currentThread().name}"
        )
    }


    override fun onResume() {
        super.onResume()

        val preferences = getSharedPreferences(
            "user_data",
            MODE_PRIVATE
        )

        val userId = preferences.getInt(
            "current_user_id",
            -1
        )

        if (userId == -1) {
            isAdmin = false
            return
        }

        lifecycleScope.launch {

            val user = userRepository.getUserById(userId)

            isAdmin = user?.isAdmin == true
        }
    }


    override fun onDestroy() {

        // Останавливаем фоновый поток
        if (::alertTask.isInitialized) {
            alertTask.stop()
        }

        alertDialog?.dismiss()
        alertDialog = null

        isAlertRunning = false

        Log.i(tag, "MenuActivity уничтожена")

        super.onDestroy()
    }
}


// Интерфейс главного меню
@Composable
fun MenuScreen(
    isAdmin: Boolean,
    isAlertRunning: Boolean,
    onToggleAlert: () -> Unit,
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

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (isAdmin) {

            Button(
                onClick = onAdministration,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Администрирование")
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Button(
            onClick = onProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Профиль")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Настройки")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onReport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сообщить о проблеме")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onTestDatabase,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Проверить фоновый поток")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Кнопка управления сообщениями
        Button(
            onClick = onToggleAlert,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (isAlertRunning) {
                    "Выключить сообщения"
                } else {
                    "Включить сообщения"
                }
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onReturn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вернуться к авторизации")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выход")
        }
    }
}
