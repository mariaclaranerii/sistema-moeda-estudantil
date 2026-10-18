# Decisão de arquitetura: persistência da Sprint 02

## Contexto

O sistema precisa persistir cadastros e, nas próximas sprints, transações monetárias que exigem consistência, relacionamentos e histórico. A equipe também precisa conseguir executar o projeto localmente com pouca configuração.

## Decisão

Adotar:

- **H2 em modo arquivo** como SGBD relacional da Sprint 02;
- **Spring Data JPA** como abstração de repositórios;
- **Hibernate** como implementação ORM;
- estratégia de herança **JOINED** para armazenar os atributos comuns de `Usuario` e os atributos próprios de `Aluno` e `EmpresaParceira`;
- exclusão lógica por meio do atributo `ativo`, preservando referências históricas;
- controle otimista de concorrência por meio do atributo de versão das entidades de usuário.

## Justificativa

H2 não exige instalação ou serviço externo, mantém os dados entre execuções quando usado em modo arquivo e suporta as restrições relacionais necessárias ao laboratório. JPA mantém as entidades independentes do dialeto específico do banco e oferece um caminho direto para PostgreSQL caso a implantação futura exija múltiplos usuários concorrentes ou infraestrutura externa.

## Consequências

- A demonstração local exige somente Java e Maven.
- Login, CPF, RG e e-mails aplicáveis possuem restrições únicas também no esquema físico.
- Senhas não são persistidas em texto puro; apenas hashes BCrypt são armazenados.
- Alterar para PostgreSQL exigirá trocar o driver e a URL de conexão, além de criar migrações formais do esquema.
- O `ddl-auto: update` é apropriado ao protótipo acadêmico, mas deverá ser substituído por Flyway antes de um uso de produção.
