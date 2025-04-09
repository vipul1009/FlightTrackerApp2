package com.example.flighttracker.network

import retrofit2.http.GET
import retrofit2.http.Query

data class ApiResponse(
    val data: List<FlightData>? = listOf(),
    val error: ApiError? = null
)

data class ApiError(
    val code: String? = null,
    val message: String? = null
)

data class FlightData(
    val flight: FlightInfo? = null,
    val departure: AirportInfo? = null,
    val arrival: AirportInfo? = null,
    val airline: AirlineInfo? = null,
    val status: String? = null
)

data class FlightInfo(
    val number: String? = null,
    val iata: String? = null,
    val icao: String? = null
)

data class AirportInfo(
    val airport: String? = null,
    val iata: String? = null,
    val icao: String? = null,
    val terminal: String? = null,
    val gate: String? = null,
    val scheduled: String? = null,
    val estimated: String? = null,
    val actual: String? = null,
    val delay: Int? = null
)

data class AirlineInfo(
    val name: String? = null,
    val iata: String? = null,
    val icao: String? = null
)

interface ApiService {
    @GET("flights")
    suspend fun getFlights(
        @Query("access_key") access_key: String,
        @Query("limit") limit: Int = 100,
        @Query("dep_iata") dep_iata: String? = null,
        @Query("arr_iata") arr_iata: String? = null
    ): ApiResponse
}