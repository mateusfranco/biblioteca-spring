package br.mateus.authserver.user

import br.mateus.authserver.user.requests.CreateUserRequest
import br.mateus.authserver.user.responses.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(val service: UserService) {
    @GetMapping
    fun list(
        @RequestParam sortDir: String? = null,
        @RequestParam role: String? = null
    ): ResponseEntity<*> = if (role != null) {
        val users = service.findByRole(role)
        ResponseEntity.ok(users.map { UserResponse(it) })
    } else {
        val dir = SortDir.find(sortDir ?: "ASC")
        val users = service.findAll(dir)
        ResponseEntity.ok(users.map { UserResponse(it) })
    }

    @PostMapping
    fun insert(
        @Valid @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val user = service.insert(request.toUser())
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse(user))
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long
    ): ResponseEntity<UserResponse> {
        val user = service.findById(id)
        return ResponseEntity.ok(UserResponse(user))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id}/roles/{role}")
    fun grant(
        @PathVariable id: Long,
        @PathVariable role: String
    ): ResponseEntity<Void> {
        service.addRole(id, role)
        return ResponseEntity.ok().build()
    }
}
