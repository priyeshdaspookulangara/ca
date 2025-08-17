package com.example.chittycollectionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.model.ChittyGroup
import com.example.chittycollectionapp.data.model.Member
import com.example.chittycollectionapp.data.repository.ChittyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MemberWithPendingAmount(
    val member: Member,
    val pendingAmount: Long,
    val isDefaulter: Boolean
)

class DetailsViewModel(
    private val repository: ChittyRepository,
    private val chittyId: String
) : ViewModel() {

    private val _chittyGroup = MutableStateFlow<ChittyGroup?>(null)
    val chittyGroup: StateFlow<ChittyGroup?> = _chittyGroup

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _membersWithPendingAmount = MutableStateFlow<List<MemberWithPendingAmount>>(emptyList())

    val filteredMembers: StateFlow<List<MemberWithPendingAmount>> =
        combine(searchQuery, _membersWithPendingAmount) { query, members ->
            if (query.isEmpty()) {
                members
            } else {
                members.filter { it.member.memberName.contains(query, ignoreCase = true) }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            repository.getChittyGroupWithMembers(chittyId)?.let { group ->
                _chittyGroup.value = group
                val today = java.util.Calendar.getInstance()
                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val mostRecentDueDate = group.members
                    .map { dateFormat.parse(it.dueDate) }
                    .filter { !it.after(today.time) }
                    .maxOrNull()

                val membersWithPending = group.members.map { member ->
                    val pendingCollections = repository.getPendingCollectionsForMember(member.memberId)
                    val pendingAmount = pendingCollections.sumOf { it.collectionAmount }
                    val isDefaulter = if (mostRecentDueDate != null) {
                        !repository.getCollectionsForMemberSync(member.memberId).any {
                            val collectionDate = dateFormat.parse(it.timestamp.substring(0, 10))
                            collectionDate == mostRecentDueDate && it.paymentStatus == "Paid"
                        }
                    } else {
                        false
                    }
                    MemberWithPendingAmount(member, pendingAmount, isDefaulter)
                }
                _membersWithPendingAmount.value = membersWithPending
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
