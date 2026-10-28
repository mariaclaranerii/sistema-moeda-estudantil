package br.edu.pucminas.moedaestudantil.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired AlunoRepository alunos;
    @Autowired PasswordEncoder encoder;

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
    void professorNaoPodeCadastrarCPFComMaisDeOnzeDigitos() throws Exception {
        long id = instituicoes.findAll().get(0).getId();
        mockMvc.perform(get("/alunos/novo").with(user("prof.cpf").roles("PROFESSOR")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("maxlength=\"11\"")))
            .andExpect(content().string(containsString("pattern=\"[0-9]{11}\"")));

        mockMvc.perform(post("/alunos").with(user("prof.cpf").roles("PROFESSOR")).with(csrf())
            .param("nome", "Aluno CPF longo")
            .param("email", "cpf.longo@exemplo.com")
            .param("cpf", "123456789012")
            .param("rg", "MG-8765")
            .param("endereco", "Rua Principal, 1")
            .param("curso", "Engenharia")
            .param("login", "aluno.cpf.longo")
            .param("senha", "segredo123")
            .param("instituicaoId", Long.toString(id)))
            .andExpect(status().isOk())
            .andExpect(view().name("alunos/formulario"))
            .andExpect(content().string(containsString("Informe um CPF com 11 dígitos.")));
    }

    @Test
    void novoAlunoEntraComEmailETrocaSenhaAntesDeUsarSistema() throws Exception {
        long instituicao = instituicoes.findAll().get(0).getId();
        mockMvc.perform(get("/alunos/novo").with(user("prof.novo").roles("PROFESSOR")))
            .andExpect(status().isOk())
            .andExpect(content().string(not(containsString("name=\"login\""))))
            .andExpect(content().string(not(containsString("name=\"senha\""))));

        mockMvc.perform(post("/alunos").with(csrf()).with(user("prof.novo").roles("PROFESSOR"))
            .param("nome", "Aluno por E-mail")
            .param("email", "Aluno.Novo@Exemplo.com")
            .param("cpf", "32456789321")
            .param("rg", "MG-222245")
            .param("endereco", "Rua Universitária, 10")
            .param("curso", "Engenharia")
            .param("instituicaoId", Long.toString(instituicao)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));

        var aluno = alunos.findByEmailIgnoreCase("aluno.novo@exemplo.com").orElseThrow();
        assertThat(aluno.getLogin()).isEqualTo("aluno.novo@exemplo.com");
        assertThat(aluno.isTrocaSenhaPendente()).isTrue();
        assertThat(encoder.matches("Aluno@2026!", aluno.getSenhaHash())).isTrue();

        mockMvc.perform(formLogin().user("aluno.novo@exemplo.com").password("Aluno@2026!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));
        mockMvc.perform(get("/painel").with(user("aluno.novo@exemplo.com").roles("ALUNO")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/perfil/senha"));
        mockMvc.perform(get("/perfil/senha").with(user("aluno.novo@exemplo.com").roles("ALUNO")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Alterar minha senha")));
        mockMvc.perform(post("/perfil/senha").with(csrf()).with(user("aluno.novo@exemplo.com").roles("ALUNO"))
            .param("senhaAtual", "senha-invalida").param("novaSenha", "NovaSenha@2026")
            .param("confirmacao", "NovaSenha@2026"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/perfil/senha"));
        assertThat(alunos.findById(aluno.getId()).orElseThrow().isTrocaSenhaPendente()).isTrue();

        mockMvc.perform(post("/perfil/senha").with(csrf()).with(user("aluno.novo@exemplo.com").roles("ALUNO"))
            .param("senhaAtual", "Aluno@2026!").param("novaSenha", "NovaSenha@2026")
            .param("confirmacao", "NovaSenha@2026"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/perfil"));
        assertThat(alunos.findById(aluno.getId()).orElseThrow().isTrocaSenhaPendente()).isFalse();
        assertThat(encoder.matches("NovaSenha@2026",
            alunos.findById(aluno.getId()).orElseThrow().getSenhaHash())).isTrue();
        mockMvc.perform(get("/painel").with(user("aluno.novo@exemplo.com").roles("ALUNO")))
            .andExpect(status().isOk());
        mockMvc.perform(formLogin().user("aluno.novo@exemplo.com").password("Aluno@2026!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"));
        mockMvc.perform(formLogin().user("aluno.novo@exemplo.com").password("NovaSenha@2026"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));
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
