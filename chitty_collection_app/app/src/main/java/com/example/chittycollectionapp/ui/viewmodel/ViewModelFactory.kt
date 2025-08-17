package com.example.chittycollectionapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chittycollectionapp.data.repository.ChittyRepository

class ViewModelFactory(
    private val application: Application,
    private val repository: ChittyRepository,
    private val chittyId: String? = null,
    private val memberId: String? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, repository) as T
        }
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(repository, chittyId ?: throw IllegalArgumentException("ChittyId is required for DetailsViewModel")) as T
        }
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            return CollectionViewModel(repository, chittyId ?: throw IllegalArgumentException("ChittyId is required for CollectionViewModel"), memberId ?: throw IllegalArgumentException("MemberId is required for CollectionViewModel")) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
