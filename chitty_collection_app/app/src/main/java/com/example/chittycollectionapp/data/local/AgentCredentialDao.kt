package com.example.chittycollectionapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chittycollectionapp.data.model.AgentCredential

@Dao
interface AgentCredentialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgentCredentials(agentCredentials: List<AgentCredential>)

    @Query("SELECT * FROM agent_credentials WHERE loginName = :loginName")
    suspend fun getAgentByLoginName(loginName: String): AgentCredential?
}
