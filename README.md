video -> https://www.youtube.com/watch?v=_iZ6lU8XLFA


# Biblioteca Spring

Um sistema de gestão de biblioteca desenvolvido com Spring Boot e Kotlin. A aplicação oferece funcionalidades completas para administrar livros, controlar empréstimos e gerenciar usuários através de uma API REST.

## O que tem aqui

### Autenticação

A aplicação usa JWT para autenticação. Cada usuário tem um role (ADMIN ou USER) que define o que pode fazer. As operações sensíveis como criar ou deletar livros exigem autenticação e privilégios de administrador. Tudo é integrado com Spring Security para manter a segurança em operações críticas.

### Livros

A gestão de livros é bem simples. Você consegue listar todos os livros, buscar um específico por ID, ou filtrar por autor. Cada livro tem um título, autor, ISBN e um status que indica se está disponível para empréstimo.

Os endpoints públicos para livros são:

- GET /livros - retorna todos os livros
- GET /livros/{id} - retorna um livro específico
- GET /livros/disponivel - retorna apenas os que podem ser emprestados

Você consegue filtrar e ordenar também:

- GET /livros?autor=Machado de Assis
- GET /livros?sort=titulo

Criar, atualizar e deletar livros só é possível com autenticação e sendo admin. O sistema valida para garantir que não existem títulos duplicados e que cada ISBN é único. Quando você tenta deletar um livro que ainda tem empréstimos ativos, o sistema impede a operação.

Cada livro tem um ID que é auto-incrementado, título e autor como campos obrigatórios, um ISBN único no sistema e um booleano que marca se está disponível ou não (por padrão começa como true).


### Sistema de Empréstimos

É o coração da aplicação. Um aluno autenticado consegue emprestar um livro fazendo uma requisição POST para /emprestimos com o ID do livro. Se o livro estiver disponível, o empréstimo é criado e o livro é marcado como indisponível.

Para emprestar um livro você faz:

```
POST /emprestimos
{
  "livroId": 1
}
```

E recebe de volta os dados do empréstimo com a data que pegou o livro. Quando quiser devolver, faz:

```
POST /emprestimos/{id}/devolver
```

O sistema atualiza a data de devolução e, se não houver mais empréstimos ativos daquele livro, marca como disponível novamente. Apenas o aluno que pegou emprestado ou um admin conseguem devolver.


### Usuários

A aplicação gerencia usuários que fazem login e recebem um token JWT. Cada usuário tem um email, nome e um role que define o que pode fazer no sistema. Os alunos conseguem emprestar e devolver livros, enquanto os admins têm acesso total para gerenciar livros.

## Como está organizado

O código está organizado em pacotes por domínio. Tem um pacote para livro, outro para empréstimo e outro para usuário. Em cada pacote tem a entidade JPA que representa a tabela no banco, o controller com os endpoints HTTP, o service com a lógica de negócio, o repository para acesso ao banco e as classes de request/response.

O projeto usa MVC para separar as responsabilidades. O controller recebe as requisições HTTP, o service cuida da lógica e validações, e o repository acessa o banco de dados. Os DTOs (Data Transfer Objects) garantem que os dados que entram e saem estão validados.

As operações críticas como emprestar e devolver livros usam @Transactional para garantir que tudo é salvo de forma consistente. Tudo o que acontece é registrado em logs para você conseguir rastrear o que o sistema fez.

## Como rodar

Você vai precisar de Java 21 ou superior, Kotlin 1.9 e Gradle 8. Se não tiver instalado, pode usar o wrapper que já está no projeto.

Para rodar a aplicação:

```
./gradlew build
./gradlew bootRun
```

Isso vai compilar tudo e iniciar o servidor na porta padrão. A aplicação usa um banco H2 em memória por padrão, mas você consegue mudar isso na configuração.

Se quiser usar outro banco como PostgreSQL ou MySQL, edita o arquivo application.yaml que fica em src/main/resources e muda as configurações de datasource.

## Testes

Tem testes unitários e de integração para verificar se tudo está funcionando. Para rodar todos:

```
./gradlew test
```

Os testes cobrem os controllers (endpoints), os services (lógica de negócio) e as validações principais. Você consegue ver os resultados no build/reports/tests/test/index.html

