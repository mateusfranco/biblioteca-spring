package br.mateus.authserver.user.responses

data class LoginResponse(
    val token: String,
    val user: UserResponse
)
