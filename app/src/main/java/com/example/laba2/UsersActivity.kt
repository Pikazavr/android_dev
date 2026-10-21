package com.example.laba2

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class UsersActivity : Activity() {

    private val users = arrayListOf(
        "Иванов Иван Иванович",
        "Петров Пётр Петрович"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        val usersList = findViewById<ListView>(R.id.usersList)
        val addUserButton = findViewById<Button>(R.id.addUserButton)

        val adapter = ArrayAdapter(
            this,
            R.layout.item,
            R.id.itemContent,
            users
        )

        usersList.adapter = adapter

        addUserButton.setOnClickListener {
            val userName = "Пользователь ${users.size + 1}"

            users.add(userName)
            adapter.notifyDataSetChanged()

            Log.d("UsersActivity", "Добавлен пользователь: $userName")
        }

        Log.d("UsersActivity", "Список пользователей открыт")
    }
}