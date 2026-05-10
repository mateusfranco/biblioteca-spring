package br.mateus.authserver.roles.responses

import br.mateus.authserver.roles.Role

data class RoleResponse(
    val name: String,
    val description: String,
) {
    constructor(role: Role) : this(role.name, role.description)
}
