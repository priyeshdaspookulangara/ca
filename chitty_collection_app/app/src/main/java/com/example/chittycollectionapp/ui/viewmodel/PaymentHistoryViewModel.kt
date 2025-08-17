package com.example.chittycollectionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.model.Collection
import com.example.chittycollectionapp.data.repository.ChittyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PaymentHistoryViewModel(
    repository: ChittyRepository,
    memberId: String
) : ViewModel() {

    val paymentHistory: StateFlow<List<Collection>> =
        repository.getCollectionsForMember(memberId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
