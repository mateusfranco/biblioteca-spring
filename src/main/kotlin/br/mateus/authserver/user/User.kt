package br.mateus.authserver.user

import jakarta.persistence.*

@Entity
@Table(name = "UserTable")
class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var name: String = "",
)
