package br.mateus.authserver.emprestimo

import br.mateus.authserver.livro.Livro
import br.mateus.authserver.user.User
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "Emprestimo")
class Emprestimo(
    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    var aluno: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "livro_id")
    var livro: Livro,

    @Column(nullable = false)
    var dataEmprestimo: LocalDate = LocalDate.now(),

    @Column(nullable = true)
    var dataDevolucao: LocalDate? = null
) {
    @Transient
    fun ativo(): Boolean = dataDevolucao == null
}
