package id.neotica.neotica.domain.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val expirationTime: Long
)
