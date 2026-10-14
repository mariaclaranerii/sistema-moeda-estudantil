# Sistema de Moeda Estudantil

Projeto acadêmico da disciplina Projeto de Software, do curso de Engenharia de Software da PUC Minas (Laboratório 3, 2º semestre de 2026).

O sistema reconhece o mérito estudantil por meio de uma moeda virtual. Professores distribuem moedas a alunos, que podem trocá-las por vantagens oferecidas por empresas parceiras.

## Situação atual

Sprint 01 - Lab03S01 em revisão. Esta versão contém somente os artefatos de modelagem solicitados para a sprint; não há implementação funcional nem artefatos antecipados das sprints seguintes.

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
src/
  main/java/          # reservado para a implementação aprovada em sprint futura
  main/resources/     # reservado para recursos da implementação
  test/java/          # reservado para testes automatizados
```

Os arquivos `.puml` são as fontes editáveis dos diagramas. As imagens SVG e PNG são versões renderizadas para consulta. Novas versões dos modelos deverão ser adicionadas em uma nova pasta de versão, mantendo as anteriores e o histórico Git.

## Tecnologias definidas para orientar a modelagem

- Java 17 LTS: versão estável, amplamente suportada e suficiente para o contexto acadêmico.
- Spring Boot com Spring MVC: implementação direta do padrão MVC exigido e baixo custo de configuração.
- Thymeleaf: views renderizadas no servidor, evitando a complexidade de uma aplicação front-end separada.
- Maven: build e dependências com estrutura convencional para projetos Java.
- Spring Data JPA: candidato para a estratégia de persistência, cuja decisão e detalhamento pertencem à Sprint 02.
- Banco de dados relacional: tecnologia específica será definida somente na Sprint 02.
- PlantUML: diagramas UML editáveis, versionáveis como texto e renderizáveis em SVG.

Nenhuma biblioteca ou framework foi implementado nesta sprint. A definição acima apenas mantém os modelos de classes e componentes compatíveis com uma futura aplicação Java MVC.

## Sprints do enunciado

- Sprint 01 - Lab03S01: casos de uso, histórias de usuário, classes e componentes.
- Sprint 02 - Lab03S02: modelo ER, estratégia de acesso ao banco e versão inicial dos CRUDs de aluno e empresa parceira.
- Sprint 03 - Lab03S03: versão final dos CRUDs, apresentação da arquitetura e persistência, slides, protótipo e tutorial tecnológico.

O avanço para outra sprint depende de aprovação explícita.
