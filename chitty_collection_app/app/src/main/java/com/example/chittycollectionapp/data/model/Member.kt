package com.example.chittycollectionapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = ChittyGroup::class,
            parentColumns = ["chittyId"],
            childColumns = ["chittyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Member(
    @PrimaryKey
    @SerializedName("member_id")
    val memberId: String,
    var chittyId: String,
    @SerializedName("member_name")
    val memberName: String,
    @SerializedName("contact_number")
    val contactNumber: String,
    @SerializedName("due_date")
    val dueDate: String
)
