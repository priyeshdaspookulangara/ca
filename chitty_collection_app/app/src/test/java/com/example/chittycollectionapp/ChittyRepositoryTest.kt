package com.example.chittycollectionapp

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.chittycollectionapp.data.local.AgentDao
import com.example.chittycollectionapp.data.local.AppDatabase
import com.example.chittycollectionapp.data.local.ChittyDao
import com.example.chittycollectionapp.data.local.CollectionDao
import com.example.chittycollectionapp.data.model.AgentDetails
import com.example.chittycollectionapp.data.model.ChittyGroup
import com.example.chittycollectionapp.data.model.InitialData
import com.example.chittycollectionapp.data.model.Member
import com.example.chittycollectionapp.data.repository.ChittyRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChittyRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var agentDao: AgentDao
    private lateinit var chittyDao: ChittyDao
    private lateinit var collectionDao: CollectionDao
    private lateinit var repository: ChittyRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        agentDao = database.agentDao()
        chittyDao = database.chittyDao()
        collectionDao = database.collectionDao()
        repository = ChittyRepository(agentDao, chittyDao, collectionDao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `insert and get chitty groups`() = runBlocking {
        val initialData = InitialData(
            agentDetails = AgentDetails("A01", "Agent", "123"),
            chittyGroups = listOf(
                ChittyGroup("C01", "Chitty 1", 1000, 100, 10,
                    members = listOf(Member("M01", "C01", "Member 1", "456", "2025-01-01"))
                )
            )
        )
        repository.insertInitialData(initialData)

        val chittyGroups = repository.getAllChittyGroups().first()
        assertEquals(1, chittyGroups.size)
        assertEquals("Chitty 1", chittyGroups[0].chittyName)
    }
}
