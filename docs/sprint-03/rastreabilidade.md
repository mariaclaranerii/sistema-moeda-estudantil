# Rastreabilidade — Sprint 03

A matriz relaciona os requisitos funcionais à implementação final e aos artefatos de modelagem.

| Requisito | Implementação / componente | Observações |
|---|---|---|
| RF01 | `AlunoController`, `AlunoService`, `Aluno` | Cadastro com campos previstos |
| RF02 | `InstituicaoEnsinoRepository`, `AlunoForm` | Seleção entre instituições cadastradas |
| RF03 | `Professor`, `ProfessorRepository`, `DadosIniciaisConfig` | Professores previamente cadastrados no perfil de demonstração |
| RF04 | `CreditoSemestralService`, `CreditoSemestral` | 1.000 moedas por semestre; registro por professor/semestre |
| RF05 | `MovimentacaoService.enviar`, `Transacao` | Saldo, quantidade e motivo obrigatórios |
| RF06 | `NotificacaoService`, `NotificacaoEmail` | Registro de e-mail e transporte SMTP configurável |
| RF07 | `PainelController`, `MovimentacaoService.extrato` | Saldo e histórico de professor |
| RF08 | `PainelController`, `MovimentacaoService.extrato` | Saldo e histórico de aluno |
| RF09 | `EmpresaParceiraController`, `EmpresaParceiraService` | Cadastro de empresa |
| RF10 | `MovimentacaoService.cadastrarVantagem`, `Vantagem` | Custo, descrição e URL HTTPS da foto |
| RF11 | `PainelController`, `VantagemRepository` | Catálogo de vantagens |
| RF12 | `MovimentacaoService.resgatar` | Débito monetário no resgate |
| RF13 | `Transacao.codigoResgate` | Código aleatório único |
| RF14 | `NotificacaoService`, `NotificacaoEmail` | Mensagem do aluno com cupom |
| RF15 | `NotificacaoService`, `NotificacaoEmail` | Mensagem da empresa com o mesmo cupom |
| RF16 | `SegurancaConfig`, `LoginController` | Formulário de login e senhas BCrypt |
| RF17 | `SegurancaConfig`, `SessaoService` | Sessão, CSRF e verificações de titularidade/perfil |
| RF18 | `AlunoController`, `AlunoApiController`, `AlunoService` | Cadastro, consulta, edição e desativação lógica |
| RF19 | `EmpresaParceiraController`, `EmpresaParceiraApiController`, `EmpresaParceiraService` | Cadastro, consulta, edição e desativação lógica |

## Modelos

- [Diagrama de classes — PNG](diagramas/v2/classes.png) · [SVG](diagramas/v2/classes.svg) · [PlantUML](diagramas/v2/classes.puml)
- [Diagrama de componentes — PNG](diagramas/v2/componentes.png) · [SVG](diagramas/v2/componentes.svg) · [PlantUML](diagramas/v2/componentes.puml)
- [Modelo ER — PNG](diagramas/v2/modelo-er.png) · [SVG](diagramas/v2/modelo-er.svg) · [PlantUML](diagramas/v2/modelo-er.puml)

As representações completas da arquitetura e do acesso ao banco estão em [arquitetura-e-persistencia.md](arquitetura-e-persistencia.md).

## Comportamento do envio de e-mails

O sistema registra os avisos na fila persistida durante a transferência ou o resgate. A remessa externa ocorre com a configuração SMTP habilitada. Quando a integração não está configurada, as mensagens permanecem pendentes, identificáveis pela coluna `enviada`.
