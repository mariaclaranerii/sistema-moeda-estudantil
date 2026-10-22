package br.edu.pucminas.moedaestudantil.web.api.dto;

import br.edu.pucminas.moedaestudantil.model.Transacao;
import java.time.format.DateTimeFormatter;

public record TransacaoDto(
        Long id,
        String data,
        String tipo,
        String origem,
        String destino,
        String motivo,
        Integer valor
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static TransacaoDto from(Transacao transacao) {
        String dataFormatada = transacao.getDataHora() != null
                ? transacao.getDataHora().format(FORMATTER)
                : "";
        return new TransacaoDto(
                transacao.getId(),
                dataFormatada,
                transacao.getTipo(),
                transacao.getOrigem(),
                transacao.getDestino(),
                transacao.getMotivo(),
                transacao.getValor()
        );
    }
}
