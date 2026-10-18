# Lab03S02 - Persistência e CRUDs iniciais

## Objetivo da sprint

Iniciar a implementação do Sistema de Moeda Estudantil com persistência relacional e disponibilizar os CRUDs de aluno e empresa parceira previstos em RF18 e RF19.

## Escopo implementado

- aplicação Java 17 com Spring Boot, Spring MVC e Thymeleaf;
- persistência com Spring Data JPA, Hibernate e banco relacional H2;
- herança JPA entre `Usuario`, `Aluno` e `EmpresaParceira`, coerente com o modelo da Sprint 01;
- cadastro, consulta, atualização e desativação/reativação lógica de alunos;
- seleção da instituição de ensino do aluno a partir de cadastros prévios;
- cadastro, consulta, atualização e desativação/reativação lógica de empresas parceiras;
- validação de campos obrigatórios e restrições de unicidade;
- senhas armazenadas somente como hash BCrypt;
- testes de integração dos serviços e dos principais endpoints MVC;
- interface web responsiva para demonstração dos fluxos.

## Decisão de persistência

A justificativa completa está em [`decisao-persistencia.md`](decisao-persistencia.md). Em resumo, H2 foi selecionado para esta entrega por ser relacional, embarcado e simples de executar no ambiente acadêmico. A aplicação acessa o banco exclusivamente pela API JPA, o que reduz o acoplamento e permite migrar para PostgreSQL em uma versão posterior.

## Como executar

Pré-requisitos: Java 17 e Maven 3.9 ou superior.

```bash
mvn spring-boot:run
```

Acesse `http://localhost:8080`. Os dados ficam no diretório local `data/`, ignorado pelo Git.

Para executar os testes:

```bash
mvn test
```

## Limites desta sprint

Autenticação, professores, movimentações de moeda, vantagens, resgates e notificações permanecem no backlog. Os campos empresariais não exigidos pelo enunciado (por exemplo, CNPJ e endereço) também não foram acrescentados.
