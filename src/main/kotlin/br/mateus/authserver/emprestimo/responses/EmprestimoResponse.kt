package br.mateus.authserver.emprestimo.responses

import br.mateus.authserver.emprestimo.Emprestimo
import br.mateus.authserver.livro.responses.LivroResponse
import br.mateus.authserver.user.responses.UserResponse
import java.time.LocalDate

data class EmprestimoResponse(
    val id: Long?,
    val aluno: UserResponse,
    val livro: LivroResponse,
    val dataEmprestimo: LocalDate,
    val dataDevolucao: LocalDate?
) {
    constructor(emprestimo: Emprestimo) : this(
        id = emprestimo.id,
        aluno = UserResponse(emprestimo.aluno),
        livro = LivroResponse(emprestimo.livro),
        dataEmprestimo = emprestimo.dataEmprestimo,
        dataDevolucao = emprestimo.dataDevolucao
    )
}
