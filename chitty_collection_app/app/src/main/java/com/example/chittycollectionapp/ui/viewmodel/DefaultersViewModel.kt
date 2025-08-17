package com.example.chittycollectionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.model.Member
import com.example.chittycollectionapp.data.repository.ChittyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DefaultersViewModel(
    repository: ChittyRepository
) : ViewModel() {

    val defaulters: StateFlow<List<Member>> =
        combine(
            repository.getAllChittyGroups(),
            repository.getAllCollections()
        ) { chittyGroups, collections ->
            val defaulters = mutableListOf<Member>()
            val today = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            chittyGroups.forEach { chittyGroup ->
                val mostRecentDueDate = chittyGroup.members
                    .map { dateFormat.parse(it.dueDate) }
                    .filter { !it.after(today.time) } // due date is in the past or today
                    .maxOrNull()

                if (mostRecentDueDate != null) {
                    chittyGroup.members.forEach { member ->
                        val hasPaidForMostRecentDueDate = collections.any { collection ->
                            try {
                                val collectionDate = dateFormat.parse(collection.timestamp.substring(0, 10))
                                collection.memberId == member.memberId &&
                                        collectionDate == mostRecentDueDate &&
                                        collection.paymentStatus == "Paid"
                            } catch (e: Exception) {
                                false
                            }
                        }

                        if (!hasPaidForMostRecentDueDate) {
                            defaulters.add(member)
                        }
                    }
                }
            }
            defaulters
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
