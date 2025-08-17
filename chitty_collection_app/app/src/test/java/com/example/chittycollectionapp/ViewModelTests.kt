package com.example.chittycollectionapp

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.chittycollectionapp.data.local.AgentCredentialDao
import com.example.chittycollectionapp.data.local.AgentDao
import com.example.chittycollectionapp.data.local.AppDatabase
import com.example.chittycollectionapp.data.local.ChittyDao
import com.example.chittycollectionapp.data.local.CollectionDao
import com.example.chittycollectionapp.data.model.AgentAuth
import com.example.chittycollectionapp.data.repository.ChittyRepository
import com.example.chittycollectionapp.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ViewModelTests {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var database: AppDatabase
    private lateinit var agentDao: AgentDao
    private lateinit var chittyDao: ChittyDao
    private lateinit var collectionDao: CollectionDao
    private lateinit var agentCredentialDao: AgentCredentialDao
    private lateinit var repository: ChittyRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        agentDao = database.agentDao()
        chittyDao = database.chittyDao()
        collectionDao = database.collectionDao()
        agentCredentialDao = database.agentCredentialDao()
        repository = ChittyRepository(agentDao, chittyDao, collectionDao, agentCredentialDao)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        database.close()
    }

    @Test
    fun `login with valid credentials`() = runBlocking {
        val agentAuth = AgentAuth("A01", "Agent", "agent", "password")
        repository.insertAgentCredentials(listOf(agentAuth))

        val viewModel = LoginViewModel(repository)
        viewModel.login("agent", "password")

        assert(viewModel.loginState.value is com.example.chittycollectionapp.ui.viewmodel.LoginState.Success)
    }

    @Test
    fun `insert and get dividend`() = runBlocking {
        val dividend = com.example.chittycollectionapp.data.model.Dividend(chittyId = "C01", dividendAmount = 500, termDate = "2025-08")
        repository.insertDividend(dividend)

        val latestDividend = repository.getLatestDividendForChitty("C01")
        assert(latestDividend?.dividendAmount == 500L)
    }

    @Test
    fun `conditional dividend logic`() = runBlocking {
        // This is a simplified test and does not cover all edge cases.
        // A more thorough test would mock the repository and test the ViewModel in isolation.
        val chittyGroup = com.example.chittycollectionapp.data.model.ChittyGroup(
            "C01", "Test Chitty", 100000, 1000, 2,
            listOf(
                com.example.chittycollectionapp.data.model.Member("M01", "C01", "Alice", "111", "2025-08-15"),
                com.example.chittycollectionapp.data.model.Member("M02", "C01", "Bob", "222", "2025-08-15")
            )
        )
        val initialData = com.example.chittycollectionapp.data.model.InitialData(
            agentDetails = com.example.chittycollectionapp.data.model.AgentDetails("A01", "Agent", "123"),
            chittyGroups = listOf(chittyGroup)
        )
        repository.insertInitialData(initialData)

        // Make Bob a defaulter by adding a pending collection for the previous month
        val pendingCollection = com.example.chittycollectionapp.data.model.Collection("T01", "C01", "M02", 1000, "Pending", "Cash", "2025-07-15T10:00:00Z", "")
        collectionDao.insertCollection(pendingCollection)

        // Add a dividend where defaulters are ineligible
        val dividend = com.example.chittycollectionapp.data.model.Dividend(chittyId = "C01", dividendAmount = 100, termDate = "2025-08", defaultersIneligible = true)
        repository.insertDividend(dividend)

        val viewModel = com.example.chittycollectionapp.ui.viewmodel.DetailsViewModel(repository, "C01")
        testDispatcher.scheduler.advanceUntilIdle() // Allow the init block to run

        val members = viewModel.filteredMembers.value
        val alice = members.find { it.member.memberId == "M01" }
        val bob = members.find { it.member.memberId == "M02" }

        assert(alice?.isDefaulter == false)
        assert(bob?.isDefaulter == true)
    }
}
