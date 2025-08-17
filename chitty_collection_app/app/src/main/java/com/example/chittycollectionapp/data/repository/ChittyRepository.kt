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
}
