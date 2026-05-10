package br.mateus.authserver.livro

import br.mateus.authserver.exceptions.BadRequestException
import br.mateus.authserver.exceptions.LivroNaoEncontradoException
import br.mateus.authserver.livro.requests.UpdateLivroRequest
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LivroService(val repository: LivroRepository) {
    private val logger = LoggerFactory.getLogger(LivroService::class.java)

    fun insert(livro: Livro): Livro {
        if (repository.findByTitulo(livro.titulo).isNotEmpty()) {
            logger.warn("Tentativa de criar livro com título duplicado: ${livro.titulo}")
            throw BadRequestException("Já existe um livro com este título")
        }
        if (repository.findByIsbn(livro.isbn) != null) {
            logger.warn("Tentativa de criar livro com ISBN duplicado: ${livro.isbn}")
            throw BadRequestException("ISBN já cadastrado")
        }
        logger.info("Criando livro: ${livro.titulo} por ${livro.autor}")
        return repository.save(livro)
    }

    fun findAll(): List<Livro> {
        logger.debug("Listando todos os livros")
        return repository.findAll()
    }

    fun findById(id: Long): Livro {
        logger.info("Buscando livro com ID: $id")
        return repository.findByIdOrNull(id) ?: throw LivroNaoEncontradoException(id)
    }

    fun update(id: Long, request: UpdateLivroRequest): Livro {
        val livroExistente = findById(id)
        livroExistente.titulo = request.titulo
        livroExistente.autor = request.autor
        logger.info("Atualizando livro ID: $id para ${request.titulo}")
        return repository.save(livroExistente)
    }

    @Transactional
    fun delete(id: Long) {
        val livro = findById(id)
        if (livro.emprestimos.any { it.dataDevolucao == null }) {
            logger.warn("Tentativa de deletar livro $id que está emprestado")
            throw BadRequestException("Não é possível deletar um livro que está emprestado")
        }
        logger.info("Deletando livro ID: $id - ${livro.titulo}")
        repository.delete(livro)
    }

    fun findComFiltros(autor: String? = null, sort: String? = null): List<Livro> {
        logger.info("Buscando livros com filtros: autor=$autor, sort=$sort")
        
        val resultado = if (autor != null) {
            repository.findByAutorContainingIgnoreCase(autor)
        } else {
            repository.findAll()
        }

        return when (sort?.lowercase()) {
            "titulo" -> resultado.sortedBy { it.titulo }
            "autor" -> resultado.sortedBy { it.autor }
            "titulo-desc" -> resultado.sortedByDescending { it.titulo }
            "autor-desc" -> resultado.sortedByDescending { it.autor }
            else -> resultado
        }
    }

    fun findByDisponivel(): List<Livro> {
        logger.debug("Buscando livros disponíveis")
        return repository.findByDisponivel(true)
    }

    fun marcarIndisponivel(id: Long): Livro {
        val livro = findById(id)
        livro.disponivel = false
        logger.info("Marcando livro ID: $id como indisponível")
        return repository.save(livro)
    }

    fun marcarDisponivel(id: Long): Livro {
        val livro = findById(id)
        livro.disponivel = true
        logger.info("Marcando livro ID: $id como disponível")
        return repository.save(livro)
    }
}
