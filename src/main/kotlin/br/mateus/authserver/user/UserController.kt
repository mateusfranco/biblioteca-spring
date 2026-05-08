package br.mateus.authserver.user

import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val service: UserService
) {
    @PostMapping
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    fun insert(@RequestBody user: User): ResponseEntity<User> {
        val created = service.insert(user)
        return created.let {
            ResponseEntity.status(201).body(it)
        }
    }
}
