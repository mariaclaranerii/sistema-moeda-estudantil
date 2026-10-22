package br.edu.pucminas.moedaestudantil.web.api.dto;

public record SalvarEmpresaRequest(
        String nomeFantasia,
        String nome,
        String razaoSocial,
        String cnpj,
        String email,
        String endereco,
        String vantagens,
        String login,
        String senha
) {
    public String obterNome() {
        if (nomeFantasia != null && !nomeFantasia.isBlank()) {
            return nomeFantasia;
        }
        return nome != null ? nome : "";
    }
}
