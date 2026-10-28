package br.edu.pucminas.moedaestudantil.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FluxoMvcIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired InstituicaoEnsinoRepository instituicoes;

    @Test
    void paginaInicialELoginPublicosPainelRestrito() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
        mockMvc.perform(get("/painel")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/alunos/novo")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/empresas/nova")).andExpect(status().is3xxRedirection());
    }

    @Test
    void visitanteVisualizaSomenteEntradaSemOpcoesDeCadastro() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(not(containsString("Cadastrar aluno"))))
            .andExpect(content().string(not(containsString("Cadastrar empresa"))))
            .andExpect(content().string(containsString("Entrar")))
            .andExpect(content().string(not(containsString("Sair da conta"))))
            .andExpect(content().string(not(containsString("Meu perfil"))));
    }

    @Test
    void loginPublicoNaoOfereceLinksDeCadastro() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(content().string(not(containsString("Não possui cadastro?"))))
            .andExpect(content().string(not(containsString("href=\"/alunos/novo\""))))
            .andExpect(content().string(not(containsString("href=\"/empresas/nova\""))));
    }

    @Test
    void visitanteNaoPodeCadastrarAlunoPorRequisicaoDireta() throws Exception {
        long id = instituicoes.findAll().get(0).getId();
        mockMvc.perform(post("/alunos").with(csrf())
            .param("nome","Aluno do MVC").param("email","mvc@exemplo.com")
            .param("cpf","11122233344").param("rg","MG-12345")
            .param("endereco","Rua MVC, 10").param("curso","Engenharia de Software")
            .param("login","aluno.mvc").param("senha","segredo123")
            .param("instituicaoId",Long.toString(id)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }
}
