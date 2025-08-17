package com.example.chittycollectionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "agent_details")
data class AgentDetails(
    @PrimaryKey
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("agent_name")
    val agentName: String,
    @SerializedName("contact_number")
    val contactNumber: String
)
