# Lab04S01 - Modelagem dinâmica UML

Sistema de Moeda Estudantil - PUC Minas, Engenharia de Software.
Disciplina: Projeto de Software. Professora: Milena Menezes Adão.

## Entregável e versão de referência

O enunciado, seção 4.4, solicita diagramas de sequência e diagrama de visão geral de interação nos padrões UML. A avaliação também exige alinhamento entre modelos e implementação. Não há indicação de ferramenta, quantidade de diagramas ou modelo gráfico obrigatório no arquivo fornecido.

Referência auditada: `sprint-04` no commit `63ec82352b3c07cf17b6fcdd289221f275de08fb` (11/11/2026). As versões anteriores dos modelos foram preservadas. Este conjunto acrescenta a modelagem dinâmica; não substitui os diagramas de classes, componentes ou ER.

- [Catálogo completo em PDF - 23 páginas](diagramas-uml-lab04s01.pdf)
- [Matriz de rastreabilidade e limites](rastreabilidade-diagramas.md)
- [Visão geral atual - IG01](IG01/IG01.svg)

## Como ler

Os 16 diagramas de sequência cobrem UC01-UC12 e os fluxos adicionais de senha e logout. IG01 é a visão geral principal; IG03-IG05 a refinam por perfil. IG02 é a visão de complementação dos e-mails, ainda prevista.

**Implementado:** DS01-DS11, DS14-DS16 e IG01/IG03/IG04/IG05 descrevem a versão de referência, com abstrações identificadas nas notas.

**Previsto:** DS12 (RF06), DS13 (RF14/RF15) e IG02 representam requisitos ainda ausentes do código. Seus participantes de notificação são papéis propostos, não classes existentes. A modelagem não significa que o envio de e-mails foi implementado.

## Catálogo

| ID | Interação | Caso de uso | Situação | Visualização | Fonte editável |
|---|---|---|---|---|---|
| DS01 | Autenticar usuário | UC01 | Atual | [SVG](DS01/DS01.svg) · [PNG](DS01/DS01.png) | [puml](DS01/DS01.puml) |
| DS02 | Cadastrar aluno | UC02 - criação | Atual | [SVG](DS02/DS02.svg) · [PNG](DS02/DS02.png) | [puml](DS02/DS02.puml) |
| DS03 | Consultar, editar e desativar aluno | UC02 - manutenção | Atual | [SVG](DS03/DS03.svg) · [PNG](DS03/DS03.png) | [puml](DS03/DS03.puml) |
| DS04 | Cadastrar empresa parceira | UC03 - criação | Atual | [SVG](DS04/DS04.svg) · [PNG](DS04/DS04.png) | [puml](DS04/DS04.puml) |
| DS05 | Consultar, editar e desativar empresa | UC03 - manutenção | Atual | [SVG](DS05/DS05.svg) · [PNG](DS05/DS05.png) | [puml](DS05/DS05.puml) |
| DS06 | Creditar 1.000 moedas por semestre | UC04 | Atual | [SVG](DS06/DS06.svg) · [PNG](DS06/DS06.png) | [puml](DS06/DS06.puml) |
| DS07 | Transferir moedas para aluno | UC05 | Atual | [SVG](DS07/DS07.svg) · [PNG](DS07/DS07.png) | [puml](DS07/DS07.puml) |
| DS08 | Consultar saldo e extrato individual | UC06, UC07 | Atual | [SVG](DS08/DS08.svg) · [PNG](DS08/DS08.png) | [puml](DS08/DS08.puml) |
| DS09 | Cadastrar e gerenciar vantagens | UC08 e extensão de gestão | Atual | [SVG](DS09/DS09.svg) · [PNG](DS09/DS09.png) | [puml](DS09/DS09.puml) |
| DS10 | Consultar e selecionar vantagens | UC09 | Atual | [SVG](DS10/DS10.svg) · [PNG](DS10/DS10.png) | [puml](DS10/DS10.puml) |
| DS11 | Resgatar vantagem e gerar código | UC10 | Atual | [SVG](DS11/DS11.svg) · [PNG](DS11/DS11.png) | [puml](DS11/DS11.puml) |
| DS12 | Notificar recebimento de moedas | UC11 - PREVISTO | Previsto | [SVG](DS12/DS12.svg) · [PNG](DS12/DS12.png) | [puml](DS12/DS12.puml) |
| DS13 | Enviar cupom ao aluno e à empresa | UC12 - PREVISTO | Previsto | [SVG](DS13/DS13.svg) · [PNG](DS13/DS13.png) | [puml](DS13/DS13.puml) |
| DS14 | Alterar senha e concluir primeiro acesso | Extensão de UC01 | Atual | [SVG](DS14/DS14.svg) · [PNG](DS14/DS14.png) | [puml](DS14/DS14.puml) |
| DS15 | Redefinir senha temporária presencialmente | Extensão de UC02/UC03 | Atual | [SVG](DS15/DS15.svg) · [PNG](DS15/DS15.png) | [puml](DS15/DS15.puml) |
| DS16 | Encerrar sessão | Extensão de UC01 | Atual | [SVG](DS16/DS16.svg) · [PNG](DS16/DS16.png) | [puml](DS16/DS16.puml) |
| IG01 | Visão geral de interação - versão atual | UC01 a UC12 | Atual | [SVG](IG01/IG01.svg) · [PNG](IG01/IG01.png) | [drawio](IG01/IG01.drawio) |
| IG02 | Visão geral de interação - complemento previsto | UC01 a UC12 | Previsto | [SVG](IG02/IG02.svg) · [PNG](IG02/IG02.png) | [drawio](IG02/IG02.drawio) |
| IG03 | Operação de professor | Refinamento de IG01 | Atual | [SVG](IG03/IG03.svg) · [PNG](IG03/IG03.png) | [drawio](IG03/IG03.drawio) |
| IG04 | Operação de aluno | Refinamento de IG01 | Atual | [SVG](IG04/IG04.svg) · [PNG](IG04/IG04.png) | [drawio](IG04/IG04.drawio) |
| IG05 | Operação de empresa | Refinamento de IG01 | Atual | [SVG](IG05/IG05.svg) · [PNG](IG05/IG05.png) | [drawio](IG05/IG05.drawio) |

