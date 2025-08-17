package com.example.chittycollectionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "agent_credentials")
data class AgentCredential(
    @PrimaryKey
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("loginname")
    val loginName: String,
    @SerializedName("password")
    val passwordHash: String
)
