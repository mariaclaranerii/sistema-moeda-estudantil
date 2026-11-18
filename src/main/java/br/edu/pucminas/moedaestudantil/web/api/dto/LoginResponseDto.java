package br.edu.pucminas.moedaestudantil.web.api.dto;

import br.edu.pucminas.moedaestudantil.model.TipoUsuario;

public record LoginResponseDto(
        Long id,
        String nome,
        String login,
        String email,
        TipoUsuario tipo,
        Integer saldo,
        String curso
) {
}