## Notação e nível de abstração

- Sequência: quadro `sd`, atores, linhas de vida, mensagens síncronas contínuas, retornos tracejados e fragmentos `alt`, `opt`, `loop`, `break` e `ref`.
- Visão geral: nós iniciais/finais, decisões e merges, guardas em colchetes e usos de interação com aba `ref`. As alternativas por perfil são exclusivas; os merges não são barras de sincronização.
- Cada `ref` aponta a uma interação identificada no catálogo. IG03-IG05 detalham as alternativas de IG01. O crédito semestral DS06 é independente da sessão e está identificado por nota, sem acoplar seu término ao fluxo do usuário.
- `boundary`, `control`, `entity` e `repository` são rótulos convencionais de responsabilidade usados para leitura arquitetural; não se afirma que constituem um perfil UML formal aplicado ao projeto.
- Uma linha de vida “Repositórios JPA” agrupa repositórios reais por legibilidade. As mensagens identificam os repositórios envolvidos. “Serviço do titular/cadastro” significa AlunoService ou EmpresaParceiraService, conforme o perfil; não é uma nova classe.
- Passos internos, montagem de modelos e operações de domínio podem ser resumidos. Os diagramas não são rastros completos de todas as chamadas de Hibernate, Spring ou getters. As interações financeiras explicitam as entidades que recebem débito/crédito e a transação de resgate.
- Métodos `@Transactional` delimitam persistência e rollback. Alterações de entidades gerenciadas podem ser persistidas por dirty checking sem chamada explícita a `save`.
- Sessão ativa, perfil, CSRF e eventual troca de senha pendente são precondições das operações protegidas; particularidades aparecem em cada fluxo.

## Edição e formatos

- Sequências: fontes `.puml` abertas em ferramenta compatível com PlantUML. Não é necessário editar imagens.
- Visões gerais: fontes `.drawio` abertas no diagrams.net, mantendo os elementos UML e os identificadores de referência.
- SVG e PDF são vetoriais. PNG é uma alternativa para slides e visualizadores. O PDF consolidado usa páginas ajustadas ao tamanho dos diagramas para manter legibilidade.
- O layout das fontes editáveis pode variar ao ser renderizado em outras ferramentas; revisar o resultado após alterar o conteúdo.

## Conformidade e pendências reais

1. O código restringe novos cadastros ao professor. O enunciado descreve aluno e empresa realizando seu cadastro. DS02 e DS04 documentam o comportamento atual; a divergência não foi ocultada ou “corrigida” somente no desenho.
2. RF06, RF14 e RF15 continuam pendentes. DS12/DS13 e IG02 detalham o contrato previsto; envio, tratamento de falhas e nova tentativa exigem implementação.
3. “Estoque” de vantagens no sistema é disponibilidade ativa/inativa, sem quantidade de unidades.
4. O código disponibiliza códigos no extrato; não possui confirmação presencial de utilização de cupom.
5. Professores e instituições são pré-cadastrados. O mecanismo administrativo não é um novo caso de uso público.
6. Não foram alterados código Java, banco local, autenticação ou funcionalidades nesta entrega de diagramas.

## Referências

- Enunciado fornecido: `LABORATORIO_4_LAB_DESENVOLVIMENTO_DE_SOFTWARE (1) (1).pdf`, descrição do sistema (p. 1), avaliação/alinhamento e seção 4.4 Lab04S01 (p. 2).
- [UML 2.5.1 - OMG, seção 17: Interactions](https://www.omg.org/spec/UML/2.5.1/), incluindo notação de diagramas de sequência e visão geral de interação.
- [Código de referência](https://github.com/mariaclaranerii/sistema-moeda-estudantil/tree/63ec82352b3c07cf17b6fcdd289221f275de08fb). Os arquivos de cada interação estão listados na matriz.
