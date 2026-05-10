package br.mateus.authserver.emprestimo

import br.mateus.authserver.emprestimo.requests.CreateEmprestimoRequest
import br.mateus.authserver.emprestimo.responses.EmprestimoResponse
import br.mateus.authserver.security.UserToken
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/emprestimos")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "jwt-auth")
class EmprestimoController(
    val service: EmprestimoService,
    val userService: br.mateus.authserver.user.UserService
) {

    @PostMapping
    fun emprestar(
        @Valid @RequestBody request: CreateEmprestimoRequest,
        auth: Authentication
    ): ResponseEntity<EmprestimoResponse> {
        val token = auth.principal as UserToken
        val aluno = userService.findById(token.id)
        
        val emprestimo = service.emprestar(aluno, request.livroId)
        return ResponseEntity.status(HttpStatus.CREATED).body(EmprestimoResponse(emprestimo))
    }

    @PostMapping("/{id}/devolver")
    fun devolver(
        @PathVariable id: Long,
        auth: Authentication
    ): ResponseEntity<EmprestimoResponse> {
        val token = auth.principal as UserToken
        val emprestimo = service.findById(id)

        // Validar que é o próprio aluno ou admin
        if (token.id != emprestimo.aluno.id && !token.isAdmin) {
            throw br.mateus.authserver.exceptions.ForbiddenException("Você não pode devolver empréstimos de outros alunos")
        }

        val devolvido = service.devolver(id)
        return ResponseEntity.ok(EmprestimoResponse(devolvido))
    }

    @GetMapping("/meus")
    fun meus(auth: Authentication): ResponseEntity<List<EmprestimoResponse>> {
        val token = auth.principal as UserToken
        val aluno = userService.findById(token.id)
        
        val emprestimos = service.findMeus(aluno)
        return ResponseEntity.ok(emprestimos.map { EmprestimoResponse(it) })
    }

    @GetMapping("/meus/ativos")
    fun meusAtivos(auth: Authentication): ResponseEntity<List<EmprestimoResponse>> {
        val token = auth.principal as UserToken
        val aluno = userService.findById(token.id)
        
        val emprestimos = service.findAtivos(aluno)
        return ResponseEntity.ok(emprestimos.map { EmprestimoResponse(it) })
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun listAll(): ResponseEntity<List<EmprestimoResponse>> {
        val emprestimos = service.findAll()
        return ResponseEntity.ok(emprestimos.map { EmprestimoResponse(it) })
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        auth: Authentication
    ): ResponseEntity<EmprestimoResponse> {
        val token = auth.principal as UserToken
        val emprestimo = service.findById(id)
        
        // Validar que é o próprio aluno ou admin
        if (token.id != emprestimo.aluno.id && !token.isAdmin) {
            throw br.mateus.authserver.exceptions.ForbiddenException("Você não pode visualizar empréstimos de outros alunos")
        }
        
        return ResponseEntity.ok(EmprestimoResponse(emprestimo))
    }
}
