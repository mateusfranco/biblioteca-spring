package br.mateus.authserver.livro.requests

import jakarta.validation.constraints.NotBlank

data class UpdateLivroRequest(
    @NotBlank(message = "Título não pode ser vazio")
    val titulo: String,

    @NotBlank(message = "Autor não pode ser vazio")
    val autor: String
)
