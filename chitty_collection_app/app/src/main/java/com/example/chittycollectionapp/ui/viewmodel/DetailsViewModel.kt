package com.example.chittycollectionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.model.ChittyGroup
import com.example.chittycollectionapp.data.repository.ChittyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repository: ChittyRepository,
    private val chittyId: String
) : ViewModel() {

    private val _chittyGroup = MutableStateFlow<ChittyGroup?>(null)
    val chittyGroup: StateFlow<ChittyGroup?> = _chittyGroup

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredMembers: StateFlow<List<com.example.chittycollectionapp.data.model.Member>> =
        searchQuery.flatMapLatest { query ->
            _chittyGroup.map { group ->
                if (query.isEmpty()) {
                    group?.members ?: emptyList()
                } else {
                    group?.members?.filter { it.memberName.contains(query, ignoreCase = true) } ?: emptyList()
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadChittyGroupDetails()
    }

    private fun loadChittyGroupDetails() {
        viewModelScope.launch {
            _chittyGroup.value = repository.getChittyGroupWithMembers(chittyId)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
