package com.example.chittycollectionapp.data.repository

import com.example.chittycollectionapp.data.local.AgentDao
import com.example.chittycollectionapp.data.local.ChittyDao
import com.example.chittycollectionapp.data.local.CollectionDao
import com.example.chittycollectionapp.data.model.InitialData

import com.example.chittycollectionapp.data.model.AgentAuth
import com.example.chittycollectionapp.data.model.AgentCredential
import java.security.MessageDigest

import com.example.chittycollectionapp.data.local.DividendDao
import com.example.chittycollectionapp.data.model.AgentAuth
import com.example.chittycollectionapp.data.model.AgentCredential
import com.example.chittycollectionapp.data.model.Dividend
import java.security.MessageDigest

class ChittyRepository(
    private val agentDao: AgentDao,
    private val chittyDao: ChittyDao,
    private val collectionDao: CollectionDao,
    private val agentCredentialDao: com.example.chittycollectionapp.data.local.AgentCredentialDao,
    private val dividendDao: DividendDao
) {
    suspend fun insertInitialData(initialData: InitialData) {
        agentDao.insertAgentDetails(initialData.agentDetails)
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

    suspend fun insertAgentCredentials(agentAuths: List<AgentAuth>) {
        val agentCredentials = agentAuths.map { auth ->
            val passwordHash = hashPassword(auth.password)
            AgentCredential(auth.agentId, auth.name, auth.loginName, passwordHash)
        }
        agentCredentialDao.insertAgentCredentials(agentCredentials)
    }

    suspend fun getAgentByLoginName(loginName: String) = agentCredentialDao.getAgentByLoginName(loginName)

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun getCollectionsForMember(memberId: String) = collectionDao.getCollectionsForMember(memberId)

    fun getAllCollections() = collectionDao.getAllCollections()

    suspend fun getPendingCollectionsForMember(memberId: String) = collectionDao.getPendingCollectionsForMember(memberId)

    suspend fun insertDividend(dividend: Dividend) = dividendDao.insertDividend(dividend)

    suspend fun getLatestDividendForChitty(chittyId: String) = dividendDao.getLatestDividendForChitty(chittyId)
}
