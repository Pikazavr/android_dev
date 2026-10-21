
package com.example.laba2

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class UserStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "users_storage",
        Context.MODE_PRIVATE
    )

    fun getUsers(): MutableList<User> {
        val result = mutableListOf<User>()
        val json = preferences.getString("users", "[]") ?: "[]"
        val array = JSONArray(json)

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            result.add(
                User(
                    login = obj.getString("login"),
                    fullName = obj.getString("fullName"),
                    birthDate = obj.optString("birthDate", ""),
                    gender = obj.optString("gender", "Не указан"),
                    avatarUri = if (obj.isNull("avatarUri")) {
                        null
                    } else {
                        obj.getString("avatarUri")
                    }
                )
            )
        }

        return result
    }

    fun addUser(user: User): Boolean {
        val users = getUsers()

        if (users.any {
                it.login.equals(user.login, ignoreCase = true)
            }) {
            return false
        }

        users.add(user)

        val array = JSONArray()

        users.forEach { item ->
            val obj = JSONObject()
            obj.put("login", item.login)
            obj.put("fullName", item.fullName)
            obj.put("birthDate", item.birthDate)
            obj.put("gender", item.gender)
            obj.put("avatarUri", item.avatarUri ?: JSONObject.NULL)
            array.put(obj)
        }

        preferences.edit()
            .putString("users", array.toString())
            .apply()

        return true
    }
}
