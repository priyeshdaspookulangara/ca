package com.example.chittycollectionapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.chittycollectionapp.data.model.ChittyGroup
import com.example.chittycollectionapp.data.model.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface ChittyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChittyGroups(chittyGroups: List<ChittyGroup>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMembers(members: List<Member>)

    @Query("SELECT * FROM chitty_groups")
    fun getAllChittyGroups(): Flow<List<ChittyGroup>>

    @Query("SELECT * FROM chitty_groups WHERE chittyName LIKE '%' || :query || '%'")
    fun searchChittyGroups(query: String): Flow<List<ChittyGroup>>

    @Transaction
    @Query("SELECT * FROM chitty_groups WHERE chittyId = :chittyId")
    suspend fun getChittyGroupWithMembers(chittyId: String): ChittyGroup

    @Query("DELETE FROM chitty_groups")
    suspend fun clearChittyGroups()

    @Query("DELETE FROM members")
    suspend fun clearMembers()
}
