package br.mateus.authserver.user

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class UserServiceTest {

    private lateinit var repository: UserRepository
    private lateinit var service: UserService

    @BeforeEach
    fun setUp() {
        repository = UserRepository()
        service = UserService(repository)
    }

    // ==================== INSERT ====================

    @Test
    fun `insert should return user with id assigned`() {
        val user = User(email = "teste@email.com", password = "123", name = "Teste")
        val created = service.insert(user)

        assertNotNull(created.id)
        assertEquals("teste@email.com", created.email)
        assertEquals("Teste", created.name)
    }

    @Test
    fun `insert should assign auto-increment ids`() {
        val u1 = service.insert(User(email = "a@email.com", password = "1", name = "A"))
        val u2 = service.insert(User(email = "b@email.com", password = "2", name = "B"))

        assertEquals(1L, u1.id)
        assertEquals(2L, u2.id)
    }

    // ==================== FIND ALL ====================

    @Test
    fun `findAll should return empty list when no users`() {
        val result = service.findAll(it)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findAll should return all users sorted by name`() {
        service.insert(User(email = "c@email.com", password = "1", name = "Carlos"))
        service.insert(User(email = "a@email.com", password = "2", name = "Ana"))
        service.insert(User(email = "b@email.com", password = "3", name = "Beatriz"))

        val result = service.findAll(it)
        assertEquals(3, result.size)
        assertEquals("Ana", result[0].name)
        assertEquals("Beatriz", result[1].name)
        assertEquals("Carlos", result[2].name)
    }

    // ==================== FIND BY ID ====================

    @Test
    fun `findByIdOrNull should return user when exists`() {
        val created = service.insert(User(email = "x@email.com", password = "1", name = "X"))

        val found = service.findByIdOrNull(created.id!!)
        assertNotNull(found)
        assertEquals("x@email.com", found?.email)
        assertEquals("X", found?.name)
    }

    @Test
    fun `findByIdOrNull should return null when user not found`() {
        val found = service.findByIdOrNull(999L)
        assertNull(found)
    }

    // ==================== INTEGRATION STYLE ====================

    @Test
    fun `full insert-and-retrieve cycle should work end to end`() {
        // Insert
        val created = service.insert(User(email = "crud@email.com", password = "secret", name = "CRUD"))
        assertNotNull(created.id)

        // Find by id
        val found = service.findByIdOrNull(created.id!!)
        assertNotNull(found)
        assertEquals("crud@email.com", found?.email)

        // Find all
        val all = service.findAll(it)
        assertEquals(1, all.size)
        assertEquals("CRUD", all[0].name)
    }

    @Test
    fun `save should update existing user when id is set`() {
        val created = service.insert(User(email = "old@email.com", password = "123", name = "Old"))
        val id = created.id!!

        // Re-save with new data
        created.email = "new@email.com"
        created.name = "New"
        val updated = repository.save(created)

        assertEquals("new@email.com", updated.email)
        assertEquals("New", updated.name)
        assertEquals(id, updated.id) // id nao muda
        assertEquals(1, service.findAll(it).size) // so tem 1 usuario
    }
}
