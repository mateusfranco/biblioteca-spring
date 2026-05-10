package br.mateus.authserver

import br.mateus.authserver.roles.Role
import br.mateus.authserver.roles.RoleRepository
import br.mateus.authserver.user.User
import br.mateus.authserver.user.UserRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class Bootstrapper(
    val rolesRepository: RoleRepository,
    val userRepository: UserRepository
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole =
            rolesRepository.findByName("ADMIN") ?: rolesRepository
                .save(Role(name = "ADMIN", description = "System Administrator"))
        rolesRepository.findByName("USER") ?: rolesRepository
            .save(Role(name = "USER", description = "Regular user"))

        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                email = "admin@gmail.com",
                password = "admin",
                name = "adminTeste",
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
        }
    }
}
