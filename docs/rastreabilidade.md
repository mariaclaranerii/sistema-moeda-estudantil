# Matriz de rastreabilidade - versão 1

Esta matriz relaciona os requisitos, as histórias de usuário e os três modelos UML da Lab03S01.

## Requisitos funcionais

| Requisito | História / condição | Caso de uso | Classes principais | Componentes responsáveis |
|---|---|---|---|---|
| RF01 | HU02 | UC01 | `Aluno`, `Usuario` | Controller, View, Serviços de aplicação, Persistência |
| RF02 | HU02 | UC01 | `Aluno`, `InstituicaoEnsino` | Controller, View, Serviços de aplicação, Persistência |
| RF03 | Pré-condição do domínio | Legenda do diagrama | `Professor`, `InstituicaoEnsino` | Persistência; processo administrativo fora do fluxo público |
| RF04 | HU04 | UC10 | `Professor`, `CreditoSemestral` | Agendador semestral, Serviços de aplicação, Persistência |
| RF05 | HU05 | UC11 | `Professor`, `Aluno`, `TransferenciaMoeda` | Controller, Serviços de aplicação, Modelo de domínio, Persistência |
| RF06 | HU05 | UC12 | `TransferenciaMoeda`, `Aluno` | Serviços de aplicação, Adaptador de e-mail, SMTP |
| RF07 | HU06 | UC13 | `Professor`, `TransferenciaMoeda` | Controller, View, Serviços de aplicação, Persistência |
| RF08 | HU07 | UC14 | `Aluno`, `TransferenciaMoeda`, `ResgateVantagem` | Controller, View, Serviços de aplicação, Persistência |
| RF09 | HU03 | UC05 | `EmpresaParceira`, `Usuario` | Controller, View, Serviços de aplicação, Persistência |
| RF10 | HU08 | UC15 | `EmpresaParceira`, `Vantagem` | Controller, View, Serviços de aplicação, Persistência |
| RF11 | HU09 | UC16 | `Vantagem`, `EmpresaParceira` | Controller, View, Serviços de aplicação, Persistência |
| RF12 | HU10 | UC17 | `Aluno`, `Vantagem`, `ResgateVantagem` | Controller, Serviços de aplicação, Modelo de domínio, Persistência |
| RF13 | HU10 | UC18 | `ResgateVantagem` | Serviços de aplicação, Modelo de domínio |
| RF14 | HU10 | UC19 | `Aluno`, `ResgateVantagem` | Serviços de aplicação, Adaptador de e-mail, SMTP |
| RF15 | HU10 | UC20 | `EmpresaParceira`, `ResgateVantagem` | Serviços de aplicação, Adaptador de e-mail, SMTP |
| RF16 | HU01 | UC09 | `Usuario` e especializações | Autenticação e autorização, Persistência |
| RF17 | HU01 | UC09 e estereótipo `autenticado` | `Usuario.ativo` | Autenticação e autorização, Controller |
| RF18 | HU02 | UC01 a UC04 | `Aluno`, `Usuario` | MVC e Persistência |
| RF19 | HU03 | UC05 a UC08 | `EmpresaParceira`, `Usuario` | MVC e Persistência |

## Regras de negócio

| Regra | Evidência nas histórias | Evidência nos modelos |
|---|---|---|
| RN01 e RN02 | CA04.1 e CA04.2 | UC10; `CreditoSemestral`; Agendador semestral |
| RN03 a RN06 | CA05.1 a CA05.5 | UC11 e UC12; `TransferenciaMoeda`; Serviços de aplicação |
| RN07 | CA08.1 | UC15; `Vantagem` |
| RN08 e RN09 | CA10.1 e CA10.2 | UC17; `ResgateVantagem`; Serviços de aplicação |
| RN10 | CA10.3 a CA10.5 | UC18 a UC20; `ResgateVantagem`; Adaptador de e-mail |
| RN11 | Pré-condições do domínio | Legenda do diagrama de casos de uso; `InstituicaoEnsino` e `Professor` |
