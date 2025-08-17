package com.example.chittycollectionapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chittycollectionapp.data.model.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: Collection)

    @Query("SELECT * FROM collections WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getCollectionsByDateRangeFlow(startDate: String, endDate: String): Flow<List<Collection>>

    @Query("SELECT * FROM collections WHERE timestamp BETWEEN :startDate AND :endDate")
    suspend fun getCollectionsByDateRange(startDate: String, endDate: String): List<Collection>

    @Query("SELECT * FROM collections WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getCollectionsForMember(memberId: String): Flow<List<Collection>>

    @Query("SELECT * FROM collections")
    fun getAllCollections(): Flow<List<Collection>>

    @Query("SELECT * FROM collections WHERE memberId = :memberId AND paymentStatus = 'Pending'")
    suspend fun getPendingCollectionsForMember(memberId: String): List<Collection>
}
