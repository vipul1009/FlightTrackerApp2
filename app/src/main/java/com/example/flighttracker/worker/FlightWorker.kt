package com.example.flighttracker.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class FlightWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Implement periodic flight data updates if needed
        return Result.success()
    }
}