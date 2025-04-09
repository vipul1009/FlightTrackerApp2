package com.example.flighttracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flights: List<Flight>)

    @Query("SELECT * FROM flights ORDER BY departureTime DESC LIMIT 3")
    suspend fun getTopThreeFlights(): List<Flight>
}