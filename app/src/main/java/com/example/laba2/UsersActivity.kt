package com.example.laba2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

class UsersActivity : Activity() {

    private lateinit var userStorage: UserStorage
    private lateinit var adapter: UserAdapter
    private lateinit var usersList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        userStorage = UserStorage(this)
        usersList = findViewById(R.id.usersList)

        adapter = UserAdapter()
        usersList.adapter = adapter

        // Кнопка добавления пользователя
        findViewById<Button>(R.id.addUserButton)
            .setOnClickListener {
                startActivity(
                    Intent(this, RegistrationActivity::class.java)
                )
            }

        // Кнопка возврата в главное меню
        findViewById<Button>(R.id.backToMenuButton)
            .setOnClickListener {
                finish()
            }

        Log.d("UsersActivity", "Список пользователей открыт")
    }

    override fun onResume() {
        super.onResume()
        adapter.updateUsers(userStorage.getUsers())
    }

    private inner class UserAdapter :
        ArrayAdapter<User>(this, 0, mutableListOf()) {

        private val items = mutableListOf<User>()

        fun updateUsers(newUsers: List<User>) {
            items.clear()
            items.addAll(newUsers)
            clear()
            addAll(newUsers)
            notifyDataSetChanged()
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view = convertView ?: LayoutInflater.from(this@UsersActivity)
                .inflate(R.layout.item, parent, false)

            val user = items[position]

            view.findViewById<TextView>(R.id.itemContent).text =
                user.fullName

            view.findViewById<TextView>(R.id.userLogin).text =
                "Логин: ${user.login}"

            view.findViewById<TextView>(R.id.userBirthDate).text =
                "Дата рождения: ${user.birthDate}"

            val avatar = view.findViewById<ImageView>(R.id.userAvatar)
            avatar.setImageResource(
                android.R.drawable.ic_menu_myplaces
            )

            user.avatarUri?.let { uri ->
                try {
                    avatar.setImageURI(Uri.parse(uri))
                } catch (e: Exception) {
                    Log.e(
                        "UsersActivity",
                        "Не удалось загрузить фото",
                        e
                    )
                }
            }

            return view
        }
    }
}