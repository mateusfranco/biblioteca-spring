package br.mateus.authserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class EmprestimoNaoEncontradoException(id: Long) : IllegalArgumentException("Empréstimo não encontrado com ID: $id")
