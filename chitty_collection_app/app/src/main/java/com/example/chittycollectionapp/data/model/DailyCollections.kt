package com.example.chittycollectionapp.data.model

import com.google.gson.annotations.SerializedName

data class DailyCollections(
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("download_date")
    val downloadDate: String,
    @SerializedName("total_collections")
    val totalCollections: Long,
    @SerializedName("collections")
    val collections: List<Collection>
)
