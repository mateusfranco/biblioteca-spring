package br.mateus.authserver.livro.requests

import br.mateus.authserver.livro.Livro
import jakarta.validation.constraints.NotBlank

data class CreateLivroRequest(
    @NotBlank(message = "Título não pode ser vazio")
    val titulo: String,

    @NotBlank(message = "Autor não pode ser vazio")
    val autor: String,

    @NotBlank(message = "ISBN não pode ser vazio")
    val isbn: String
) {
    fun toLivro() = Livro(
        titulo = this.titulo,
        autor = this.autor,
        isbn = this.isbn,
        disponivel = true
    )
}
