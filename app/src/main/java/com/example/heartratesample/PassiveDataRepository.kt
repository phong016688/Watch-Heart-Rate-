package com.example.heartratesample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class PassiveDataRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val passiveDataEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PASSIVE_DATA_ENABLED] ?: false
    }

    suspend fun setPassiveDataEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PASSIVE_DATA_ENABLED] = enabled
        }
    }

    val lastestHeartRate: Flow<Double> = dataStore.data.map { prefs ->
        prefs[LATEST_HEART_RATE] ?: 0.0
    }

    suspend fun storeLatestHeartRate(heartRate: Double) {
        dataStore.edit { prefs ->
            prefs[LATEST_HEART_RATE] = heartRate
        }
    }

    suspend fun syncDataFirebase(heartRate: Double): Result<Unit> {
        var idWatch = dataStore.data.firstOrNull()?.get(ID_WATCH)
        if (idWatch == null) {
            idWatch = Random.nextInt(1000, 9999)
            dataStore.edit { prefs -> prefs[ID_WATCH] = idWatch }
        }
        val data = mapOf(
            "heart_rate" to heartRate,
            "last_update" to ServerValue.TIMESTAMP
        )
        return runCatching {
            Firebase.database.reference
                .child("wear_data")
                .child(idWatch.toString())
                .run {
                    val key = push().key ?: UUID.randomUUID().toString()
                    child(key).setValue(data)
                }.await()
        }
    }

    companion object {
        const val PREFERENCES_FILENAME = "passive_data_prefs"
        private val PASSIVE_DATA_ENABLED = booleanPreferencesKey("passive_data_enabled")
        private val LATEST_HEART_RATE = doublePreferencesKey("latest_heart_rate")
        private val ID_WATCH = intPreferencesKey("id_watch")
    }
}