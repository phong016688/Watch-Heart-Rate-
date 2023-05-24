package com.example.heartratesample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.HrAccuracy
import androidx.health.services.client.data.PassiveMonitoringUpdate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class PassiveDataReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: PassiveDataRepository

    override fun onReceive(context: Context, intent: Intent) {
        val state = PassiveMonitoringUpdate.fromIntent(intent) ?: return
        val latestDataPoint = state.dataPoints
            .filter { it.dataType == DataType.HEART_RATE_BPM }
            .filter {
                it.accuracy == null ||
                        setOf(
                            HrAccuracy.SensorStatus.ACCURACY_MEDIUM,
                            HrAccuracy.SensorStatus.ACCURACY_HIGH
                        ).contains((it.accuracy as HrAccuracy).sensorStatus)
            }
            .filter {
                it.value.asDouble() > 0
            }
            .maxByOrNull { it.endDurationFromBoot }
            ?: return

        val latestHeartRate = latestDataPoint.value.asDouble() // HEART_RATE_BPM is a Float type.
        Log.d(TAG, "Received latest heart rate in background: $latestHeartRate")

        runBlocking {
            repository.storeLatestHeartRate(latestHeartRate)
        }
    }
}