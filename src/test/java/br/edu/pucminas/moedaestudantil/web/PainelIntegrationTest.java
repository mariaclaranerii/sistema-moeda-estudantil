package br.edu.pucminas.moedaestudantil.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PainelIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired InstituicaoEnsinoRepository instituicoes;
    @Autowired AlunoRepository alunos;
    @Autowired ProfessorRepository professores;
    @Autowired EmpresaParceiraRepository empresas;
    @Autowired PasswordEncoder encoder;

    @Test
    void alunoAutenticadoAbrePainelComVantagensEExtrato() throws Exception {
        InstituicaoEnsino i = instituicoes.findAll().get(0);
        alunos.save(new Aluno("Aluno Painel", "aluno.painel", encoder.encode("senha12345"),
            "painel@exemplo.com", "22233344455", "MG-12345",
            "Rua Principal, 1", "Software", i));
        mockMvc.perform(get("/painel").with(user("aluno.painel").roles("ALUNO")))
            .andExpect(status().isOk())
            .andExpect(view().name("painel"))
            .andExpect(content().string(containsString("Vantagens disponíveis")))
            .andExpect(content().string(containsString("Meu perfil")))
            .andExpect(content().string(containsString("Sair da conta")))
            .andExpect(content().string(not(containsString("Cadastrar aluno"))))
            .andExpect(content().string(not(containsString("Cadastrar empresa"))))
            .andExpect(content().string(not(containsString(">Entrar</a>"))));
        mockMvc.perform(formLogin().user("aluno.painel").password("senha12345"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));
    }

    @Test
    void professorAbrePainelParaDistribuirMoedas() throws Exception {
        InstituicaoEnsino i = instituicoes.findAll().get(0);
        professores.save(new Professor("Professor Painel", "prof.painel", encoder.encode("senha12345"),
            "33344455566", "Computação", i));
        mockMvc.perform(get("/painel").with(user("prof.painel").roles("PROFESSOR")))
            .andExpect(status().isOk())
            .andExpect(view().name("painel"))
            .andExpect(content().string(containsString("Distribuir moedas")))
            .andExpect(content().string(containsString("Meu perfil")))
            .andExpect(content().string(not(containsString("Cadastrar aluno"))))
            .andExpect(content().string(not(containsString("Cadastrar empresa"))));
    }

    @Test
    void empresaAbrePainelDeVantagens() throws Exception {
        empresas.save(new EmpresaParceira("Empresa Painel", "empresa.painel",
            encoder.encode("senha12345"), "empresa@exemplo.com"));
        mockMvc.perform(get("/painel").with(user("empresa.painel").roles("EMPRESA")))
            .andExpect(status().isOk())
            .andExpect(view().name("painel"))
            .andExpect(content().string(containsString("Cadastrar vantagem")))
            .andExpect(content().string(containsString("Meu perfil")))
            .andExpect(content().string(not(containsString("Cadastrar aluno"))))
            .andExpect(content().string(not(containsString("Cadastrar empresa"))));
    }
    @Test
    void perfilApresentaDadosDoUsuarioAutenticado() throws Exception {
        InstituicaoEnsino i = instituicoes.findAll().get(0);
        alunos.save(new Aluno("Pessoa Teste", "aluno.perfil", encoder.encode("senha12345"),
            "perfil@exemplo.com", "50560670788", "MG-15432",
            "Rua do Perfil, 42", "Engenharia de Software", i));

        mockMvc.perform(get("/perfil").with(user("aluno.perfil").roles("ALUNO")))
            .andExpect(status().isOk())
            .andExpect(view().name("perfil"))
            .andExpect(content().string(containsString("Pessoa Teste")))
            .andExpect(content().string(containsString("perfil@exemplo.com")))
            .andExpect(content().string(containsString("Meu perfil")));
        mockMvc.perform(get("/perfil")).andExpect(status().is3xxRedirection());
    }

    @Test
    void autenticadosNaoPodemAbrirOuEnviarNovosCadastros() throws Exception {
        mockMvc.perform(get("/alunos/novo").with(user("aluno.teste").roles("ALUNO")))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/empresas/nova").with(user("professor.teste").roles("PROFESSOR")))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/alunos").with(user("aluno.teste").roles("ALUNO")).with(csrf()))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/empresas").with(user("empresa.teste").roles("EMPRESA")).with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    void encerrarSessaoExigeCsrfERedirecionaParaLogin() throws Exception {
        mockMvc.perform(post("/logout").with(user("aluno.teste").roles("ALUNO")).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?logout"));
    }

}
