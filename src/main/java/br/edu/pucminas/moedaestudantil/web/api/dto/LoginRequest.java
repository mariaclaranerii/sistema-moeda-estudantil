package br.edu.pucminas.moedaestudantil.web.api.dto;

public record LoginRequest(
        String login,
        String senha
) {
}
