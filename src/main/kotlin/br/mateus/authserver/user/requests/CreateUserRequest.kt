package br.mateus.authserver.user.requests

import br.mateus.authserver.user.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateUserRequest(
    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email cannot be blank")
    val email: String,

    @field:NotBlank(message = "Name cannot be blank")
    val name: String,

    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\\$!%*#?&])[A-Za-z\\d@\\$!%*#?&]{8,}$",
        message = "Password must contain at least 8 characters, including letters, numbers, and special characters"
    )
    @field:NotBlank(message = "Password cannot be blank")
    val password: String
) {
    fun toUser() = User(
        email = this.email,
        name = this.name,
        password = this.password
    )
}
