package com.example.chittycollectionapp.data.repository

import com.example.chittycollectionapp.data.local.AgentDao
import com.example.chittycollectionapp.data.local.ChittyDao
import com.example.chittycollectionapp.data.local.CollectionDao
import com.example.chittycollectionapp.data.model.InitialData

class ChittyRepository(
    private val agentDao: AgentDao,
    private val chittyDao: ChittyDao,
    private val collectionDao: CollectionDao
) {
    suspend fun insertInitialData(initialData: InitialData) {
        agentDao.insertAgentDetails(initialData.agentDetails)
        chittyDao.clearChittyGroups()
        chittyDao.clearMembers()
        val chittyGroups = initialData.chittyGroups
        val allMembers = mutableListOf<com.example.chittycollectionapp.data.model.Member>()
        chittyGroups.forEach { chittyGroup ->
            chittyGroup.members.forEach { member ->
                member.chittyId = chittyGroup.chittyId
                allMembers.add(member)
            }
        }
        chittyDao.insertChittyGroups(chittyGroups)
        chittyDao.insertMembers(allMembers)
    }

    fun getAllChittyGroups() = chittyDao.getAllChittyGroups()

    fun searchChittyGroups(query: String) = chittyDao.searchChittyGroups(query)

    suspend fun getChittyGroupWithMembers(chittyId: String) = chittyDao.getChittyGroupWithMembers(chittyId)

    suspend fun insertCollection(collection: com.example.chittycollectionapp.data.model.Collection) {
        collectionDao.insertCollection(collection)
    }

    suspend fun getCollectionsByDateRange(startDate: String, endDate: String) =
        collectionDao.getCollectionsByDateRange(startDate, endDate)

    suspend fun getAgentDetails() = agentDao.getAgentDetails()
}
