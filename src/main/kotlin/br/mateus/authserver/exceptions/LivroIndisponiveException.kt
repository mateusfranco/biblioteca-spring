package br.mateus.authserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class LivroIndisponiveException(livroId: Long) : IllegalArgumentException("Livro com ID $livroId não está disponível para empréstimo")
