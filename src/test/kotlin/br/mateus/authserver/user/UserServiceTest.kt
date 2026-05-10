package br.mateus.authserver.user

import br.mateus.authserver.roles.RoleRepository
import br.mateus.authserver.security.Jwt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var repository: UserRepository

    @Mock
    private lateinit var roleRepository: RoleRepository

    @Mock
    private lateinit var jwt: Jwt

    @InjectMocks
    private lateinit var service: UserService

    // ==================== INSERT ====================

    @Test
    fun `insert should return user when email is unique`() {
        val user = User(email = "teste@email.com", password = "123", name = "Teste")
        given(repository.findByEmail("teste@email.com")).willReturn(null)
        given(repository.save(user)).willAnswer { it.getArgument<User>(0).also { u -> u.id = 1L; u } }

        val created = service.insert(user)

        assertNotNull(created)
        assertEquals(1L, created!!.id)
        assertEquals("teste@email.com", created!!.email)
        assertEquals("Teste", created!!.name)
    }

    @Test
    fun `insert should return null when email already exists`() {
        val existing = User(email = "dup@email.com", password = "123", name = "Dup")
        given(repository.findByEmail("dup@email.com")).willReturn(existing)

        val user = User(email = "dup@email.com", password = "456", name = "Dup2")
        
        assertThrows<br.mateus.authserver.exceptions.BadRequestException> {
            service.insert(user)
        }
    }

    // ==================== FIND ALL ====================

    @Test
    fun `findAll should return empty list when no users`() {
        given(repository.findAll(org.springframework.data.domain.Sort.by("name").ascending())).willReturn(emptyList())

        val result = service.findAll(SortDir.ASC)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findAll DESC should return users sorted descending`() {
        given(repository.findAll(org.springframework.data.domain.Sort.by("name").descending())).willReturn(emptyList())

        val result = service.findAll(SortDir.DESC)
        assertNotNull(result)
    }

    // ==================== FIND BY ID ====================

    @Test
    fun `findByIdOrNull should return user when exists`() {
        val user = User(email = "x@email.com", password = "1", name = "X")
        user.id = 42L
        given(repository.findById(42L)).willReturn(Optional.of(user))

        val found = service.findByIdOrNull(42L)
        assertNotNull(found)
        assertEquals("x@email.com", found!!.email)
        assertEquals("X", found.name)
    }

    @Test
    fun `findByIdOrNull should return null when user not found`() {
        given(repository.findById(999L)).willReturn(Optional.empty())

        val found = service.findByIdOrNull(999L)
        assertNull(found)
    }

    // ==================== DELETE ====================

    @Test
    fun `delete should return true when user exists`() {
        val user = User(email = "del@email.com", password = "1", name = "Del")
        user.id = 10L
        given(repository.findById(10L)).willReturn(Optional.of(user))

        service.delete(10L)
        
        // delete() retorna Unit, verifica apenas se não lança exceção
        verify(repository).delete(user)
    }

    @Test
    fun `delete should return false when user not found`() {
        given(repository.findById(999L)).willReturn(Optional.empty())

        assertThrows<br.mateus.authserver.exceptions.NotFoundException> {
            service.delete(999L)
        }
    }

    // ==================== ADD ROLE ====================

    @Test
    fun `addRole should return null when user not found`() {
        given(repository.findById(999L)).willReturn(Optional.empty())

        assertThrows<br.mateus.authserver.exceptions.NotFoundException> {
            service.addRole(999L, "ADMIN")
        }
    }

    @Test
    fun `addRole should return null when role not found`() {
        val user = User(email = "a@email.com", password = "1", name = "A")
        user.id = 1L
        given(repository.findById(1L)).willReturn(Optional.of(user))
        given(roleRepository.findByName("UNKNOWN")).willReturn(null)

        assertThrows<br.mateus.authserver.exceptions.BadRequestException> {
            service.addRole(1L, "UNKNOWN")
        }
    }

    @Test
    fun `addRole should return false when user already has role`() {
        val role = br.mateus.authserver.roles.Role(name = "ADMIN", description = "Admin")
        role.id = 1L
        val user = User(email = "a@email.com", password = "1", name = "A")
        user.id = 1L
        user.roles.add(role)

        given(repository.findById(1L)).willReturn(Optional.of(user))

        val result = service.addRole(1L, "ADMIN")
        assertNotNull(result)
        assertFalse(result!!)
    }
}
