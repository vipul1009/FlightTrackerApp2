package com.example.flighttracker.utils

import android.content.Context
import android.util.Log
import com.example.flighttracker.data.Flight
import com.example.flighttracker.data.FlightDatabase
import com.example.flighttracker.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FlightRepository(context: Context) {
    private val apiService: ApiService
    val flightDao = FlightDatabase.getDatabase(context).flightDao()

    // Your API key - consider using BuildConfig or secure storage for production
    private val apiKey = "0c9f277571065f64aaf89ea6577f43e8"
    private val TAG = "FlightRepository"

    init {
        // Create logging interceptor to see full request/response details
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aviationstack.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun getFlights(departure: String, arrival: String): List<Flight> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Making API request to fetch flights from $departure to $arrival")
                Log.d(TAG, "Using API key: ${apiKey.take(5)}...${apiKey.takeLast(5)}")

                // Make API call with departure and arrival parameters directly
                val response = apiService.getFlights(
                    access_key = apiKey,
                    limit = 10, // Limit to 10 flights
                    dep_iata = departure.uppercase(), // Pass departure IATA
                    arr_iata = arrival.uppercase()    // Pass arrival IATA
                )

                Log.d(TAG, "API response received: ${response.data?.size} flights")

                // No need to filter as API should return matches based on parameters
                val flightData = response.data ?: emptyList()

                if (flightData.isEmpty()) {
                    Log.w(TAG, "No matching flights found, returning cached data")
                    return@withContext flightDao.getTopThreeFlights()
                }

                val flights = flightData.mapNotNull { data ->
                    try {
                        // Check all required fields are present
                        val flightNumber = data.flight?.iata ?: data.flight?.icao ?: data.flight?.number
                        val depIata = data.departure?.iata
                        val arrIata = data.arrival?.iata
                        val depTime = data.departure?.scheduled ?: data.departure?.estimated
                        val arrTime = data.arrival?.scheduled ?: data.arrival?.estimated

                        // Only create Flight object if all required fields are present
                        if (flightNumber != null && depIata != null && arrIata != null &&
                            depTime != null && arrTime != null) {

                            val durationMinutes = calculateDuration(depTime, arrTime)
                            val delay = data.arrival?.delay ?: data.departure?.delay ?: 0

                            Flight(
                                flightNumber = flightNumber,
                                departureAirport = depIata,
                                arrivalAirport = arrIata,
                                departureTime = formatTime(depTime),
                                arrivalTime = formatTime(arrTime),
                                delayMinutes = delay,
                                durationMinutes = durationMinutes
                            )
                        } else {
                            Log.w(TAG, "Skipping flight due to missing required data: flightNumber=$flightNumber, depIata=$depIata, arrIata=$arrIata, depTime=$depTime, arrTime=$arrTime")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error mapping flight data", e)
                        null
                    }
                }.take(3) // Take top 3 flights

                Log.d(TAG, "Mapped ${flights.size} valid flights from API response")

                if (flights.isNotEmpty()) {
                    try {
                        flightDao.insertAll(flights)
                        Log.d(TAG, "Successfully stored ${flights.size} flights in database")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to save flights to database", e)
                    }
                    flights
                } else {
                    Log.w(TAG, "No valid flights found, returning cached data")
                    flightDao.getTopThreeFlights()
                }
            } catch (e: Exception) {
                Log.e(TAG, "API call failed: ${e.message}", e)
                Log.e(TAG, "API call stack trace: ${e.stackTraceToString()}")
                // Return cached data on failure
                flightDao.getTopThreeFlights()
            }
        }
    }

    private fun calculateDuration(start: String, end: String): Int {
        return try {
            // Handle different date-time formats
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            )

            var startDate: Date? = null
            var endDate: Date? = null

            // Try each format until we find one that works
            for (format in formats) {
                try {
                    if (startDate == null) startDate = format.parse(start)
                    if (endDate == null) endDate = format.parse(end)
                    if (startDate != null && endDate != null) break
                } catch (e: Exception) {
                    // Try next format
                }
            }

            if (startDate == null || endDate == null) {
                Log.w(TAG, "Could not parse dates: $start, $end")
                return 0
            }

            val duration = ((endDate.time - startDate.time) / 60000).toInt().coerceAtLeast(0) // Ensure non-negative
            Log.d(TAG, "Calculated flight duration: $duration minutes from $start to $end")
            duration
        } catch (e: Exception) {
            Log.e(TAG, "Duration calculation failed: $start to $end", e)
            0
        }
    }

    private fun formatTime(time: String): String {
        return try {
            // Handle different date-time formats
            val inputFormats = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            )

            val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

            // Try each format until we find one that works
            for (format in inputFormats) {
                try {
                    val date = format.parse(time)
                    if (date != null) {
                        val formatted = outputFormat.format(date)
                        Log.d(TAG, "Formatted time $time to $formatted")
                        return formatted
                    }
                } catch (e: Exception) {
                    // Try next format
                }
            }

            // If all parsing failed, return original
            Log.w(TAG, "Could not format time: $time")
            time
        } catch (e: Exception) {
            Log.e(TAG, "Time formatting failed for: $time", e)
            time // Return original string if parsing fails
        }
    }
}