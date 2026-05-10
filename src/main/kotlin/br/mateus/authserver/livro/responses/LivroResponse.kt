package br.mateus.authserver.livro.responses

import br.mateus.authserver.livro.Livro

data class LivroResponse(
    val id: Long?,
    val titulo: String,
    val autor: String,
    val isbn: String,
    val disponivel: Boolean
) {
    constructor(livro: Livro) : this(
        id = livro.id,
        titulo = livro.titulo,
        autor = livro.autor,
        isbn = livro.isbn,
        disponivel = livro.disponivel
    )
}
