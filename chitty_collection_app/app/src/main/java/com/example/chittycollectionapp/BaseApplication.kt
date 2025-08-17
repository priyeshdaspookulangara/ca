package com.example.chittycollectionapp

import android.app.Application
import com.example.chittycollectionapp.data.local.AppDatabase
import com.example.chittycollectionapp.data.repository.ChittyRepository

class BaseApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ChittyRepository(database.agentDao(), database.chittyDao(), database.collectionDao(), database.agentCredentialDao()) }
}
