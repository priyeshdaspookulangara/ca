package com.example.chittycollectionapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "dividends",
    foreignKeys = [
        ForeignKey(
            entity = ChittyGroup::class,
            parentColumns = ["chittyId"],
            childColumns = ["chittyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Dividend(
    @PrimaryKey(autoGenerate = true)
    val dividendId: Int = 0,
    val chittyId: String,
    val dividendAmount: Long,
    val termDate: String // Format: "YYYY-MM"
)
