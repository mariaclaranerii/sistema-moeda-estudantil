# Lab03S03 — Entrega final

## Escopo funcional

- Autenticação por login/senha e controle de acesso para aluno, professor e empresa parceira.
- CRUD de aluno e empresa parceira, com exclusão lógica e acesso restrito ao titular.
- Listas de alunos e empresas acessíveis apenas ao professor, com nome, dados básicos e situação de cada cadastro.
- Campo de CPF limitado a 11 algarismos na interface, com validação também no servidor; RG e endereço continuam obrigatórios conforme o enunciado.
- Persistência relacional H2 em arquivo, ORM JPA/Hibernate e repositórios Spring Data.
- Cadastro prévio de instituições; professores cadastrados no perfil de demonstração.
- Crédito de 1.000 moedas por semestre, acumulativo, com registro único por professor/semestre.
- Envio de moedas com motivo obrigatório e conferência do saldo do professor.
- Extrato individual por perfil, com movimentações vinculadas ao identificador do usuário.
- Cadastro e estoque de vantagens por empresa parceira: listagem de ativas/inativas, edição dos próprios itens e ativação/desativação sem excluir o histórico.
- Apenas vantagens ativas ficam disponíveis para novos resgates; custo, descrição e foto podem ser editados pela empresa titular.
- Resgate de vantagens, desconto do saldo e código único de conferência visível no extrato do aluno e da empresa.
- Alteração de senha sem e-mail: alunos e empresas autenticados informam a senha atual e a nova; o professor pode redefinir senhas temporárias de ambos, exibidas uma única vez e com troca obrigatória no próximo acesso. O envio automático de e-mails não integra esta versão.

## Arquitetura

A aplicação segue MVC, com controllers Spring MVC e REST, serviços responsáveis pelas regras de negócio, entidades JPA e repositórios Spring Data. O banco é H2 em modo arquivo. O sistema não tenta enviar e-mails nesta versão. Transferências e resgates são confirmados pelo próprio painel e registrados no extrato. Registros históricos de notificações de versões anteriores podem permanecer no banco, mas não são enviados.

Modelos da versão final:

- [Diagrama de classes — PNG](diagramas/v2/classes.png) · [SVG](diagramas/v2/classes.svg) · [PlantUML](diagramas/v2/classes.puml)
- [Diagrama de componentes — PNG](diagramas/v2/componentes.png) · [SVG](diagramas/v2/componentes.svg) · [PlantUML](diagramas/v2/componentes.puml)
- [Modelo ER — PNG](diagramas/v2/modelo-er.png) · [SVG](diagramas/v2/modelo-er.svg) · [PlantUML](diagramas/v2/modelo-er.puml)
- [Detalhamento da arquitetura e persistência](arquitetura-e-persistencia.md)
- [Rastreabilidade funcional](rastreabilidade.md)
- [Apresentação da Sprint 03 (PDF)](apresentacao-sprint-03.pdf)

### Diagramas em imagem

![Diagrama de classes — Sprint 03](diagramas/v2/classes.png)

![Diagrama de componentes — Sprint 03](diagramas/v2/componentes.png)

![Modelo entidade-relacionamento — Sprint 03](diagramas/v2/modelo-er.png)

As versões da Sprint 01 e o Modelo ER da Sprint 02 permanecem em seus respectivos diretórios.

## Execução

Pré-requisitos: JDK 17 e Maven 3.9+.

```bash
mvn spring-boot:run
mvn test
```

Acesse `http://localhost:8080`. Os cadastros de alunos e empresas são feitos exclusivamente por professores autenticados. Visitantes podem apenas acessar a página de entrada. Professores são previamente cadastrados; para a demonstração, use:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

Contas de demonstração (somente para ambiente local):

| Perfil | Login | Senha |
|---|---|---|
| Aluno | ana.silva | 12345678 |
| Professor | prof.carlos | 12345678 |
| Empresa | livraria.academica | 12345678 |

O perfil `demo` cria registros de exemplo apenas quando as tabelas correspondentes estão vazias. Utilize um banco H2 de demonstração e **não ative esse perfil em ambiente público**.

### Cadastro de alunos: login e senha inicial

O professor não precisa informar um login ou senha: o sistema usa automaticamente o e-mail cadastrado como login (normalizado para minúsculas) e atribui a senha temporária padrão `Aluno@2026!`, personalizável pela variável de ambiente `ALUNO_SENHA_INICIAL`. A senha é armazenada com BCrypt, e novos alunos devem trocá-la no primeiro acesso antes de utilizar o painel ou as APIs. O aluno também pode alterá-la em **Meu perfil → Alterar senha** (informando a senha atual, uma nova de 8 a 72 caracteres e a confirmação).

Alunos cadastrados anteriormente continuam acessando com o login antigo ou com o e-mail. Ao editar o e-mail, o login é atualizado para o novo endereço e a sessão é encerrada para novo acesso. A mudança não redefine a senha dos cadastros antigos.

**Segurança:** a senha inicial compartilhada serve somente para demonstrações locais. Em um sistema público, uma senha igual para todos permitiria assumir contas antes do primeiro acesso; nesse cenário, implemente credenciais temporárias aleatórias individuais e entrega segura.

### Sem envio de e-mails nesta versão

A aplicação não utiliza Brevo, SMTP nem qualquer serviço externo de e-mail. O endereço cadastrado pelo professor permanece como login dos novos alunos, mas o sistema não precisa acessar a caixa de entrada. O aluno troca a própria senha em **Meu perfil → Alterar senha**, informando a senha atual e uma nova senha com confirmação.

Caso um aluno **ou empresa** esqueça a senha, o professor acessa **Meu painel → Ver alunos cadastrados** ou **Ver empresas cadastradas → Redefinir senha**, recebe uma senha temporária aleatória exibida uma única vez e a entrega ao titular por um meio privado, preferencialmente presencialmente. No próximo acesso, o titular precisa trocá-la antes de usar as demais funcionalidades. A ação exige autenticação de professor e proteção CSRF; a senha temporária é armazenada apenas como hash.

Para empresas novas, a senha inicial ainda é definida pelo professor no cadastro (o login da empresa continua independente do e-mail). A empresa deve alterá-la em seu primeiro acesso, assim como o aluno. Empresas anteriores continuam com as credenciais existentes e não têm a senha redefinida automaticamente. A empresa pode trocar sua senha pelo menu **Meu perfil → Alterar senha** ou na edição dos dados cadastrais; a alteração exige informar a senha atual, uma nova com 8 a 72 caracteres e a confirmação.

Os cupons dos resgates e os saldos permanecem consultáveis nos extratos de aluno e empresa. **Limitação em relação ao enunciado:** os avisos por e-mail de transferência e os e-mails de cupons para aluno/empresa não estão implementados na versão atual.

### Restrições da versão

- O cadastro de professores e instituições ocorre fora da interface pública, como indicado no enunciado.
- O sistema não implementa recuperação de senha por e-mail: utiliza redefinição manual pelo professor, e não inclui validação presencial do cupom.
- O arquivo HTML de painel da Sprint 02 permanece preservado como artefato histórico. O fluxo operacional atual usa `/login` e `/painel`.
- O H2 em arquivo e a geração automática do esquema atendem à execução acadêmica, não ao uso em produção.
