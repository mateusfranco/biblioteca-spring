package br.mateus.authserver.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository
) {
    fun insert(user: User): User {
        return repository.insert(user)
    }
}
