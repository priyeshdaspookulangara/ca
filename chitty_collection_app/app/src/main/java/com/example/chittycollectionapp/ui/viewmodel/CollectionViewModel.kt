package com.example.chittycollectionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.model.Collection
import com.example.chittycollectionapp.data.repository.ChittyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CollectionViewModel(
    private val repository: ChittyRepository,
    private val chittyId: String,
    private val memberId: String
) : ViewModel() {

    fun saveCollection(
        amount: Long,
        paymentMethod: String,
        paymentStatus: String,
        notes: String
    ) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
            val transactionId = "TX_${chittyId}_${memberId}_${System.currentTimeMillis()}"
            val collection = Collection(
                transactionId = transactionId,
                chittyId = chittyId,
                memberId = memberId,
                collectionAmount = amount,
                paymentMethod = paymentMethod,
                paymentStatus = paymentStatus,
                timestamp = timestamp,
                notes = notes
            )
            repository.insertCollection(collection)
        }
    }
}
