package br.mateus.authserver.roles

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/roles")
class RoleController(val service: RoleService) {
    @PostMapping
    fun insert(
        @RequestBody role: Role
    ): ResponseEntity<Role> {
        val savedRole = service.insert(role)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole)
    }

    @GetMapping
    fun list() = service.findAll()
        .let { ResponseEntity.ok(it) }
}
