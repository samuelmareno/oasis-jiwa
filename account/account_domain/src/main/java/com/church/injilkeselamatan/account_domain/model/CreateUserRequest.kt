package com.church.injilkeselamatan.account_domain.model


data class CreateUserRequest(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val updatedAt: Long? = null,
    val ipAddress: String,
    val lastLogin: Long = System.currentTimeMillis(),
    val model: String,
    val profile: String
) {
    fun toUpdateUserRequest(): UpdateUserRequest {
        return UpdateUserRequest(
            email = email,
            name = name,
            phoneNumber = phoneNumber,
            ipAddress = ipAddress,
            model = model,
            profile = profile,
        )
    }
}
