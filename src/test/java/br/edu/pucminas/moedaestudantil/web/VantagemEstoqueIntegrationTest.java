package br.edu.pucminas.moedaestudantil.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VantagemEstoqueIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired EmpresaParceiraRepository empresas;
    @Autowired VantagemRepository vantagens;
    @Autowired AlunoRepository alunos;
    @Autowired InstituicaoEnsinoRepository instituicoes;
    @Autowired PasswordEncoder encoder;
    @Autowired TransacaoRepository transacoes;

    private EmpresaParceira empresa(String login) {
        return empresas.save(new EmpresaParceira("Parceira " + login, login, encoder.encode("senha12345"),
            login + "@exemplo.com"));
    }

    private Vantagem vantagem(EmpresaParceira e, String titulo) {
        Vantagem vantagem = new Vantagem(e.getId(), e.getNome(), titulo, 25,
            "Desconto em material didático", "fa-gift");
        vantagem.setFotoReferencia("https://example.com/produto.jpg");
        return vantagens.save(vantagem);
    }

    @Test
    void estoqueMostraVantagensDaEmpresaEPermiteEdicao() throws Exception {
        EmpresaParceira loja = empresa("loja.estoque");
        EmpresaParceira outra = empresa("outra.estoque");
        Vantagem item = vantagem(loja, "Desconto Livros");
        vantagem(outra, "Vantagem de outra empresa");

        mockMvc.perform(get("/painel").with(user(loja.getLogin()).roles("EMPRESA")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Estoque de vantagens cadastradas")))
            .andExpect(content().string(containsString("Desconto Livros")))
            .andExpect(content().string(containsString("Editar")))
            .andExpect(content().string(containsString("Desativar")))
            .andExpect(content().string(not(containsString("Vantagem de outra empresa"))));

        mockMvc.perform(get("/painel/vantagens/{id}/editar", item.getId())
            .with(user(loja.getLogin()).roles("EMPRESA")))
            .andExpect(status().isOk())
            .andExpect(view().name("vantagens/editar"))
            .andExpect(content().string(containsString("Desconto Livros")));

        mockMvc.perform(post("/painel/vantagens/{id}/editar", item.getId())
            .with(user(loja.getLogin()).roles("EMPRESA")).with(csrf())
            .param("titulo", "Novo desconto")
            .param("custo", "40")
            .param("descricao", "Descrição atualizada")
            .param("fotoReferencia", "https://example.com/atualizada.jpg"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));

        Vantagem atualizada = vantagens.findById(item.getId()).orElseThrow();
        assertThat(atualizada.getTitulo()).isEqualTo("Novo desconto");
        assertThat(atualizada.getCustoMoedas()).isEqualTo(40);
        assertThat(atualizada.getDescricao()).isEqualTo("Descrição atualizada");
        assertThat(atualizada.isAtivo()).isTrue();
    }

    @Test
    void empresaNaoPodeAlterarOuDesativarVantagemDeOutraEmpresa() throws Exception {
        EmpresaParceira dona = empresa("dona.vantagem");
        EmpresaParceira invasora = empresa("invasora.vantagem");
        Vantagem item = vantagem(dona, "Vantagem exclusiva");
        var loginOutra = user(invasora.getLogin()).roles("EMPRESA");

        mockMvc.perform(get("/painel/vantagens/{id}/editar", item.getId()).with(loginOutra))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/painel/vantagens/{id}/editar", item.getId())
            .with(user(invasora.getLogin()).roles("EMPRESA")).with(csrf())
            .param("titulo", "Alterado").param("custo", "1")
            .param("descricao", "Outra descrição")
            .param("fotoReferencia", "https://example.com/alterado.jpg"))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/painel/vantagens/{id}/situacao", item.getId())
            .with(user(invasora.getLogin()).roles("EMPRESA")).with(csrf()))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/painel/vantagens/{id}/editar", item.getId())
            .with(user("aluno.sem.acesso").roles("ALUNO")))
            .andExpect(status().isForbidden());

        assertThat(vantagens.findById(item.getId()).orElseThrow().isAtivo()).isTrue();
        assertThat(vantagens.findById(item.getId()).orElseThrow().getTitulo()).isEqualTo("Vantagem exclusiva");
    }

    @Test
    void desativarImpedeResgateMasPreservaHistoricoEPermiteReativar() throws Exception {
        EmpresaParceira loja = empresa("loja.resgate");
        Vantagem item = vantagem(loja, "Oferta temporária");
        InstituicaoEnsino i = instituicoes.findAll().get(0);
        Aluno aluno = new Aluno("Aluno Resgate", "aluno.catalogo", encoder.encode("senha12345"),
            "aluno.catalogo@exemplo.com", "12398765432", "MG-111222",
            "Rua Cadastro, 10", "Software", i);
        aluno.setSaldo(100);
        aluno = alunos.save(aluno);

        mockMvc.perform(post("/painel/vantagens/{id}/situacao", item.getId())
            .with(user(loja.getLogin()).roles("EMPRESA")).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));
        assertThat(vantagens.findById(item.getId()).orElseThrow().isAtivo()).isFalse();

        mockMvc.perform(get("/painel").with(user(aluno.getLogin()).roles("ALUNO")))
            .andExpect(status().isOk())
            .andExpect(content().string(not(containsString("Oferta temporária"))));

        mockMvc.perform(post("/api/transacoes/resgatar")
            .with(user(aluno.getLogin()).roles("ALUNO")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"vantagemId\":" + item.getId() + "}"))
            .andExpect(status().isBadRequest());
        assertThat(alunos.findById(aluno.getId()).orElseThrow().getSaldo()).isEqualTo(100);
        assertThat(transacoes.findAllByAlunoIdOrderByDataHoraDesc(aluno.getId())).isEmpty();

        mockMvc.perform(get("/painel").with(user(loja.getLogin()).roles("EMPRESA")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Oferta temporária")))
            .andExpect(content().string(containsString("Reativar")));

        mockMvc.perform(post("/painel/vantagens/{id}/situacao", item.getId())
            .with(user(loja.getLogin()).roles("EMPRESA")).with(csrf()))
            .andExpect(status().is3xxRedirection());
        assertThat(vantagens.findById(item.getId()).orElseThrow().isAtivo()).isTrue();
        mockMvc.perform(get("/painel").with(user(aluno.getLogin()).roles("ALUNO")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Oferta temporária")));
    }

    @Test
    void apiNaoExibeVantagemInativaParaAluno() throws Exception {
        EmpresaParceira loja = empresa("loja.api");
        Vantagem item = vantagem(loja, "Cupom oculto");
        item.desativar();
        vantagens.save(item);
        InstituicaoEnsino instituicao = instituicoes.findAll().get(0);
        alunos.save(new Aluno("Aluno Catálogo", "aluno.api", encoder.encode("senha12345"),
            "catalogo.api@exemplo.com", "21212121212", "MG-211212",
            "Rua do Catálogo, 11", "Software", instituicao));
        mockMvc.perform(get("/api/vantagens").with(user("aluno.api").roles("ALUNO")))
            .andExpect(status().isOk())
            .andExpect(content().string(not(containsString("Cupom oculto"))));
        mockMvc.perform(get("/api/vantagens").with(user(loja.getLogin()).roles("EMPRESA")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Cupom oculto")));
    }
}
