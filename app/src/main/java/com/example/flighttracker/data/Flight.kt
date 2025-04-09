package com.example.flighttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flights")
data class Flight(
    @PrimaryKey
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: String,
    val arrivalTime: String,
    val delayMinutes: Int,
    val durationMinutes: Int
)