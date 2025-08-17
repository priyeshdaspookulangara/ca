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
    fun getCollectionsByDateRange(startDate: String, endDate: String): Flow<List<Collection>>
}
