# Rastreabilidade — Sprint 03

A matriz relaciona os requisitos funcionais à implementação final e aos artefatos de modelagem.

| Requisito | Implementação / componente | Observações |
|---|---|---|
| RF01 | `AlunoController`, `AlunoService`, `Aluno` | Cadastro com campos previstos |
| RF02 | `InstituicaoEnsinoRepository`, `AlunoForm` | Seleção entre instituições cadastradas |
| RF03 | `Professor`, `ProfessorRepository`, `DadosIniciaisConfig` | Professores previamente cadastrados no perfil de demonstração |
| RF04 | `CreditoSemestralService`, `CreditoSemestral` | 1.000 moedas por semestre; registro por professor/semestre |
| RF05 | `MovimentacaoService.enviar`, `Transacao` | Saldo, quantidade e motivo obrigatórios |
| RF06 | — | Não implementado nesta versão: aviso por e-mail ao aluno após receber moedas |
| RF07 | `PainelController`, `MovimentacaoService.extrato` | Saldo e histórico de professor |
| RF08 | `PainelController`, `MovimentacaoService.extrato` | Saldo e histórico de aluno |
| RF09 | `EmpresaParceiraController`, `EmpresaParceiraService` | Cadastro de empresa |
| RF10 | `MovimentacaoService.cadastrarVantagem`, `Vantagem` | Custo, descrição e URL HTTPS da foto |
| RF11 | `PainelController`, `VantagemRepository` | Catálogo de vantagens |
| RF12 | `MovimentacaoService.resgatar` | Débito monetário no resgate |
| RF13 | `Transacao.codigoResgate` | Código aleatório único |
| RF14 | `Transacao.codigoResgate`, `PainelController` | Código disponível no extrato; envio do cupom por e-mail não implementado |
| RF15 | `Transacao.codigoResgate`, `PainelController` | Código disponível no extrato da empresa; e-mail não implementado |
| RF16 | `SegurancaConfig`, `LoginController` | Formulário de login e senhas BCrypt |
| RF17 | `SegurancaConfig`, `SessaoService` | Sessão, CSRF e verificações de titularidade/perfil |
| RF18 | `AlunoController`, `AlunoApiController`, `AlunoService` | Cadastro, consulta, edição e desativação lógica |
| RF19 | `EmpresaParceiraController`, `EmpresaParceiraApiController`, `EmpresaParceiraService` | Cadastro, consulta, edição e desativação lógica |

## Modelos

- [Diagrama de classes — PNG](diagramas/v2/classes.png) · [SVG](diagramas/v2/classes.svg) · [PlantUML](diagramas/v2/classes.puml)
- [Diagrama de componentes — PNG](diagramas/v2/componentes.png) · [SVG](diagramas/v2/componentes.svg) · [PlantUML](diagramas/v2/componentes.puml)
- [Modelo ER — PNG](diagramas/v2/modelo-er.png) · [SVG](diagramas/v2/modelo-er.svg) · [PlantUML](diagramas/v2/modelo-er.puml)

As representações completas da arquitetura e do acesso ao banco estão em [arquitetura-e-persistencia.md](arquitetura-e-persistencia.md).

## Acesso e recuperação de senha sem e-mail

O aluno altera a senha diretamente pelo perfil, usando a senha atual. Caso a esqueça, um professor autenticado redefine uma senha temporária aleatória na listagem de alunos, que deverá ser entregue pessoalmente ao titular. A troca é obrigatória no próximo acesso. Não existem chamadas SMTP ou filas ativas de envio de e-mails; por isso RF06, RF14 e RF15 continuam pendentes de implementação se o sistema precisar atender integralmente ao enunciado.
