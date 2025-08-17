package com.example.chittycollectionapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chitty_groups")
data class ChittyGroup(
    @PrimaryKey
    @SerializedName("chitty_id")
    val chittyId: String,
    @SerializedName("chitty_name")
    val chittyName: String,
    @SerializedName("chitty_amount")
    val chittyAmount: Long,
    @SerializedName("installment_amount")
    val installmentAmount: Long,
    @SerializedName("total_members")
    val totalMembers: Int,
    @SerializedName("members")
    val members: List<Member>
)
