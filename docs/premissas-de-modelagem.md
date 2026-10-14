# Premissas de modelagem

O enunciado não define todos os detalhes necessários para uma modelagem consistente. As decisões abaixo são premissas, não novos requisitos de negócio.

| ID | Premissa | Justificativa |
|---|---|---|
| PM01 | O cadastro inicial de aluno e empresa parceira é público; consulta, alteração, desativação e demais operações exigem sessão autenticada. | Não é possível autenticar um usuário antes de criar suas credenciais, embora o enunciado determine autenticação para a realização dos requisitos. |
| PM02 | O CRUD de aluno e empresa parceira é de autoatendimento: cada ator gerencia apenas o próprio cadastro. | O enunciado exige os CRUDs, mas não define um ator administrador. A premissa evita criar um novo perfil. |
| PM03 | A exclusão lógica é representada por desativação do cadastro. | Preserva transferências, resgates e extratos, evitando apagar histórico financeiro. |
| PM04 | Login e CPF são únicos no âmbito aplicável ao sistema; senhas são armazenadas apenas como hash. | Evita ambiguidade de identidade e atende a uma prática mínima de segurança sem alterar o comportamento funcional. |
| PM05 | A empresa parceira informa nome e e-mail no cadastro. | O nome permite identificá-la e o e-mail é indispensável para receber o aviso de resgate exigido. O PDF não enumera outros dados empresariais. |
| PM06 | Quantidades e saldos de moedas são números inteiros não negativos. | A moeda é descrita em unidades, sem regra de fracionamento, e transferências ou resgates não podem produzir saldo negativo. |
| PM07 | A carga de 1.000 moedas é executada automaticamente uma vez por professor e por semestre, com registro idempotente. | O enunciado exige a recorrência e o acúmulo, mas não define o mecanismo de disparo. O registro evita crédito duplicado. |
| PM08 | Transferências e resgates concluídos são registros imutáveis; correções futuras exigiriam uma nova movimentação, caso venham a ser especificadas. | Mantém a coerência dos extratos e a rastreabilidade dos saldos. |
| PM09 | A foto da vantagem é modelada como uma referência ao arquivo, não como conteúdo binário dentro da entidade. | A forma de armazenamento pertence à Sprint 02; a referência preserva independência da tecnologia. |
| PM10 | O código de resgate é único e não previsível; o mesmo valor é enviado ao aluno e à empresa parceira. | O enunciado exige um código para conferência, e unicidade é necessária para identificar o resgate correto. |
| PM11 | Instituições e professores são carregados por processo administrativo fora dos fluxos públicos desta versão. | O enunciado afirma que ambos já estarão pré-cadastrados e não descreve interface, ator operador ou formato de importação. |
| PM12 | O extrato do professor exibe envios e o saldo; o do aluno exibe recebimentos, resgates e o saldo. A carga semestral fica registrada para auditoria, mas sua exibição não é exigida. | Reproduz exatamente as transações mencionadas no enunciado e ainda permite comprovar a composição do saldo do professor. |
| PM13 | A tecnologia específica do banco de dados e a estratégia definitiva de persistência serão decididas na Sprint 02. | São entregáveis explícitos da Lab03S02 e não devem ser antecipados. |
| PM14 | Cada aluno e professor possui uma conta própria para o saldo; créditos, transferências e resgates especializam uma transação comum. | Separa os dados de identidade do estado monetário e mantém um histórico uniforme para os extratos, sem acrescentar operações ao escopo. |

## Pontos em aberto para a Sprint 02

- Campos empresariais adicionais, como CNPJ, endereço e razão social, não foram exigidos e não foram incluídos.
- O enunciado não define expiração, uso único ou cancelamento do cupom de resgate.
- Não há regra de edição, exclusão, disponibilidade ou estoque de vantagens além do cadastro.
- Não há fluxo descrito para recuperação de senha, administração de usuários ou estorno de transações.
- O formato da lista de professores enviada pela instituição não foi especificado.
