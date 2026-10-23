
package com.example.laba2

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory

class UsersActivity : AppCompatActivity() {

    private lateinit var userStorage: UserStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userStorage = UserStorage(this)

        Log.d("UsersActivity", "Список пользователей открыт")

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UsersScreen(
                        users = userStorage.getUsers(),
                        onAddUser = {
                            startActivity(
                                Intent(
                                    this@UsersActivity,
                                    RegistrationActivity::class.java
                                )
                            )
                        },
                        onBack = {
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // При возвращении на экран список обновится в UsersScreen
        // при следующем создании экрана; обновление при возврате
        // выполняется через обновление состояния ниже.
    }
}

@Composable
fun UsersScreen(
    users: List<User>,
    onAddUser: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Все пользователи",
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (users.isEmpty()) {
            Text("Пользователей пока нет")
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users, key = { it.login }) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val bitmap = remember(user.avatarUri) {
                            try {
                                user.avatarUri?.let { uriString ->
                                    context.contentResolver.openInputStream(
                                        Uri.parse(uriString)
                                    )?.use { stream ->
                                        BitmapFactory.decodeStream(stream)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "UsersActivity",
                                    "Не удалось загрузить фото",
                                    e
                                )
                                null
                            }
                        }

                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Аватар пользователя",
                                modifier = Modifier.size(64.dp)
                            )
                        } else {
                            Text(
                                text = "👤",
                                fontSize = 40.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = user.fullName,
                                fontSize = 18.sp
                            )
                            Text("Логин: ${user.login}")
                            Text("Дата рождения: ${user.birthDate}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAddUser,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить пользователя")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("В главное меню")
        }
    }
}
