package com.example.chittycollectionapp.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromMemberList(members: List<Member>?): String? {
        if (members == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Member>>() {}.type
        return gson.toJson(members, type)
    }

    @TypeConverter
    fun toMemberList(membersString: String?): List<Member>? {
        if (membersString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Member>>() {}.type
        return gson.fromJson(membersString, type)
    }
}
