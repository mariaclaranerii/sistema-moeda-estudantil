# Lab04S03 · Relatório de análise crítica

- **Projeto avaliado** — Sistema de Moeda Estudantil, PUC Minas
- **Base da análise** — `sprint-04`, commit `fe7b106`, do [repositório original][base]
- **Data da avaliação** — 25 de novembro de 2026

A aplicação cobre os fluxos centrais de cadastro, crédito, transferência e resgate e tem testes de integração que exercitam a API e as páginas MVC. As principais divergências envolvem os e-mails obrigatórios, os atores autorizados a cadastrar usuários e trechos da documentação que descrevem comportamentos diferentes dos observados no código. A análise compara o README, os requisitos versionados, os diagramas, o código e a execução local da mesma branch.

## 1. Arquitetura e tecnologias

O projeto é um monólito Java 17 com Spring Boot 3.5.7. O Spring MVC atende tanto páginas Thymeleaf quanto uma API REST. Spring Security controla autenticação por formulário, sessão e CSRF. Spring Data JPA e Hibernate persistem os dados no H2 em arquivo. O build usa Maven, e a suíte usa JUnit e MockMvc com H2 em memória. PlantUML e diagrams.net mantêm fontes editáveis dos modelos. [Dependências](../../pom.xml) · [Configuração local](../../src/main/resources/application.yml) · [Configuração de teste](../../src/test/resources/application-test.yml)

A atualização dos saldos e o lançamento da transferência ou do resgate ocorrem dentro da mesma transação de banco. Os saldos pertencem a `Aluno` e `Professor`, e o versionamento otimista em `Usuario` permite ao JPA detectar atualizações concorrentes. A herança `JOINED` concentra identidade e credenciais em `usuarios`. Os identificadores de participantes em `Transacao` são campos escalares, o que mantém uma representação simples do extrato, mas deixa a consistência desses vínculos a cargo da aplicação. [Movimentação original][movimentacao-base] · [Usuário](../../src/main/java/br/edu/pucminas/moedaestudantil/model/Usuario.java) · [Transação](../../src/main/java/br/edu/pucminas/moedaestudantil/model/Transacao.java)

Na versão avaliada, a camada de aplicação já protege os fluxos monetários e aplica os limites de cada perfil. A separação é parcial no catálogo, pois os controllers MVC e REST consultavam diretamente o repositório e repetiam o critério de visibilidade, enquanto a gestão de vantagens ficava no serviço de movimentação. Como painel e API são duas entradas do mesmo caso de uso, uma alteração em apenas um controller poderia produzir catálogos diferentes. [Painel original][painel-base] · [API de vantagens original][vantagem-api-base]

## 2. Organização do GitHub

A estrutura `src/main`, `src/test` e `docs/sprint-*` facilita localizar a implementação, os testes e os entregáveis de cada sprint. A Sprint 04 registra os pais da integração e mantém um catálogo numerado com fontes editáveis, imagens e matriz de rastreabilidade. O workflow **Testes Java** executa testes, empacota o JAR e verifica a inicialização com dados de demonstração. A execução de `fe7b106` concluiu com sucesso. [Integração da Sprint 04](README.md) · [Catálogo de diagramas](diagramas/v1/README.md) · [Workflow](../../.github/workflows/testes.yml) · [Execução no GitHub Actions][ci]

Na consulta de 25/11/2026, o original tinha três branches (`main`, `sprint-03-final` e `sprint-04`), sem issues ou pull requests públicos listados. As pendências estão no backlog em Markdown. A branch padrão é `main`, mas a entrega mais recente está em `sprint-04`. Em relação à `main` consultada, `sprint-04` tinha 40 commits exclusivos e a `main` tinha 3. Um clone comum abre outra versão do README e da aplicação, o que dificulta reproduzir a entrega certa. A página inicial do repositório poderia apontar diretamente para a branch da entrega, e as pendências de requisitos poderiam ter issues vinculadas à matriz. [Branches][branches] · [Comparação com main][comparacao] · [Issues][issues] · [Pull requests][pulls]

## 3. Configuração seguindo o README

Em uma cópia limpa de `fe7b106`, sem o diretório `data/`, foram executados os dois comandos documentados. O ambiente inicial tinha Java 25 e não tinha `mvn` no PATH. Foi preciso preparar JDK 17 e Maven 3.9.11 fora do repositório, pois o projeto não contém Maven Wrapper. Após esse passo, a execução teve baixa dificuldade e dispensou serviços externos. [README da base][readme-base]

