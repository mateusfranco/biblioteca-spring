package br.mateus.authserver.user.requests

import jakarta.validation.constraints.NotBlank

data class UpdateUserRequest(
    @NotBlank
    val name: String?
)
