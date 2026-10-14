# Histórias de usuário - Lab03S01

As histórias abaixo derivam do enunciado e mantêm os identificadores usados na matriz de rastreabilidade.

## HU01 - Autenticar usuário

**Como** aluno, professor ou empresa parceira,
**quero** entrar no sistema com login e senha,
**para** acessar as funcionalidades permitidas ao meu perfil.

Critérios de aceitação:

- CA01.1 - Credenciais válidas iniciam uma sessão vinculada a exatamente um perfil.
- CA01.2 - Credenciais inválidas não concedem acesso.
- CA01.3 - Operações marcadas como protegidas somente podem ser realizadas em sessão autenticada.

## HU02 - Gerenciar cadastro de aluno

**Como** aluno,
**quero** criar, consultar, atualizar e desativar meu cadastro,
**para** ingressar no sistema e manter meus dados corretos.

Critérios de aceitação:

- CA02.1 - O cadastro exige nome, e-mail, CPF, RG, endereço, curso, login, senha e uma instituição de ensino.
- CA02.2 - A instituição é escolhida entre as instituições previamente cadastradas.
- CA02.3 - Após o cadastro inicial, somente o aluno autenticado consulta, altera ou desativa o próprio registro.
- CA02.4 - A desativação impede novos acessos sem apagar o histórico de movimentações, conforme PM03.

## HU03 - Gerenciar cadastro de empresa parceira

**Como** empresa parceira,
**quero** criar, consultar, atualizar e desativar meu cadastro,
**para** participar do programa e manter meus dados de contato corretos.

Critérios de aceitação:

- CA03.1 - O cadastro exige nome, e-mail, login e senha, conforme PM05.
- CA03.2 - Após o cadastro inicial, somente a empresa autenticada consulta, altera ou desativa o próprio registro.
- CA03.3 - A desativação impede novos acessos sem remover vantagens e resgates históricos, conforme PM03.

## HU04 - Receber carga semestral de moedas

**Como** professor pré-cadastrado,
**quero** receber 1.000 moedas a cada semestre,
**para** reconhecer o mérito dos meus alunos.

Critérios de aceitação:

- CA04.1 - O sistema acrescenta exatamente 1.000 moedas ao saldo do professor uma vez por semestre.
- CA04.2 - Moedas não distribuídas permanecem no saldo e são somadas à nova carga.
- CA04.3 - O crédito semestral fica registrado sem duplicação para o mesmo professor e semestre, conforme PM07.

## HU05 - Transferir moedas a um aluno

**Como** professor autenticado,
**quero** enviar moedas a um aluno e informar o motivo,
**para** reconhecer seu mérito.

Critérios de aceitação:

- CA05.1 - O professor informa aluno destinatário, quantidade positiva e motivo textual obrigatório.
- CA05.2 - A transferência é recusada se o saldo do professor for insuficiente.
- CA05.3 - Em uma transferência concluída, o saldo do professor é debitado e o saldo do aluno é creditado pela mesma quantidade.
- CA05.4 - A transferência aparece nos extratos do professor e do aluno.
- CA05.5 - O aluno recebe uma notificação por e-mail após a conclusão.

## HU06 - Consultar extrato do professor

**Como** professor autenticado,
**quero** consultar meu saldo e os envios de moedas realizados,
**para** acompanhar a utilização das moedas disponíveis.

Critérios de aceitação:

- CA06.1 - O extrato apresenta o saldo atual do professor.
- CA06.2 - O extrato lista as transferências realizadas pelo professor.
- CA06.3 - Cada item permite identificar data, quantidade, aluno destinatário e motivo.

## HU07 - Consultar extrato do aluno

**Como** aluno autenticado,
**quero** consultar meu saldo, os recebimentos e os resgates,
**para** acompanhar minhas moedas.

Critérios de aceitação:

- CA07.1 - O extrato apresenta o saldo atual do aluno.
- CA07.2 - O extrato lista transferências recebidas e vantagens resgatadas.
- CA07.3 - Cada recebimento permite identificar data, quantidade, professor e motivo.
- CA07.4 - Cada resgate permite identificar data, vantagem, empresa e custo em moedas.

## HU08 - Cadastrar vantagem

**Como** empresa parceira autenticada,
**quero** cadastrar uma vantagem,
**para** oferecer um produto ou desconto aos alunos.

Critérios de aceitação:

- CA08.1 - O cadastro exige descrição, foto do produto e custo positivo em moedas.
- CA08.2 - A vantagem fica vinculada à empresa que a cadastrou.
- CA08.3 - Uma vantagem cadastrada pode ser apresentada aos alunos para seleção.

## HU09 - Consultar vantagens

**Como** aluno autenticado,
**quero** consultar as vantagens cadastradas e seus custos,
**para** escolher uma opção de resgate.

Critérios de aceitação:

- CA09.1 - Cada vantagem apresenta descrição, foto, custo em moedas e empresa ofertante.
- CA09.2 - O aluno consegue selecionar uma vantagem para iniciar o resgate.

## HU10 - Resgatar vantagem

**Como** aluno autenticado,
**quero** trocar minhas moedas por uma vantagem,
**para** utilizar o produto ou desconto presencialmente.

Critérios de aceitação:

- CA10.1 - O resgate é recusado se o aluno não possuir saldo suficiente.
- CA10.2 - Em um resgate concluído, o custo da vantagem é descontado do saldo do aluno e registrado no extrato.
- CA10.3 - O sistema gera um código único de conferência para o resgate.
- CA10.4 - O aluno recebe por e-mail um cupom contendo o código.
- CA10.5 - A empresa parceira recebe por e-mail o mesmo código para conferência.

## Pré-condições do domínio não expressas como histórias

- Instituições de ensino participantes já estão cadastradas quando o aluno realiza seu cadastro.
- Professores já estão cadastrados, com nome, CPF, departamento e vínculo institucional, a partir da lista enviada pela instituição.
- O mecanismo administrativo desses pré-cadastros não foi especificado e permanece fora dos fluxos públicos modelados, conforme PM11.
