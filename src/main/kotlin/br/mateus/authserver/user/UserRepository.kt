package br.mateus.authserver.user

import org.springframework.stereotype.Repository

@Repository
class UserRepository {
    private val users = mutableMapOf<Long, User>()
    private var counter = 0L

    fun insert(user: User): User {
        counter++
        val newUser = user.copy(id = counter)
        users[counter] = newUser
        return newUser
    }
}
