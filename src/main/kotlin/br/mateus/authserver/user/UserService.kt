package br.mateus.authserver.user

import br.mateus.authserver.exceptions.BadRequestException
import br.mateus.authserver.exceptions.NotFoundException
import br.mateus.authserver.roles.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw BadRequestException("Email already registered")
        }
        logger.info("Creating user with email: ${user.email}")
        return repository.save(user)
    }

    fun findAll(dir: SortDir = SortDir.ASC) = when (dir) {
        SortDir.ASC -> repository.findAll(Sort.by("name").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("name").descending())
    }

    fun findById(id: Long): User {
        logger.info("Finding user by id: $id")
        return repository.findByIdOrNull(id) ?: throw NotFoundException(id)
    }

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun delete(id: Long) {
        val user = findById(id)
        if (user.isAdmin() && repository.findByRole("ADMIN").size == 1) {
            throw BadRequestException("Cannot delete the last admin user")
        }
        logger.info("Deleting user with id: $id")
        repository.delete(user)
    }

    fun findByRole(role: String) = repository.findByRole(role.uppercase())

    fun addRole(id: Long, roleName: String): Boolean {
        val upperRole = roleName.uppercase()
        val user = findById(id)
        if (user.roles.any { it.name == upperRole }) return false

        val role = roleRepository.findByName(upperRole) 
            ?: throw BadRequestException("Role not found: $upperRole")

        user.roles.add(role)
        logger.info("Adding role $upperRole to user with id: $id")
        repository.save(user)
        return true
    }
}
