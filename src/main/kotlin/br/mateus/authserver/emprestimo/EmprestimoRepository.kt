package br.mateus.authserver.emprestimo

import br.mateus.authserver.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface EmprestimoRepository : JpaRepository<Emprestimo, Long> {
    fun findByAluno(aluno: User): List<Emprestimo>
    fun findByAlunoAndDataDevolucaoIsNull(aluno: User): List<Emprestimo>
    fun findByDataDevolucaoIsNull(): List<Emprestimo>
}
