package br.mateus.authserver.emprestimo

import br.mateus.authserver.exceptions.BadRequestException
import br.mateus.authserver.exceptions.EmprestimoNaoEncontradoException
import br.mateus.authserver.exceptions.LivroIndisponiveException
import br.mateus.authserver.livro.LivroService
import br.mateus.authserver.user.User
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class EmprestimoService(
    val repository: EmprestimoRepository,
    val livroService: LivroService
) {
    private val logger = LoggerFactory.getLogger(EmprestimoService::class.java)

    @Transactional
    fun emprestar(aluno: User, livroId: Long): Emprestimo {
        val livro = livroService.findById(livroId)

        if (!livro.disponivel) {
            logger.warn("Tentativa de emprestar livro indisponível ID: $livroId para aluno ${aluno.id}")
            throw LivroIndisponiveException(livroId)
        }

        if (repository.findByAlunoAndDataDevolucaoIsNull(aluno).any { it.livro.id == livroId }) {
            logger.warn("Aluno ${aluno.id} já tem empréstimo ativo do livro $livroId")
            throw BadRequestException("Você já possui este livro emprestado")
        }

        val emprestimo = Emprestimo(
            aluno = aluno,
            livro = livro,
            dataEmprestimo = LocalDate.now()
        )

        livroService.marcarIndisponivel(livroId)
        logger.info("Aluno ${aluno.id} (${aluno.email}) pegou livro emprestado: ${livro.titulo}")
        return repository.save(emprestimo)
    }

    @Transactional
    fun devolver(emprestimoId: Long): Emprestimo {
        val emprestimo = findById(emprestimoId)

        if (emprestimo.dataDevolucao != null) {
            logger.warn("Tentativa de devolver empréstimo já finalizado ID: $emprestimoId")
            throw BadRequestException("Este empréstimo já foi finalizado")
        }

        emprestimo.dataDevolucao = LocalDate.now()
        
        // Verificar se não há mais empréstimos ativos para este livro
        val emprestimosPendentes = repository.findByDataDevolucaoIsNull()
            .filter { it.livro.id == emprestimo.livro.id }
        
        if (emprestimosPendentes.isEmpty()) {
            livroService.marcarDisponivel(emprestimo.livro.id!!)
        }

        logger.info("Aluno ${emprestimo.aluno.id} devolveu livro: ${emprestimo.livro.titulo}")
        return repository.save(emprestimo)
    }

    fun findById(id: Long): Emprestimo {
        logger.info("Buscando empréstimo ID: $id")
        return repository.findByIdOrNull(id) ?: throw EmprestimoNaoEncontradoException(id)
    }

    fun findMeus(aluno: User): List<Emprestimo> {
        logger.info("Listando empréstimos do aluno ${aluno.id}")
        return repository.findByAluno(aluno)
    }

    fun findAtivos(aluno: User): List<Emprestimo> {
        logger.info("Listando empréstimos ativos do aluno ${aluno.id}")
        return repository.findByAlunoAndDataDevolucaoIsNull(aluno)
    }

    fun findAll(): List<Emprestimo> {
        logger.debug("Listando todos os empréstimos")
        return repository.findAll()
    }
}
