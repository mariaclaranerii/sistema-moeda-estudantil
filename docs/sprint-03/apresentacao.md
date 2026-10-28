# Roteiro — Sprint 03 (20 minutos)

| Intervalo | Conteúdo | Demonstração |
|---|---|---|
| 0:00–2:00 | Objetivo e perfis do sistema | Página inicial, login |
| 2:00–5:00 | Arquitetura MVC | Diagrama de componentes e pastas |
| 5:00–8:00 | ORM e persistência | Entidades, herança JOINED, repositories, Modelo ER |
| 8:00–11:00 | CRUD final de aluno e empresa | Cadastro, consulta, edição e desativação |
| 11:00–14:00 | Regras da moeda | Saldo do professor, transferência, crédito semestral |
| 14:00–16:30 | Resgate e notificações | Código, desconto e registros de mensagens |
| 16:30–18:30 | Tutorial das tecnologias | Java/Spring Boot, JPA/H2, Thymeleaf, Maven |
| 18:30–20:00 | Testes, decisões e encerramento | Comandos `mvn test` e `mvn spring-boot:run` |

## Tutorial técnico

1. Executar o projeto com Java 17 e Maven 3.9+: `mvn spring-boot:run -Dspring-boot.run.profiles=demo`.
2. Abrir `http://localhost:8080` e entrar com os perfis de demonstração.
3. Identificar o fluxo `Controller → Service → Repository → H2` no código.
4. Explicar como `@Entity`, `@Inheritance(JOINED)`, `@Transactional` e `JpaRepository` definem a camada de persistência.
5. Apresentar a separação dos métodos de transferência, crédito semestral e resgate.
6. Mostrar os testes `mvn test` e os modelos UML versionados no GitHub.

A integração de e-mail depende das variáveis SMTP e fica registrada como pendente sem configuração do servidor.
