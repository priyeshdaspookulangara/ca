package com.example.chittycollectionapp.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chittycollectionapp.data.model.InitialData
import com.example.chittycollectionapp.data.repository.ChittyRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainViewModel(application: Application, private val repository: ChittyRepository) :
    AndroidViewModel(application) {

    private val _chittyGroups = MutableStateFlow<List<com.example.chittycollectionapp.data.model.ChittyGroup>>(emptyList())
    val chittyGroups: StateFlow<List<com.example.chittycollectionapp.data.model.ChittyGroup>> = _chittyGroups

    init {
        viewModelScope.launch {
            repository.getAllChittyGroups().collect {
                _chittyGroups.value = it
            }
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
}
