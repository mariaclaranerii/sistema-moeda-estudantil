package br.edu.pucminas.moedaestudantil.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Vantagens disponíveis")));
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
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Distribuir moedas")));
    }

    @Test
    void empresaAbrePainelDeVantagens() throws Exception {
        empresas.save(new EmpresaParceira("Empresa Painel", "empresa.painel",
            encoder.encode("senha12345"), "empresa@exemplo.com"));
        mockMvc.perform(get("/painel").with(user("empresa.painel").roles("EMPRESA")))
            .andExpect(status().isOk())
            .andExpect(view().name("painel"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Cadastrar vantagem")));
    }
}
