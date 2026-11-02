package com.fimly.app.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class DataStorePreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) : PreferencesDataSource {

    private companion object {
        val KEY_IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    }

    override val isOnboarded: Flow<Boolean> = dataStore.data
        .catch { exception ->
            // Gracefully handle corruption or IO errors when reading preferences
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_IS_ONBOARDED] ?: false
        }

    override suspend fun saveOnboardingState(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_IS_ONBOARDED] = isCompleted
        }
    }
}