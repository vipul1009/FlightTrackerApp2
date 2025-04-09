package com.example.flighttracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Flight::class], version = 1, exportSchema = false)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile
        private var INSTANCE: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightDatabase::class.java,
                    "flight_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}