package br.edu.pucminas.moedaestudantil.web.api.dto;

public record SalvarAlunoRequest(
        String nome,
        String email,
        String cpf,
        String rg,
        String endereco,
        String curso,
        Long instituicaoId,
        String login,
        String senha,
        Integer saldo
) {
}
