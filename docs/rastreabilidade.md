# Matriz de rastreabilidade - versão 1

Esta matriz relaciona os requisitos, as histórias de usuário e os três modelos UML da Lab03S01.

## Requisitos funcionais

| Requisito | História / condição | Caso de uso | Classes principais | Componentes responsáveis |
|---|---|---|---|---|
| RF01 | HU02 | UC02 | `Aluno`, `Usuario` | Controller, View, Serviços de aplicação, Persistência |
| RF02 | HU02 | UC02 | `Aluno`, `InstituicaoEnsino` | Controller, View, Serviços de aplicação, Persistência |
| RF03 | Pré-condição do domínio | Legenda do diagrama | `Professor`, `InstituicaoEnsino` | Persistência; processo administrativo fora do fluxo público |
| RF04 | HU04 | UC04 | `Professor`, `Conta`, `CreditoSemestral` | Agendador semestral, Serviços de aplicação, Persistência |
| RF05 | HU05 | UC05 | `Professor`, `Aluno`, `Conta`, `TransferenciaMoeda` | Controller, Serviços de aplicação, Modelo de domínio, Persistência |
| RF06 | HU05 | UC11 | `TransferenciaMoeda`, `Aluno` | Serviços de aplicação, Adaptador de e-mail, SMTP |
| RF07 | HU06 | UC06 | `Professor`, `Transacao` | Controller, View, Serviços de aplicação, Persistência |
| RF08 | HU07 | UC07 | `Aluno`, `Transacao` | Controller, View, Serviços de aplicação, Persistência |
| RF09 | HU03 | UC03 | `EmpresaParceira`, `Usuario` | Controller, View, Serviços de aplicação, Persistência |
| RF10 | HU08 | UC08 | `EmpresaParceira`, `Vantagem` | Controller, View, Serviços de aplicação, Persistência |
| RF11 | HU09 | UC09 | `Vantagem`, `EmpresaParceira` | Controller, View, Serviços de aplicação, Persistência |
| RF12 | HU10 | UC10 | `Aluno`, `Conta`, `Vantagem`, `ResgateVantagem` | Controller, Serviços de aplicação, Modelo de domínio, Persistência |
| RF13 | HU10 | UC10 | `ResgateVantagem` | Serviços de aplicação, Modelo de domínio |
| RF14 | HU10 | UC12 | `Aluno`, `ResgateVantagem` | Serviços de aplicação, Adaptador de e-mail, SMTP |
| RF15 | HU10 | UC12 | `EmpresaParceira`, `ResgateVantagem` | Serviços de aplicação, Adaptador de e-mail, SMTP |
| RF16 | HU01 | UC01 | `Usuario` e especializações | Autenticação e autorização, Persistência |
| RF17 | HU01 | UC01 e estereótipo `autenticado` | `Usuario.ativo` | Autenticação e autorização, Controller |
| RF18 | HU02 | UC02 | `Aluno`, `Usuario` | MVC e Persistência |
| RF19 | HU03 | UC03 | `EmpresaParceira`, `Usuario` | MVC e Persistência |

## Regras de negócio

| Regra | Evidência nas histórias | Evidência nos modelos |
|---|---|---|
| RN01 e RN02 | CA04.1 e CA04.2 | UC04; `Conta`; `CreditoSemestral`; Agendador semestral |
| RN03 a RN06 | CA05.1 a CA05.5 | UC05 e UC11; `Conta`; `TransferenciaMoeda`; Serviços de aplicação |
| RN07 | CA08.1 | UC08; `Vantagem` |
| RN08 e RN09 | CA10.1 e CA10.2 | UC10; `Conta`; `ResgateVantagem`; Serviços de aplicação |
| RN10 | CA10.3 a CA10.5 | UC10 e UC12; `ResgateVantagem`; Adaptador de e-mail |
| RN11 | Pré-condições do domínio | Legenda do diagrama de casos de uso; `InstituicaoEnsino` e `Professor` |
