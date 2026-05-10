package br.mateus.authserver.livro

import br.mateus.authserver.livro.requests.CreateLivroRequest
import br.mateus.authserver.livro.requests.UpdateLivroRequest
import br.mateus.authserver.livro.responses.LivroResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@DisplayName("LivroController - Testes Unitários")
class LivroControllerTest {

    @Mock
    private lateinit var livroService: LivroService

    @InjectMocks
    private lateinit var livroController: LivroController

    private lateinit var mockMvc: MockMvc

    private lateinit var livro1: Livro
    private lateinit var livroResponse1: LivroResponse

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(livroController).build()

        livro1 = Livro(
            id = 1L,
            titulo = "Clean Code",
            autor = "Robert C. Martin",
            isbn = "978-0132350884",
            disponivel = true
        )

        livroResponse1 = LivroResponse(livro1)
    }

    // ==================== GET ENDPOINTS ====================

    @Test
    @DisplayName("GET /livros - Deve retornar lista de livros sem autenticação")
    fun testGetLivrosWithoutAuth() {
        val livro2 = Livro(2L, "Design Patterns", "Gang of Four", "222", false)
        whenever(livroService.findComFiltros("", "titulo"))
            .thenReturn(listOf(livro1, livro2))

        val resultado = livroController.list("", "titulo")

        assertEquals(2, resultado.body?.size)
        assertEquals("Clean Code", resultado.body?.get(0)?.titulo)
    }

    @Test
    @DisplayName("GET /livros?autor=Martin - Deve filtrar por autor")
    fun testGetLivrosFilterByAutor() {
        whenever(livroService.findComFiltros("Martin", "titulo"))
            .thenReturn(listOf(livro1))

        val resultado = livroController.list("Martin", "titulo")

        assertEquals(1, resultado.body?.size)
        assertEquals("Robert C. Martin", resultado.body?.get(0)?.autor)
    }

    @Test
    @DisplayName("GET /livros?sort=autor-desc - Deve ordenar por autor descendente")
    fun testGetLivrosSortByAutorDesc() {
        val livro2 = Livro(2L, "Design Patterns", "Zoe Author", "222", true)
        whenever(livroService.findComFiltros("", "autor-desc"))
            .thenReturn(listOf(livro2, livro1))

        val resultado = livroController.list("", "autor-desc")

        assertEquals(2, resultado.body?.size)
    }

    @Test
    @DisplayName("GET /livros/{id} - Deve retornar livro por ID")
    fun testGetLivroById() {
        whenever(livroService.findById(1L)).thenReturn(livro1)

        val resultado = livroController.getById(1L)

        assertEquals(1L, resultado.body?.id)
        assertEquals("Clean Code", resultado.body?.titulo)
    }

    @Test
    @DisplayName("GET /livros/disponivel - Deve retornar apenas livros disponíveis")
    fun testGetLivrosDisponivel() {
        whenever(livroService.findByDisponivel()).thenReturn(listOf(livro1))

        val resultado = livroController.getDisponiveis()

        assertEquals(1, resultado.body?.size)
        assertEquals(true, resultado.body?.get(0)?.disponivel)
    }

    // ==================== CREATE ENDPOINT ====================

    @Test
    @DisplayName("POST /livros - Deve criar livro com dados válidos")
    fun testCreateLivro() {
        val request = CreateLivroRequest("Clean Code", "Robert C. Martin", "978-0132350884")
        whenever(livroService.insert(any())).thenReturn(livro1)

        val resultado = livroController.insert(request)

        assertEquals(1L, resultado.body?.id)
        assertEquals("Clean Code", resultado.body?.titulo)
    }

    // ==================== UPDATE ENDPOINT ====================

    @Test
    @DisplayName("PUT /livros/{id} - Deve atualizar livro com dados válidos")
    fun testUpdateLivro() {
        val request = UpdateLivroRequest("Clean Code Atualizado", "Robert C. Martin Jr.")
        val livroAtualizado = Livro(1L, "Clean Code Atualizado", "Robert C. Martin Jr.", "978-0132350884", true)
        whenever(livroService.update(1L, request)).thenReturn(livroAtualizado)

        val resultado = livroController.update(1L, request)

        assertEquals("Clean Code Atualizado", resultado.body?.titulo)
        assertEquals("978-0132350884", resultado.body?.isbn) // ISBN preserve
    }

    // ==================== DELETE ENDPOINT ====================

    @Test
    @DisplayName("DELETE /livros/{id} - Deve deletar livro sem empréstimos")
    fun testDeleteLivro() {
        doNothing().whenever(livroService).delete(1L)

        livroController.delete(1L)

        // Teste bem-sucedido se não lançar exceção
    }
}
