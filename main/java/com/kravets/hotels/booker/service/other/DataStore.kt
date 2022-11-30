package com.kravets.hotels.booker.service.other

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")
        val SESSION_KEY = stringPreferencesKey("sessionKey")
        val SHORT_NAME = stringPreferencesKey("shortName")
        val IS_ADMIN = booleanPreferencesKey("isAdmin")
    }

    val sessionKey: Flow<String> = context.dataStore.data
        .map {
            it[SESSION_KEY] ?: ""
        }
    var shortName: Flow<String> = context.dataStore.data
        .map {
            it[SHORT_NAME] ?: ""
        }
    var isAdmin: Flow<Boolean> = context.dataStore.data
        .map {
            it[IS_ADMIN] ?: false
        }


    suspend fun saveSessionKey(sessionKey: String?) {
        context.dataStore.edit {
            it[SESSION_KEY] = sessionKey ?: ""
        }
    }

    suspend fun saveShortName(shortName: String?) {
        context.dataStore.edit {
            it[SHORT_NAME] = shortName ?: ""
        }
    }

    suspend fun saveIsAdmin(isAdmin: Boolean?) {
        context.dataStore.edit {
            it[IS_ADMIN] = isAdmin ?: false
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it[SESSION_KEY] = ""
            it[SHORT_NAME] = ""
            it[IS_ADMIN] = false
        }
    }
}