package br.mateus.authserver.livro

import br.mateus.authserver.livro.requests.CreateLivroRequest
import br.mateus.authserver.livro.requests.UpdateLivroRequest
import br.mateus.authserver.livro.responses.LivroResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/livros")
class LivroController(val service: LivroService) {

    @GetMapping
    fun list(
        @RequestParam autor: String? = null,
        @RequestParam sort: String? = null
    ): ResponseEntity<List<LivroResponse>> {
        val livros = service.findComFiltros(autor, sort)
        return ResponseEntity.ok(livros.map { LivroResponse(it) })
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<LivroResponse> {
        val livro = service.findById(id)
        return ResponseEntity.ok(LivroResponse(livro))
    }

    @GetMapping("/disponivel")
    fun getDisponiveis(): ResponseEntity<List<LivroResponse>> {
        val livros = service.findByDisponivel()
        return ResponseEntity.ok(livros.map { LivroResponse(it) })
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    fun insert(
        @Valid @RequestBody request: CreateLivroRequest
    ): ResponseEntity<LivroResponse> {
        val livro = service.insert(request.toLivro())
        return ResponseEntity.status(HttpStatus.CREATED).body(LivroResponse(livro))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateLivroRequest
    ): ResponseEntity<LivroResponse> {
        val updated = service.update(id, request)
        return ResponseEntity.ok(LivroResponse(updated))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.ok().build()
    }
}
