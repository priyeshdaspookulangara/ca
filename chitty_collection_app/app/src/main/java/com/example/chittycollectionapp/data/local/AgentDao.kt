package com.example.chittycollectionapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chittycollectionapp.data.model.AgentDetails

@Dao
interface AgentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgentDetails(agentDetails: AgentDetails)

    @Query("SELECT * FROM agent_details LIMIT 1")
    suspend fun getAgentDetails(): AgentDetails?
}
