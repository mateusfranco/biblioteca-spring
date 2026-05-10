package br.mateus.authserver.user

import br.mateus.authserver.exceptions.BadRequestException

enum class SortDir {
    ASC, DESC;
    companion object {
        fun find(sortDir: String): SortDir {
            return entries.find { it.name == sortDir.uppercase() }
                ?: throw BadRequestException("Invalid sort direction: $sortDir")
        }

        fun findOrNull(sortDir: String) = entries.find { it.name == sortDir.uppercase() }
    }
}
