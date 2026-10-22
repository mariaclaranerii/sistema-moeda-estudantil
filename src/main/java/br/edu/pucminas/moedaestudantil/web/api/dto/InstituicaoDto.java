package br.edu.pucminas.moedaestudantil.web.api.dto;

import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;

public record InstituicaoDto(
        Long id,
        String nome
) {
    public static InstituicaoDto from(InstituicaoEnsino instituicao) {
        return new InstituicaoDto(instituicao.getId(), instituicao.getNome());
    }
}
