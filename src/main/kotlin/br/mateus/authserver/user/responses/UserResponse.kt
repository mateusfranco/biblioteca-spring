package br.mateus.authserver.user.responses

import br.mateus.authserver.user.User

data class UserResponse(
    val id: Long?,
    val email: String,
    val name: String
) {
    constructor(user: User) : this(
        id = user.id,
        email = user.email,
        name = user.name
    )
}
