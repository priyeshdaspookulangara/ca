package com.example.chittycollectionapp.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.model.Dividend
import com.example.chittycollectionapp.data.model.InitialData
import com.example.chittycollectionapp.data.repository.ChittyRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainViewModel(application: Application, private val repository: ChittyRepository) :
    AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val chittyGroups: StateFlow<List<com.example.chittycollectionapp.data.model.ChittyGroup>> =
        searchQuery.flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.getAllChittyGroups()
            } else {
                repository.searchChittyGroups(query)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedChittyDividend = MutableStateFlow<Dividend?>(null)
    val selectedChittyDividend: StateFlow<Dividend?> = _selectedChittyDividend

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun saveDividend(chittyId: String, amount: Long, termDate: String) {
        viewModelScope.launch {
            val dividend = Dividend(chittyId = chittyId, dividendAmount = amount, termDate = termDate)
            repository.insertDividend(dividend)
        }
    }

    fun getLatestDividend(chittyId: String) {
        viewModelScope.launch {
            _selectedChittyDividend.value = repository.getLatestDividendForChitty(chittyId)
        }
    }

    fun loadInitialData(uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val initialData = Gson().fromJson(reader, InitialData::class.java)
                repository.insertInitialData(initialData)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadAgentData(uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val agentAuthList = Gson().fromJson(reader, com.example.chittycollectionapp.data.model.AgentAuthList::class.java)
                repository.insertAgentCredentials(agentAuthList.agents)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun exportCollections(startDate: String, endDate: String) {
        viewModelScope.launch {
            val agentDetails = repository.getAgentDetails()
            val collections = repository.getCollectionsByDateRange(startDate, endDate)
            val totalCollections = collections.sumOf { it.collectionAmount }
            val dailyCollections = com.example.chittycollectionapp.data.model.DailyCollections(
                agentId = agentDetails?.agentId ?: "",
                downloadDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                totalCollections = totalCollections,
                collections = collections
            )
            val json = Gson().toJson(dailyCollections)
            saveJsonToFile(json)
        }
    }

    private fun saveJsonToFile(json: String) {
        val contentResolver = getApplication<Application>().contentResolver
        val values = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "daily_collections_${System.currentTimeMillis()}.json")
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = contentResolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            try {
                contentResolver.openOutputStream(uri).use {
                    it?.write(json.toByteArray())
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
