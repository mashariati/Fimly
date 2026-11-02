package com.fimly.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.fimly.app.data.local.preferences.DataStorePreferencesDataSource
import com.fimly.app.data.local.preferences.PreferencesDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
private const val USER_PREFERENCES_NAME = "fimly_user_prefs"

val preferencesModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile(USER_PREFERENCES_NAME) }
        )
    }

    // Local DataSources
    single<PreferencesDataSource> { DataStorePreferencesDataSource(get()) }

}