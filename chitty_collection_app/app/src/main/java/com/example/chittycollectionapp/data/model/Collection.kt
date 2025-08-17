package com.example.chittycollectionapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "collections",
    foreignKeys = [
        ForeignKey(
            entity = ChittyGroup::class,
            parentColumns = ["chittyId"],
            childColumns = ["chittyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Member::class,
            parentColumns = ["memberId"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Collection(
    @PrimaryKey
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("chitty_id")
    val chittyId: String,
    @SerializedName("member_id")
    val memberId: String,
    @SerializedName("collection_amount")
    val collectionAmount: Long,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("notes")
    val notes: String
)
