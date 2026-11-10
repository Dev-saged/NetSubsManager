package com.abdullah.netsubsmanager.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val TEMPLATE = stringPreferencesKey("sms_template")
}

class Settings(private val context: Context) {
    val templateFlow: Flow<String> = context.dataStore.data.map { pref ->
        pref[SettingsKeys.TEMPLATE] ?: defaultMessageTemplate()
    }

    suspend fun saveTemplate(value: String) {
        context.dataStore.edit { pref ->
            pref[SettingsKeys.TEMPLATE] = value
        }
    }
}