| Verificação | Resultado |
| --- | --- |
| `mvn spring-boot:run` | Porta 8080 disponível, HTTP 200 na página inicial e criação de `data/moeda-estudantil.mv.db`. |
| `mvn spring-boot:run -Dspring-boot.run.profiles=demo` | A aplicação iniciou e as três credenciais de demonstração do README abriram `/painel`. |
| `mvn -B test` | 40 testes passaram na base, sem falhas, erros ou testes ignorados. |

Em banco vazio, o perfil padrão carrega instituições. Os usuários de demonstração, incluindo professores, são carregados pelo perfil `demo`. Para uma primeira avaliação, o README poderia pôr o comando com `demo` em destaque, mostrar como selecionar a branch antes de executar e indicar `java -version` e `mvn -v` para diagnosticar pré-requisitos. [Carga inicial](../../src/main/java/br/edu/pucminas/moedaestudantil/config/DadosIniciaisConfig.java)

## 4. Erros e lacunas nos diagramas

Foram comparados os fontes editáveis, o PDF consolidado do Lab04S01 e os modelos da Sprint 03 com a base avaliada. DS12, DS13 e IG02 estão identificados como previstos no catálogo e representam corretamente os fluxos de e-mail ainda pendentes. [PDF do Lab04S01](diagramas/v1/diagramas-uml-lab04s01.pdf) · [Rastreabilidade do Lab04S01](diagramas/v1/rastreabilidade-diagramas.md)

| Diagrama | Problema e correção sugerida |
| --- | --- |
| [IG01](diagramas/v1/IG01/IG01.drawio), p. 19 do PDF | O losango `Perfil` recebe o fluxo após o primeiro acesso e o retorno de `Continuar? [sim]` como duas entradas de controle comuns. Em UML, fluxos alternativos convergem em um nó de merge antes da decisão. O retorno deveria entrar no merge anterior e o losango `Perfil` receber uma única entrada. A saída de DS14 também leva ao menu sem guarda de sucesso, embora a própria nota exija a troca concluída. Modelar a falha voltando à troca e a saída de sucesso seguindo ao merge. |
| [DS02](diagramas/v1/DS02/DS02.puml), p. 4 | Uma mesma alternativa agrupa duplicidade e instituição inexistente como erro apresentado pelo controller. No código, a duplicidade retorna ao formulário, enquanto a instituição inexistente gera `RegistroNaoEncontradoException` e abre a página `erro` com HTTP 404 via `ErroControllerAdvice`. Mostrar as duas saídas explicitamente. [Controller](../../src/main/java/br/edu/pucminas/moedaestudantil/web/AlunoController.java) · [Tratamento MVC](../../src/main/java/br/edu/pucminas/moedaestudantil/web/ErroControllerAdvice.java) |
| [DS03](diagramas/v1/DS03/DS03.puml) e [DS05](diagramas/v1/DS05/DS05.puml), pp. 5 e 7 | O ramo do titular promete `desativar() ou reativar()`. Para acessar a rota, o próprio titular precisa passar por `SessaoService.usuario()`, que rejeita a conta inativa. O serviço sabe alternar o estado, mas a reativação pelo titular desenhada nesse fluxo está inacessível. Representar a desativação no fluxo atual e apresentar uma eventual reativação como um caso de uso separado. [Sessão](../../src/main/java/br/edu/pucminas/moedaestudantil/service/SessaoService.java) |
| [Componentes v2](../sprint-03/diagramas/v2/componentes.puml) e [classes v2](../sprint-03/diagramas/v2/classes.puml) | Os diagramas representam chamadas ativas para uma fila de e-mail e um `NotificacaoService`. A base mantém entidade e repositório de notificações, mas o serviço e o cliente SMTP estão ausentes e as movimentações não alimentam a fila. Marcar esses componentes e suas dependências como previstos. [Movimentação original][movimentacao-base] · [Repositório de notificações](../../src/main/java/br/edu/pucminas/moedaestudantil/repository/NotificacaoEmailRepository.java) |
| [Modelo ER v2](../sprint-03/diagramas/v2/modelo-er.puml) | `USUARIOS.login` aparece como `VARCHAR(80)`, mas a entidade mapeia 160 caracteres. Faltam também `troca_senha_pendente` em `ALUNOS` e `EMPRESAS_PARCEIRAS` e `ativo` em `VANTAGENS`. Atualizar o ER com os comprimentos e atributos efetivamente mapeados. [Usuário](../../src/main/java/br/edu/pucminas/moedaestudantil/model/Usuario.java) · [Aluno](../../src/main/java/br/edu/pucminas/moedaestudantil/model/Aluno.java) · [Empresa](../../src/main/java/br/edu/pucminas/moedaestudantil/model/EmpresaParceira.java) · [Vantagem](../../src/main/java/br/edu/pucminas/moedaestudantil/model/Vantagem.java) |

