package br.edu.pucminas.moedaestudantil.web.api.dto;

public record EnviarMoedasRequest(
        Long alunoId,
        Integer valor,
        String motivo,
        String professor
) {
}
