package br.edu.pucminas.moedaestudantil.web.api.dto;

import br.edu.pucminas.moedaestudantil.model.Aluno;

public record AlunoDto(
        Long id,
        String nome,
        String login,
        String email,
        String cpf,
        String rg,
        String endereco,
        String curso,
        Long instituicaoId,
        String instituicaoNome,
        Integer saldo,
        boolean ativo
) {
    public static AlunoDto from(Aluno aluno) {
        Long instId = aluno.getInstituicao() != null ? aluno.getInstituicao().getId() : null;
        String instNome = aluno.getInstituicao() != null ? aluno.getInstituicao().getNome() : "";
        return new AlunoDto(
                aluno.getId(),
                aluno.getNome(),
                aluno.getLogin(),
                aluno.getEmail(),
                aluno.getCpf(),
                aluno.getRg(),
                aluno.getEndereco(),
                aluno.getCurso(),
                instId,
                instNome,
                aluno.getSaldo(),
                aluno.isAtivo()
        );
    }
}
