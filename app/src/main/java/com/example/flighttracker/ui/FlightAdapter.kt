package com.example.flighttracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttracker.data.Flight
import com.example.flighttracker.databinding.FlightItemBinding

class FlightAdapter : ListAdapter<Flight, FlightAdapter.FlightViewHolder>(FlightDiffCallback()) {

    class FlightViewHolder(private val binding: FlightItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(flight: Flight) {
            binding.flightNumber.text = flight.flightNumber
            binding.departureAirport.text = flight.departureAirport
            binding.arrivalAirport.text = flight.arrivalAirport
            binding.departureTime.text = flight.departureTime
            binding.arrivalTime.text = flight.arrivalTime
            binding.durationLabel.text = "${flight.durationMinutes} min"

            // Show delay information if there is a delay
            if (flight.delayMinutes > 0) {
                binding.delay.visibility = View.VISIBLE
                binding.delay.text = "${flight.delayMinutes} min"
            } else {
                binding.delay.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val binding = FlightItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FlightDiffCallback : DiffUtil.ItemCallback<Flight>() {
        override fun areItemsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem.flightNumber == newItem.flightNumber &&
                    oldItem.departureTime == newItem.departureTime &&
                    oldItem.arrivalTime == newItem.arrivalTime
        }

        override fun areContentsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem == newItem
        }
    }
}