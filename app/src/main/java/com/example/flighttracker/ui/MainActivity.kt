package com.example.flighttracker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flighttracker.databinding.ActivityMainBinding
import com.example.flighttracker.utils.FlightRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: FlightRepository
    private lateinit var adapter: FlightAdapter
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = FlightRepository(applicationContext)
        adapter = FlightAdapter()

        binding.flightList.setHasFixedSize(true)
        binding.flightList.layoutManager = LinearLayoutManager(this)
        binding.flightList.adapter = adapter

        binding.searchFlightCodeButton.setOnClickListener {
            val flightCode = binding.flightCodeInput.text.toString().trim()
            if (flightCode.isNotEmpty()) {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra("FLIGHT_URL", "https://www.flightaware.com/live/flight/$flightCode")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a flight code", Toast.LENGTH_SHORT).show()
            }
        }

        binding.swapButton.setOnClickListener {
            val dep = binding.departureInput.text.toString()
            val arr = binding.arrivalInput.text.toString()
            binding.departureInput.setText(arr)
            binding.arrivalInput.setText(dep)
        }

        binding.searchAirportsButton.setOnClickListener {
            val dep = binding.departureInput.text.toString().uppercase().trim()
            val arr = binding.arrivalInput.text.toString().uppercase().trim()

            if (dep.length != 3 || arr.length != 3) {
                Toast.makeText(this, "Enter valid 3-letter IATA codes", Toast.LENGTH_SHORT).show()
            } else {
                fetchFlights(dep, arr)
            }
        }

        loadCachedFlights()
    }

    private fun fetchFlights(dep: String, arr: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.searchAirportsButton.isEnabled = false
        binding.averageTimeText.text = "Average flight time: Loading..."

        lifecycleScope.launch {
            try {
                val flights = repository.getFlights(dep, arr)
                binding.progressBar.visibility = View.GONE
                binding.searchAirportsButton.isEnabled = true

                if (flights.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No flights found. Showing cached data.", Toast.LENGTH_SHORT).show()
                    binding.averageTimeText.text = "Average flight time: N/A"
                } else {
                    adapter.submitList(flights)
                    binding.flightList.visibility = View.VISIBLE
                    updateAverageDuration(flights)
                    Toast.makeText(this@MainActivity, "Found ${flights.size} flights", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching flights: ${e.message}")
                binding.progressBar.visibility = View.GONE
                binding.searchAirportsButton.isEnabled = true
                Toast.makeText(this@MainActivity, "Error occurred. Showing cached data.", Toast.LENGTH_LONG).show()
                loadCachedFlights()
            }
        }
    }

    private fun updateAverageDuration(flights: List<com.example.flighttracker.data.Flight>) {
        val avg = flights.map { it.durationMinutes }.average()
        binding.averageTimeText.text = if (avg > 0) "Average flight time: ${"%.1f".format(avg)} minutes" else "Average flight time: N/A"
    }

    private fun loadCachedFlights() {
        lifecycleScope.launch {
            try {
                val cached = repository.flightDao.getTopThreeFlights()
                if (cached.isNotEmpty()) {
                    adapter.submitList(cached)
                    binding.flightList.visibility = View.VISIBLE
                    updateAverageDuration(cached)
                } else {
                    binding.averageTimeText.text = "Average flight time: N/A"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cached data", e)
            }
        }
    }
}
