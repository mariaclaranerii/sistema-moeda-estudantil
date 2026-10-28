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
- Resgate de vantagens, desconto do saldo, código único de conferência e avisos ao aluno e à empresa.
- Fila de notificações persistida; envio SMTP executado quando a integração está configurada.

## Arquitetura

A aplicação segue MVC, com controllers Spring MVC e REST, serviços responsáveis pelas regras de negócio, entidades JPA e repositórios Spring Data. O banco é H2 em modo arquivo. O envio de e-mails utiliza registros persistidos, com tentativas agendadas; a falta de SMTP não cancela a transferência ou o resgate, e os registros permanecem pendentes.

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

### Envio de e-mails transacionais — Brevo SMTP

Os eventos já gravam notificações na tabela `notificacoes_email`:
- Professor envia moedas: um aviso ao aluno, com quantidade e motivo.
- Aluno resgata uma vantagem: um cupom com código para o aluno e um aviso com o mesmo código para a empresa.

A aplicação processa até 20 notificações pendentes por ciclo, a cada 60 segundos. Registros com falha são tentados até 5 vezes. O envio fica desativado por padrão, mas as notificações continuam registradas no banco. O status `enviada` indica que o servidor SMTP aceitou a mensagem, **não** que ela chegou à caixa de entrada.

**Preparação da conta Brevo:**

1. Crie/acesse a conta Brevo e configure um remetente verificado. Para uso com domínio próprio, configure a autenticação do domínio exigida pela Brevo.
2. No painel Brevo, em **SMTP e API → SMTP**, copie o **login SMTP** e crie uma **chave SMTP**, diferente de uma chave API.
3. Defina as variáveis de ambiente na máquina que vai executar o projeto. Exemplo em **PowerShell** (substitua os valores de exemplo; não comite nem compartilhe a chave):

```powershell
$env:EMAIL_ENABLED = "true"
$env:EMAIL_FROM = "remetente-verificado@seu-dominio.com"
$env:SMTP_USER = "login-smtp-da-conta"
$env:SMTP_PASSWORD = "sua-chave-smtp"
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

As opções padrão já estão no `application.yml`: `SMTP_HOST=smtp-relay.brevo.com`, `SMTP_PORT=587`, autenticação SMTP e STARTTLS habilitados. Se a porta 587 estiver bloqueada, a Brevo oferece a 2525 como alternativa. O `EMAIL_FROM` é o endereço do remetente autorizado na Brevo, e não necessariamente o login SMTP.

Para usar outra instância ou servidor, configure também `SMTP_HOST` e `SMTP_PORT`. As variáveis `SMTP_AUTH` e `SMTP_STARTTLS` permitem ajustar autenticação e STARTTLS, se necessário. Foram adicionados limites de conexão e leitura/gravação de 10 segundos para evitar travamentos prolongados.

**Teste de ponta a ponta:** com o projeto rodando e um aluno e uma empresa com e-mails válidos, autentique-se como professor e envie moedas. Aguarde o próximo processamento da fila (até aproximadamente 60 segundos) e confira a caixa de entrada e o spam do aluno. Depois faça um resgate com esse aluno e confira os dois e-mails com o mesmo código (aluno e empresa). Também é possível verificar os resultados no painel de eventos transacionais da Brevo.

**Segurança:** nunca envie a chave SMTP no chat nem suba credenciais ao GitHub; use somente variáveis de ambiente locais ou um gerenciador de segredos no ambiente de execução. O arquivo `.env` está excluído do Git, mas a aplicação não o carrega automaticamente.

Fontes: [Guia SMTP da Brevo](https://help.brevo.com/hc/pt/articles/7924908994450-Envie-e-mails-transacionais-usando-o-Brevo-SMTP) e [chaves SMTP](https://help.brevo.com/hc/pt/articles/7959631848850-Criar-e-gerenciar-suas-chaves-SMTP).

### Restrições da versão

- O cadastro de professores e instituições ocorre fora da interface pública, como indicado no enunciado.
- O sistema não implementa recuperação de senha nem validação presencial do cupom; essas operações não foram detalhadas.
- O arquivo HTML de painel da Sprint 02 permanece preservado como artefato histórico. O fluxo operacional atual usa `/login` e `/painel`.
- O H2 em arquivo e a geração automática do esquema atendem à execução acadêmica, não ao uso em produção.
