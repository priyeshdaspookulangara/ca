package com.example.chittycollectionapp.data.model

import com.google.gson.annotations.SerializedName

data class InitialData(
    @SerializedName("agent_details")
    val agentDetails: AgentDetails,
    @SerializedName("chitty_groups")
    val chittyGroups: List<ChittyGroup>
)
