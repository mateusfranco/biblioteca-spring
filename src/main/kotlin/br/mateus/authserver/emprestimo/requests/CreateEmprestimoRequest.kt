package br.mateus.authserver.emprestimo.requests

import jakarta.validation.constraints.NotNull

data class CreateEmprestimoRequest(
    @NotNull(message = "ID do livro não pode ser nulo")
    val livroId: Long
)
