package br.mateus.authserver.livro

import br.mateus.authserver.emprestimo.Emprestimo
import jakarta.persistence.*

@Entity
@Table(name = "Livro")
class Livro(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var titulo: String,

    @Column(nullable = false)
    var autor: String,

    @Column(nullable = false, unique = true)
    var isbn: String,

    @Column(nullable = false)
    var disponivel: Boolean = true,

    @OneToMany(mappedBy = "livro", cascade = [CascadeType.ALL], orphanRemoval = true)
    var emprestimos: MutableList<Emprestimo> = mutableListOf()
)