Referência de notação para IG01. [OMG UML 2.5.1, seções 15.7.3 e 17][uml]

## 5. Alinhamento entre projeto e implementação

| Requisito ou premissa | Implementação e impacto |
| --- | --- |
| **RF06 · Avisar o aluno ao receber moedas** | O envio registra débito, crédito e extrato, sem disparar o aviso previsto. DS12 modela o fluxo futuro. O aluno precisa abrir o sistema para verificar o recebimento. [Requisitos](../requisitos.md) · [Movimentação original][movimentacao-base] |
| **RF13–RF15 · Código de resgate e envio por e-mail** | RF13 está implementado e o mesmo código aparece nos extratos de aluno e empresa. RF14 e RF15 continuam pendentes, com o envio representado em DS13. A consulta no extrato oferece acesso ao código, enquanto a entrega por e-mail continua em aberto. [DS13](diagramas/v1/DS13/DS13.puml) · [Movimentação original][movimentacao-base] |
| **RF01/RF09, RF18/RF19 e PM01/PM02 · Cadastro e autoatendimento** | O modelo inicial coloca o cadastro na mão do aluno e da empresa e prevê cadastro inicial público. O Spring Security reserva a criação ao professor nas duas interfaces. A consulta e a manutenção do próprio cadastro permanecem restritas ao titular. A Sprint 04 reconhece a divergência e deixa a política de criação para confirmação. [Premissas](../premissas-de-modelagem.md) · [Segurança](../../src/main/java/br/edu/pucminas/moedaestudantil/config/SegurancaConfig.java) · [Backlog](README.md) |
| **RF11/RF12 · Catálogo e disponibilidade para resgate** | O catálogo filtra vantagens pela situação do item. O resgate verifica também se a empresa está ativa. Uma vantagem ativa de empresa desativada pode aparecer para seleção e falhar somente na confirmação. O catálogo poderia comunicar a indisponibilidade antes de permitir a seleção. [Painel original][painel-base] · [Movimentação original][movimentacao-base] |

## 6. Refatorações GRASP

### 6.1 Alta Coesão e Baixo Acoplamento · Gestão do catálogo

**Antes.** O painel aplicava a regra de visibilidade diretamente no repositório. A API fazia a mesma filtragem e delegava o cadastro à classe que também processava saldos e resgates.

```java
// PainelController
model.addAttribute("vantagens", vantagens.findAllByOrderByTituloAsc().stream()
    .filter(Vantagem::isAtivo).toList());

// VantagemApiController
return repository.findAllByOrderByTituloAsc().stream().filter(Vantagem::isAtivo).toList();
```

**Depois.** Os controllers delegam a consulta e a gestão a `VantagemService`. O serviço reúne consulta, validação, cadastro, edição e mudança de situação. O resgate continua coordenado por `MovimentacaoService`.

```java
// VantagemApiController
return vantagens.listarPara(sessao.usuario());

// VantagemService
public List<Vantagem> listarPara(Usuario usuario) {
    if (usuario instanceof EmpresaParceira) {
        return vantagens.findByEmpresaIdOrderByTituloAsc(usuario.getId());
    }
    return vantagens.findAllByOrderByTituloAsc().stream()
        .filter(Vantagem::isAtivo)
        .toList();
}
```

**Justificativa.** A gestão de vantagens tem um ponto de manutenção. As duas interfaces passam a compartilhar a regra que mostra à empresa seus itens ativos e inativos e aos demais perfis o catálogo ativo. Os controllers de vantagens deixam de depender de consultas ao repositório desse catálogo. [Serviço após a refatoração](../../src/main/java/br/edu/pucminas/moedaestudantil/service/VantagemService.java)

### 6.2 Criador · Construção de registros financeiros

**Antes.** Os serviços montavam o tipo, os dados de exibição e os vínculos em etapas. Uma etapa omitida ainda permitia gravar o registro, com risco de o lançamento desaparecer do extrato de um participante.

```java
Transacao t = new Transacao("ENVIO_PROFESSOR", professor.getNome(), aluno.getNome(),
    motivo.trim(), quantidade);
t.vincularTransferencia(professor.getId(), aluno.getId());
t = transacoes.save(t);
return t;
```

**Depois.** `Transacao` fornece um método de fábrica para cada operação. O exemplo mostra a transferência. Resgate e crédito semestral seguem o mesmo padrão, com seus respectivos vínculos.

