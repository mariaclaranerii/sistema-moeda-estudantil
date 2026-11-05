package br.edu.pucminas.moedaestudantil.web.api.dto;

public record LoginResponseDto(
        Long id,
        String nome,
        String login,
        String email,
        String tipo,
        Integer saldo,
        String curso,
        String token
) {
}
