package br.mateus.authserver.emprestimo

import br.mateus.authserver.emprestimo.requests.CreateEmprestimoRequest
import br.mateus.authserver.livro.Livro
import br.mateus.authserver.livro.LivroRepository
import br.mateus.authserver.livro.LivroService
import br.mateus.authserver.user.User
import br.mateus.authserver.user.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
@DisplayName("EmprestimoService - Testes Unitários")
class EmprestimoServiceTest {

    @Mock
    private lateinit var emprestimoRepository: EmprestimoRepository

    @Mock
    private lateinit var livroService: LivroService

    @InjectMocks
    private lateinit var emprestimoService: EmprestimoService

    private lateinit var aluno: User
    private lateinit var livroDisponivel: Livro
    private lateinit var livroIndisponivel: Livro
    private lateinit var emprestimo1: Emprestimo

    @BeforeEach
    fun setup() {

        aluno = User(
            id = 1L,
            email = "joao@example.com",
            password = "hashed_password",
            name = "João da Silva"
        )

        livroDisponivel = Livro(
            id = 1L,
            titulo = "Clean Code",
            autor = "Robert C. Martin",
            isbn = "978-0132350884",
            disponivel = true
        )

        livroIndisponivel = Livro(
            id = 2L,
            titulo = "Design Patterns",
            autor = "Gang of Four",
            isbn = "978-0201633610",
            disponivel = false
        )

        emprestimo1 = Emprestimo(
            id = 1L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = null
        )
    }

    // ==================== EMPRESTAR TESTS ====================

    @Test
    @DisplayName("emprestar() - Deve emprestar livro disponível com sucesso")
    fun testEmprestarSuccess() {
        val request = CreateEmprestimoRequest(livroId = 1L)
        whenever(livroService.findById(1L)).thenReturn(livroDisponivel)
        whenever(emprestimoRepository.findByAlunoAndDataDevolucaoIsNull(aluno))
            .thenReturn(listOf())
        whenever(livroService.marcarIndisponivel(1L)).thenReturn(livroIndisponivel)
        whenever(emprestimoRepository.save(any())).thenReturn(emprestimo1)

        val resultado = emprestimoService.emprestar(aluno, 1L)

        assertEquals(1L, resultado.id)
        assertEquals("João da Silva", resultado.aluno.name)
        assertEquals("Clean Code", resultado.livro.titulo)
        assertNull(resultado.dataDevolucao)
        verify(emprestimoRepository).save(any())
    }

    @Test
    @DisplayName("emprestar() - Deve falhar se livro está indisponível")
    fun testEmprestarFailsUnavailableBook() {
        whenever(livroService.findById(2L)).thenReturn(livroIndisponivel)
        assertThrows<Exception> {
            emprestimoService.emprestar(aluno, 2L)
        }
    }

    @Test
    @DisplayName("emprestar() - Deve falhar se aluno já tem este livro emprestado")
    fun testEmprestarFailsDuplicateLoan() {
        val emprestimoExistente = Emprestimo(
            id = 1L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = null
        )
        whenever(livroService.findById(1L)).thenReturn(livroDisponivel)
        whenever(emprestimoRepository.findByAlunoAndDataDevolucaoIsNull(aluno))
            .thenReturn(listOf(emprestimoExistente))

        assertThrows<Exception> {
            emprestimoService.emprestar(aluno, 1L)
        }
    }

    // ==================== DEVOLVER TESTS ====================

    @Test
    @DisplayName("devolver() - Deve devolver empréstimo ativo com sucesso")
    fun testDevolverSuccess() {
        val emprestimoAtivo = Emprestimo(
            id = 1L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = null
        )
        val emprestimoDevolvido = Emprestimo(
            id = 1L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = LocalDate.now()
        )

        whenever(emprestimoRepository.findById(1L)).thenReturn(java.util.Optional.of(emprestimoAtivo))
        whenever(emprestimoRepository.findByDataDevolucaoIsNull())
            .thenReturn(listOf()) // Nenhum outro empréstimo ativo
        whenever(emprestimoRepository.save(any())).thenReturn(emprestimoDevolvido)

        val resultado = emprestimoService.devolver(1L)

        assertNotNull(resultado.dataDevolucao)
        assertEquals(LocalDate.now(), resultado.dataDevolucao)
        verify(emprestimoRepository).save(any())
    }