```java
// MovimentacaoService
return transacoes.save(Transacao.transferencia(professor, aluno, motivo.trim(), quantidade));

// Transacao
public static Transacao transferencia(Professor professor, Aluno aluno, String motivo, int quantidade) {
    Transacao transacao = new Transacao("ENVIO_PROFESSOR", professor.getNome(), aluno.getNome(), motivo, quantidade);
    transacao.professorId = professor.getId();
    transacao.alunoId = aluno.getId();
    return transacao;
}
```

**Justificativa.** A entidade reúne a inicialização dos dados específicos de cada tipo. Os serviços continuam responsáveis por autorização, movimentação de saldo e persistência. O formato dos lançamentos e o mapeamento JPA foram preservados. [Transação após a refatoração](../../src/main/java/br/edu/pucminas/moedaestudantil/model/Transacao.java) · [Crédito semestral](../../src/main/java/br/edu/pucminas/moedaestudantil/service/CreditoSemestralService.java)

### 6.3 Fabricação Pura · Política compartilhada de senhas

**Antes.** `AlunoService` e `EmpresaParceiraService` implementavam os mesmos passos de verificação da senha atual, tamanho da nova senha, confirmação e rejeição de reutilização. Também repetiam a geração da senha temporária.

```java
// Trecho da validação em EmpresaParceiraService, repetida em AlunoService
if (!nova.equals(confirmacao)) {
    throw new IllegalArgumentException("A confirmação da senha não confere.");
}
if (passwordEncoder.matches(nova, empresa.getSenhaHash())) {
    throw new IllegalArgumentException("A nova senha deve ser diferente da atual.");
}
empresa.alterarSenha(passwordEncoder.encode(nova));
empresa.confirmarTrocaSenha();
```

**Depois.** `SenhaService` concentra validação, codificação e geração de senha temporária. Cada serviço de cadastro ainda identifica o titular, aplica o hash e atualiza a pendência de primeiro acesso.

```java
// EmpresaParceiraService
EmpresaParceira empresa = buscar(sessao.empresa().getId());
empresa.alterarSenha(senhas.codificarNovaSenha(empresa.getSenhaHash(), atual, nova, confirmacao));
empresa.confirmarTrocaSenha();

// AlunoService, na redefinição autorizada ao professor
String temporaria = senhas.gerarTemporaria();
aluno.alterarSenha(senhas.codificar(temporaria));
aluno.exigirTrocaSenhaInicial();
```

**Justificativa.** A política criptográfica compartilhada ganhou um serviço próprio, sem acoplar as entidades de usuário ao `PasswordEncoder`. As duas operações preservam a autorização, as mensagens de validação e o comportamento do primeiro acesso. [Serviço de senhas](../../src/main/java/br/edu/pucminas/moedaestudantil/service/SenhaService.java) · [Aluno antes][aluno-base] · [Empresa antes][empresa-base]

## 7. Validação da branch

`mvn -B clean verify` compilou, executou 40 testes com sucesso e empacotou o JAR usando JDK 17 e Maven 3.9.11. O JAR também respondeu em `/login` com o perfil `demo`. O teste de integração da API passou a conferir o vínculo da transferência e do crédito ao extrato do professor, a vantagem associada ao resgate e o mesmo código no extrato da empresa. [Teste de integração da API](../../src/test/java/br/edu/pucminas/moedaestudantil/web/ApiIntegrationTest.java)

[base]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/tree/fe7b106116b04f03fa17b277675f85c23d211d16
[readme-base]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/blob/fe7b106116b04f03fa17b277675f85c23d211d16/README.md
[branches]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/branches
[comparacao]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/compare/main...sprint-04
[issues]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/issues
[pulls]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/pulls
[ci]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/actions/runs/43099066514
[uml]: https://www.omg.org/spec/UML/2.5.1/
[movimentacao-base]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/blob/fe7b106116b04f03fa17b277675f85c23d211d16/src/main/java/br/edu/pucminas/moedaestudantil/service/MovimentacaoService.java
[painel-base]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/blob/fe7b106116b04f03fa17b277675f85c23d211d16/src/main/java/br/edu/pucminas/moedaestudantil/web/PainelController.java
[vantagem-api-base]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/blob/fe7b106116b04f03fa17b277675f85c23d211d16/src/main/java/br/edu/pucminas/moedaestudantil/web/api/VantagemApiController.java
[aluno-base]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/blob/fe7b106116b04f03fa17b277675f85c23d211d16/src/main/java/br/edu/pucminas/moedaestudantil/service/AlunoService.java
[empresa-base]: https://github.com/mariaclaranerii/sistema-moeda-estudantil/blob/fe7b106116b04f03fa17b277675f85c23d211d16/src/main/java/br/edu/pucminas/moedaestudantil/service/EmpresaParceiraService.java
