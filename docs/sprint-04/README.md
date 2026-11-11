# Sprint 04 — Base integrada

## Origem
Integração seletiva com dois pais Git:
- sprint-03-final: 9300f471ca6ee4a1aa42a0bee8ffa91fa146aeb2
- main: 3c43373057075311fd0161526d24e81203a4a990

## Decisões de integração
- Preservar a autenticação Spring Security, sessão, CSRF e primeiro acesso da Sprint 03.
- Preservar professores reais, crédito semestral, transferência com débito, extratos individuais, resgate com código e gestão de vantagens.
- Adaptar a identidade visual MeritCoin, cores, tipografia, formulários e responsividade da main às telas Thymeleaf.
- Incorporar a correção da main para gerar logins empresariais distintos quando o login é omitido e preservar o login ao editar o e-mail.
- Preservar a senha inicial definida pelo professor e sua troca obrigatória. Nenhuma senha padrão silenciosa foi adicionada.
- Adaptar a confirmação de operações ao painel atual com dialog acessível, textos em português e inserção por textContent.
- Manter HomeController, SessaoService e proteções de acesso; não importar o login fixo, o token aleatório sem sessão ou a seleção do primeiro aluno do portal antigo.
- Os arquivos HTML históricos da Sprint 02 continuam preservados na árvore original; o fluxo atual utiliza /login e /painel.
- main e sprint-03-final permanecem inalteradas.

## Verificação
O workflow Testes Java passa a executar também em sprint-04: testes de integração, empacotamento e inicialização com perfil demo.
Foram adicionadas regressões para colisão de login, preservação de login, login explícito duplicado e formulário de sessão.

## Próximos entregáveis — Lab04S01 / Release 02
Esta integração prepara a base da Sprint 04; não representa conclusão do laboratório.
- [x] Diagramas de sequência UML dos casos de uso.
- [x] Diagrama de visão geral de interação UML.
- [ ] RF06: e-mail de recebimento de moedas.
- [ ] RF14 e RF15: cupom por e-mail ao aluno e à empresa com o mesmo código.
- [ ] Confirmar a política de cadastro com o enunciado (atualmente novos cadastros são feitos por professores).
- [ ] Conferir o escopo aceito da Release 01 e fechar a rastreabilidade dos casos restantes.

## Execução
JDK 17+ e Maven 3.9+:
```sh
mvn test
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```
Abrir http://localhost:8080/login. Credenciais de demonstração e regras de primeiro acesso: [Sprint 03](../sprint-03/README.md).


## Modelagem dinâmica - Lab04S01

[Catálogo de diagramas, fontes editáveis e rastreabilidade](diagramas/v1/README.md) · [PDF completo - 23 páginas](diagramas/v1/diagramas-uml-lab04s01.pdf).

Foram acrescentados 16 diagramas de sequência, uma visão geral principal, três refinamentos por perfil e uma visão de complementação prevista. DS12/DS13 e IG02 estão identificados como previstos porque os e-mails de RF06/RF14/RF15 continuam sem implementação. O conjunto registra a divergência entre autocadastro do enunciado e cadastro atual restrito ao professor. Nenhum código de execução foi alterado nesta entrega de documentação.
