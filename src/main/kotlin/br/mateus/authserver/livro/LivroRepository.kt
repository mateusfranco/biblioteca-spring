package br.mateus.authserver.livro

import org.springframework.data.jpa.repository.JpaRepository

interface LivroRepository : JpaRepository<Livro, Long> {
    fun findByAutor(autor: String): List<Livro>
    fun findByTitulo(titulo: String): List<Livro>
    fun findByIsbn(isbn: String): Livro?
    fun findByDisponivel(disponivel: Boolean): List<Livro>
    fun findByAutorContainingIgnoreCase(autor: String): List<Livro>
    fun findByTituloContainingIgnoreCase(titulo: String): List<Livro>
}
