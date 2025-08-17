package com.example.chittycollectionapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chittycollectionapp.data.model.AgentDetails
import com.example.chittycollectionapp.data.model.ChittyGroup
import com.example.chittycollectionapp.data.model.Collection
import com.example.chittycollectionapp.data.model.Converters
import com.example.chittycollectionapp.data.model.Member

@Database(
    entities = [AgentDetails::class, ChittyGroup::class, Member::class, Collection::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun agentDao(): AgentDao
    abstract fun chittyDao(): ChittyDao
    abstract fun collectionDao(): CollectionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chitty_collection_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
