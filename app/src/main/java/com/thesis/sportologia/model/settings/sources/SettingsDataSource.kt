package com.thesis.sportologia.model.settings.sources

import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {

    /**
     * Save auth token
     */
    fun setToken(token: String?)

    /**
     * Get the current auth token
     */
    fun getToken(): String?

    /**
     * Listen for the current auth token
     */
    fun listenToken(): Flow<String?>

}