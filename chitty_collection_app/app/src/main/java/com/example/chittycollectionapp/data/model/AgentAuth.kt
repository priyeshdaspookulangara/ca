package com.example.chittycollectionapp.data.model

import com.google.gson.annotations.SerializedName

data class AgentAuth(
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("loginname")
    val loginName: String,
    @SerializedName("password")
    val password: String
)

data class AgentAuthList(
    @SerializedName("agents")
    val agents: List<AgentAuth>
)
