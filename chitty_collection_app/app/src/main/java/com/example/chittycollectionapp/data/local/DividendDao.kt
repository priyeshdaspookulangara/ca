package com.example.chittycollectionapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chittycollectionapp.data.model.Dividend

@Dao
interface DividendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDividend(dividend: Dividend)

    @Query("SELECT * FROM dividends WHERE chittyId = :chittyId ORDER BY termDate DESC LIMIT 1")
    suspend fun getLatestDividendForChitty(chittyId: String): Dividend?
}
