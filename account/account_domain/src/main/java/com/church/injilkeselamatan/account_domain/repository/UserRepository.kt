package com.church.injilkeselamatan.account_domain.repository

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.church.injilkeselamatan.account_domain.model.*

interface UserRepository {

    companion object {
        val EMAIL = stringPreferencesKey("user_info_email")
        val NAME = stringPreferencesKey("user_info_name")
        val PHONE_NUMBER = stringPreferencesKey("user_info_phone")
        val CREATED_AT = longPreferencesKey("user_info_created_at")
        val UPDATED_AT = longPreferencesKey("user_info_updated_at")
        val IP_ADDRESS = stringPreferencesKey("user_info_ip_address")
        val LAST_LOGIN = longPreferencesKey("user_info_last_login")
        val MODEL = stringPreferencesKey("user_info_model")
        val PROFILE = stringPreferencesKey("user_info_picture")
    }

    suspend fun getUsers(): Result<List<User>>

    suspend fun registerUser(createUserRequest: CreateUserRequest): Result<UserApi>

    suspend fun updateCredentials(updateUserRequest: UpdateUserRequest): Result<UserApi>

    suspend fun getIp(): Result<GetIpAddress>

    suspend fun saveUserInfo(userInfo: UserInfo)

    suspend fun loadUserInfo(): UserInfo?
}