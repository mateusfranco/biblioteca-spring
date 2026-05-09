package br.mateus.authserver.user

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(val repository: UserRepository) {
    fun insert(user: User): User? {
        if(repository.findByEmail(user.email) != null) {
            return null
        }
        return repository.save(user)
    }
    fun findAll(dir: SortDir) = repository.findAll()
    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)
}
