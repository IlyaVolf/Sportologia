package com.thesis.sportologia.model.users

import com.thesis.sportologia.model.settings.sources.SettingsDataSource
import javax.inject.Inject

class AuthTokenImpRepository @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
) : AuthTokenRepository {

    override suspend fun setToken(token: String) {
        settingsDataSource.setToken(token)
    }

    override suspend fun getToken(): String? {
        return settingsDataSource.getToken()
    }

    override suspend fun clearToken() {
        settingsDataSource.setToken(null)
    }

}