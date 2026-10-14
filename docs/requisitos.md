# Requisitos do Sistema de Moeda Estudantil

Fonte obrigatória: enunciado do Laboratório 3 da disciplina Projeto de Software, PUC Minas, 2º semestre de 2026.

## Objetivo

Estimular o reconhecimento do mérito estudantil por meio de uma moeda virtual distribuída por professores aos alunos e trocada pelos alunos por produtos ou descontos oferecidos por empresas parceiras.

## Atores

| Ator | Responsabilidade no enunciado |
|---|---|
| Aluno | Cadastrar-se, autenticar-se, consultar saldo e extrato, receber moedas e resgatar vantagens. |
| Professor | Autenticar-se, receber a carga semestral, transferir moedas a alunos e consultar saldo e extrato. |
| Empresa parceira | Cadastrar-se, autenticar-se, cadastrar vantagens e receber a notificação de resgate. |
| Instituição de ensino | Participar do sistema, constar no cadastro prévio e fornecer a lista de professores no início da parceria. |
| Serviço de e-mail | Entregar notificações de recebimento de moedas e mensagens relativas ao resgate. |
| Agendador semestral | Representa o disparo automático da carga semestral de moedas; é uma premissa técnica documentada. |

## Requisitos funcionais

| ID | Requisito |
|---|---|
| RF01 | Permitir o cadastro de aluno com nome, e-mail, CPF, RG, endereço, instituição de ensino e curso. |
| RF02 | Permitir que o aluno selecione uma instituição entre as instituições participantes previamente cadastradas. |
| RF03 | Manter professores previamente cadastrados a partir da lista fornecida pela instituição, com nome, CPF, departamento e vínculo explícito com a instituição. |
| RF04 | Acrescentar 1.000 moedas ao saldo de cada professor a cada semestre. |
| RF05 | Permitir que o professor transfira moedas a um aluno, informando a quantidade e um motivo textual obrigatório. |
| RF06 | Enviar e-mail ao aluno quando ele receber moedas. |
| RF07 | Permitir que o professor consulte o saldo e o histórico de envios de moedas. |
| RF08 | Permitir que o aluno consulte o saldo e o histórico de recebimentos e resgates de moedas. |
| RF09 | Permitir o cadastro de empresa parceira. |
| RF10 | Permitir que a empresa parceira cadastre uma vantagem com custo em moedas, descrição e foto do produto. |
| RF11 | Permitir que o aluno consulte e selecione uma vantagem cadastrada. |
| RF12 | Permitir que o aluno resgate uma vantagem, descontando o custo de seu saldo. |
| RF13 | Gerar um código de conferência para cada resgate. |
| RF14 | Enviar ao aluno um e-mail de cupom com o código do resgate para a troca presencial. |
| RF15 | Enviar à empresa parceira um e-mail com o mesmo código, para conferência da troca. |
| RF16 | Permitir login e senha para alunos, professores e empresas parceiras. |
| RF17 | Exigir autenticação para as operações protegidas do sistema. |
| RF18 | Disponibilizar CRUD de alunos, conforme o entregável explícito das Sprints 02 e 03. |
| RF19 | Disponibilizar CRUD de empresas parceiras, conforme o entregável explícito das Sprints 02 e 03. |

## Regras de negócio

| ID | Regra |
|---|---|
| RN01 | A carga semestral do professor é fixa em 1.000 moedas. |
| RN02 | O saldo não utilizado pelo professor permanece acumulado; uma nova carga é somada ao saldo corrente. |
| RN03 | Uma transferência somente pode ocorrer se o professor possuir saldo suficiente. |
| RN04 | A quantidade transferida, o aluno destinatário e o motivo são obrigatórios. |
| RN05 | O motivo da transferência é uma mensagem aberta. |
| RN06 | Toda transferência concluída debita o professor, credita o aluno e integra os extratos correspondentes. |
| RN07 | O cadastro de vantagem exige custo em moedas, descrição e foto do produto. |
| RN08 | Um resgate somente pode ocorrer se o aluno possuir saldo suficiente. |
| RN09 | Todo resgate concluído debita do aluno exatamente o custo da vantagem. |
| RN10 | Aluno e empresa parceira recebem, para o mesmo resgate, o mesmo código gerado pelo sistema. |
| RN11 | Professores e instituições de ensino são pré-cadastrados, não havendo no enunciado um fluxo de cadastro público para esses atores. |

## Restrições e critérios de qualidade

- A linguagem principal da implementação deve ser Java.
- O sistema deve seguir a arquitetura MVC.
- Modelos de classes e de arquitetura devem permanecer alinhados ao código.
- Os modelos devem ser atualizados quando o projeto evoluir, preservando suas versões no GitHub.
- A qualidade final considera a adequação do sistema aos requisitos apresentados.
- O grupo deve apresentar semanalmente o andamento; a ausência implica perda de 50% dos pontos da sprint.

## Entregáveis por sprint

| Sprint | Entregáveis obrigatórios | Situação neste repositório |
|---|---|---|
| Lab03S01 | Diagrama de casos de uso, histórias de usuário, diagrama de classes e diagrama de componentes. | Produzidos para revisão. |
| Lab03S02 | Modelo ER, definição e implementação da estratégia de acesso ao banco, versão inicial dos CRUDs de aluno e empresa parceira com front-end e comunicação com back-end. | Não iniciado. |
| Lab03S03 | Versão final dos CRUDs, apresentação da arquitetura e persistência, slides, protótipo e tutorial das tecnologias; apresentação de 20 minutos. | Não iniciado. |

## Limites da Sprint 01

Nesta sprint não são produzidos modelo ER, esquema físico de banco, código Java, front-end, CRUD funcional, testes de implementação, slides, protótipo executável ou tutorial. Esses itens pertencem às sprints posteriores e dependem de aprovação.
