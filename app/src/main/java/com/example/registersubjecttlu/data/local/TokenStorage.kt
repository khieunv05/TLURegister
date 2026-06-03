package com.example.registersubjecttlu.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name="user_storage")
@Singleton
class TokenStorage @Inject constructor(
    private val context: Context
){
    companion object{
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private val STUDENT_ID_KEY = intPreferencesKey("student_id")
        private val SEMESTER_ID_KEY = intPreferencesKey("semester_id")
    }
    val tokenFlow : Flow<String?> = context.dataStore.data
        .map {
            preferences ->
            preferences[TOKEN_KEY]
        }
    val studentIdFlow : Flow<Int?> = context.dataStore.data.map {
        preferences -> preferences[STUDENT_ID_KEY]
    }
    val semesterIdFlow: Flow<Int?> = context.dataStore.data.map {
        preferences -> preferences[SEMESTER_ID_KEY]
    }
    suspend fun saveToken(token: String){
        context.dataStore.edit {
            preferences -> preferences[TOKEN_KEY] = token
        }
    }
    suspend fun saveStudentId(studentId: Int){
        context.dataStore.edit {
            preferences -> preferences[STUDENT_ID_KEY] = studentId
        }
    }
    suspend fun saveSemesterId(semesterId: Int){
        context.dataStore.edit {
            preferences -> preferences[SEMESTER_ID_KEY] = semesterId
        }
    }
    suspend fun clearToken(){
        context.dataStore.edit {
            preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(STUDENT_ID_KEY)
            preferences.remove(SEMESTER_ID_KEY)
        }
    }
}