package br.edu.pucminas.moedaestudantil.web.api.dto;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;

public record EmpresaDto(
        Long id,
        String nomeFantasia,
        String razaoSocial,
        String cnpj,
        String email,
        String endereco,
        String vantagens,
        String login,
        boolean ativo
) {
    public static EmpresaDto from(EmpresaParceira empresa) {
        return new EmpresaDto(
                empresa.getId(),
                empresa.getNome(),
                empresa.getRazaoSocial() != null ? empresa.getRazaoSocial() : empresa.getNome(),
                empresa.getCnpj() != null ? empresa.getCnpj() : "",
                empresa.getEmail(),
                empresa.getEndereco() != null ? empresa.getEndereco() : "",
                empresa.getVantagens() != null ? empresa.getVantagens() : "",
                empresa.getLogin(),
                empresa.isAtivo()
        );
    }
}
