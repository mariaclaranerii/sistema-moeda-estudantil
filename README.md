# Sistema de Moeda Estudantil

Projeto de Software · Engenharia de Software · PUC Minas · Laboratório 3 (2026/2)

Aplicação MVC para reconhecimento do mérito acadêmico. Professores recebem uma carga semestral de moedas, podem distribuí-las aos alunos e os alunos as trocam por vantagens de empresas parceiras.

## Funcionalidades

- Cadastro de alunos e empresas parceiras exclusivamente por professores autenticados; edição e desativação lógica do próprio cadastro.
- Autenticação por login/senha e acesso individual aos dados.
- Cadastro prévio de instituições e professores.
- Crédito semestral acumulativo de 1.000 moedas por professor.
- Transferência com motivo, conferência de saldo e extratos.
- Vantagens com custo, descrição e foto.
- Resgate com código exclusivo e notificações ao aluno e à empresa.
- Persistência H2, ORM JPA e testes de integração.

## Tecnologias

Java 17, Spring Boot 3.5.7, Spring MVC, Spring Security, Thymeleaf, Spring Data JPA, Hibernate, H2, Spring Mail, Maven e PlantUML.

## Executar

Requer JDK 17 e Maven 3.9+.

```bash
mvn spring-boot:run
```

Acesse **http://localhost:8080**. Para iniciar com dados e contas de demonstração:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

| Perfil | Login de demonstração | Senha |
|---|---|---|
| Aluno | `ana.silva` | `12345678` |
| Professor | `prof.carlos` | `12345678` |
| Empresa parceira | `livraria.academica` | `12345678` |

**Use o perfil `demo` somente localmente.** Os dados ficam em `data/`, fora do versionamento Git. Para testar: `mvn test`.

Notificações ficam registradas na base. O projeto está configurado para o SMTP transacional da Brevo, com envio desativado até a configuração local das variáveis `EMAIL_ENABLED=true`, `EMAIL_FROM`, `SMTP_USER` e `SMTP_PASSWORD`. Use uma **chave SMTP** da Brevo (não uma chave API), sem adicioná-la ao repositório. Veja o [passo a passo da integração](docs/sprint-03/README.md#envio-de-e-mails-transacionais--brevo-smtp).

## Documentação e modelagem

- [Sprint 01 — Casos de uso, histórias e diagramas](docs/sprint-01/README.md)
- [Sprint 02 — Modelo ER, persistência e CRUDs iniciais](docs/sprint-02/README.md)
- [Sprint 03 — Arquitetura, implementação final e demonstração](docs/sprint-03/README.md)
- [Requisitos](docs/requisitos.md)
- [Rastreabilidade](docs/rastreabilidade.md)
- [Premissas de modelagem](docs/premissas-de-modelagem.md)

Os diagramas antigos permanecem preservados. As fontes atualizadas do diagrama de classes, componentes e Modelo ER estão em `docs/sprint-03/diagramas/v2/`.

## Integrantes

- Bárbara Marcella Inácio da Silva
- Lucas Gabriel de Oliveira Franco
- Maria Clara Neri Stankunas
- Yago Garzon Chaves