    @Test
    @DisplayName("devolver() - Deve marcar livro como disponível após devolução")
    fun testDevolverMarksBookAvailable() {
        val emprestimoAtivo = Emprestimo(
            id = 1L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = null
        )
        val emprestimoDevolvido = Emprestimo(
            id = 1L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = LocalDate.now()
        )

        whenever(emprestimoRepository.findById(1L)).thenReturn(java.util.Optional.of(emprestimoAtivo))
        whenever(emprestimoRepository.findByDataDevolucaoIsNull())
            .thenReturn(listOf()) // Sem outros empréstimos ativos
        whenever(emprestimoRepository.save(any())).thenReturn(emprestimoDevolvido)

        emprestimoService.devolver(1L)

        verify(emprestimoRepository).save(any())
    }

    @Test
    @DisplayName("devolver() - Deve falhar se empréstimo não encontrado")
    fun testDevolverNotFound() {
        whenever(emprestimoRepository.findById(999L)).thenReturn(java.util.Optional.empty())

        assertThrows<Exception> {
            emprestimoService.devolver(999L)
        }
    }

    // ==================== FIND TESTS ====================

    @Test
    @DisplayName("findById() - Deve encontrar empréstimo por ID")
    fun testFindByIdSuccess() {
        whenever(emprestimoRepository.findById(1L)).thenReturn(java.util.Optional.of(emprestimo1))

        val resultado = emprestimoService.findById(1L)

        assertEquals(1L, resultado.id)
        assertEquals("Clean Code", resultado.livro.titulo)
    }

    @Test
    @DisplayName("findById() - Deve lançar exceção se não encontrado")
    fun testFindByIdNotFound() {
        whenever(emprestimoRepository.findById(999L)).thenReturn(java.util.Optional.empty())

        assertThrows<Exception> {
            emprestimoService.findById(999L)
        }
    }

    @Test
    @DisplayName("findMeus() - Deve retornar empréstimos do aluno")
    fun testFindMeus() {
        val emprestimo2 = Emprestimo(
            id = 2L,
            aluno = aluno,
            livro = livroIndisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = LocalDate.now()
        )
        whenever(emprestimoRepository.findByAluno(aluno))
            .thenReturn(listOf(emprestimo1, emprestimo2))

        val resultado = emprestimoService.findMeus(aluno)

        assertEquals(2, resultado.size)
        assertEquals(1L, resultado[0].id)
        assertEquals(2L, resultado[1].id)
    }

    @Test
    @DisplayName("findAtivos() - Deve retornar apenas empréstimos ativos do aluno")
    fun testFindAtivos() {
        whenever(emprestimoRepository.findByAlunoAndDataDevolucaoIsNull(aluno))
            .thenReturn(listOf(emprestimo1))

        val resultado = emprestimoService.findAtivos(aluno)

        assertEquals(1, resultado.size)
        assertNull(resultado[0].dataDevolucao)
    }

    @Test
    @DisplayName("findAll() - Deve retornar todos os empréstimos")
    fun testFindAll() {
        val emprestimo2 = Emprestimo(
            id = 2L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = null
        )
        whenever(emprestimoRepository.findAll()).thenReturn(listOf(emprestimo1, emprestimo2))

        val resultado = emprestimoService.findAll()

        assertEquals(2, resultado.size)
    }

    // ==================== BUSINESS LOGIC TESTS ====================

    @Test
    @DisplayName("emprestar() - Deve marcar livro como indisponível")
    fun testEmprestarMarksBookUnavailable() {
        whenever(livroService.findById(1L)).thenReturn(livroDisponivel)
        whenever(emprestimoRepository.findByAlunoAndDataDevolucaoIsNull(aluno))
            .thenReturn(listOf())
        whenever(livroService.marcarIndisponivel(1L)).thenReturn(livroIndisponivel)
        whenever(emprestimoRepository.save(any())).thenReturn(emprestimo1)

        emprestimoService.emprestar(aluno, 1L)

        verify(emprestimoRepository).save(any())
    }

    @Test
    @DisplayName("ativo() - Empréstimo sem devolução deve estar ativo")
    fun testEmprestimoAtivoWithoutReturn() {
        val ativo = emprestimo1.ativo()
        assertEquals(true, ativo)
    }

    @Test
    @DisplayName("ativo() - Empréstimo com devolução deve estar inativo")
    fun testEmprestimoInativoWithReturn() {
        val emprestimoDevolvido = Emprestimo(
            id = 1L,
            aluno = aluno,
            livro = livroDisponivel,
            dataEmprestimo = LocalDate.now(),
            dataDevolucao = LocalDate.now()
        )
        val ativo = emprestimoDevolvido.ativo()
        assertEquals(false, ativo)
    }
}
