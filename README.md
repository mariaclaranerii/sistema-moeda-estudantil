# Sistema de Moeda Estudantil

Projeto acadêmico da disciplina Projeto de Software, do curso de Engenharia de Software da PUC Minas (Laboratório 3, 2º semestre de 2026).

O sistema reconhece o mérito estudantil por meio de uma moeda virtual. Professores distribuem moedas a alunos, que podem trocá-las por vantagens oferecidas por empresas parceiras.

## Situação atual

Sprint 02 - Lab03S02 implementada. O projeto agora contém a aplicação Spring Boot, persistência relacional e os CRUDs MVC de alunos e empresas parceiras. A modelagem da Sprint 01 permanece preservada em `docs/sprint-01`.

## Organização

```text
docs/
  requisitos.md
  premissas-de-modelagem.md
  rastreabilidade.md
  sprint-01/
    README.md
    historias-de-usuario.md
    diagramas/v1/
      casos-de-uso.puml
      classes.puml
      componentes.puml
      renderizados/
  sprint-02/
    README.md
    decisao-persistencia.md
src/
  main/java/          # domínio, repositórios, serviços e controllers MVC
  main/resources/     # configuração, templates Thymeleaf e estilos
  test/java/          # testes automatizados de integração
```

Os arquivos `.puml` são as fontes editáveis dos diagramas. As imagens SVG e PNG são versões renderizadas para consulta. Novas versões dos modelos deverão ser adicionadas em uma nova pasta de versão, mantendo as anteriores e o histórico Git.

## Tecnologias definidas para orientar a modelagem

- Java 17 LTS: versão estável, amplamente suportada e suficiente para o contexto acadêmico.
- Spring Boot com Spring MVC: implementação direta do padrão MVC exigido e baixo custo de configuração.
- Thymeleaf: views renderizadas no servidor, evitando a complexidade de uma aplicação front-end separada.
- Maven: build e dependências com estrutura convencional para projetos Java.
- Spring Data JPA com Hibernate: estratégia ORM adotada na Sprint 02.
- H2 em modo arquivo: banco relacional da Sprint 02, substituível por outro SGBD compatível com JPA.
- PlantUML: diagramas UML editáveis, versionáveis como texto e renderizáveis em SVG.

## Execução

Com Java 17 e Maven 3.9 ou superior instalados:

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8080`. Execute a suíte automatizada com `mvn test`.

## Integrantes

- Bárbara Marcella Inácio da Silva
- Lucas Gabriel de Oliveira Franco
- Maria Clara Neri Stankunas
- Yago Garzon Chaves
