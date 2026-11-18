package br.edu.pucminas.moedaestudantil.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FluxoMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InstituicaoEnsinoRepository instituicaoRepository;

    @Test
    void deveAbrirPaginaInicialEListagens() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/index.html"));
        MockHttpSession admin = sessaoAdmin();
        mockMvc.perform(get("/alunos")).andExpect(status().isUnauthorized());

        mockMvc.perform(get("/alunos").session(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("alunos/lista"));

        mockMvc.perform(get("/empresas").session(admin))
                .andExpect(status().isOk())
                .andExpect(view().name("empresas/lista"));
    }

    @Test
    void deveCadastrarAlunoPeloFormularioMvc() throws Exception {
        InstituicaoEnsino instituicao = instituicaoRepository.findAllByOrderByNomeAsc().get(0);

        mockMvc.perform(post("/alunos").session(sessaoAdmin())
                        .param("nome", "Aluno do MVC")
                        .param("email", "mvc@exemplo.com")
                        .param("cpf", "11122233344")
                        .param("rg", "MG-11.222.333")
                        .param("endereco", "Rua MVC, 10")
                        .param("curso", "Engenharia de Software")
                        .param("login", "aluno.mvc")
                        .param("senha", "segredo123")
                        .param("instituicaoId", instituicao.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/alunos/*"))
                .andExpect(flash().attribute("sucesso", "Aluno cadastrado com sucesso."));
    }

    private MockHttpSession sessaoAdmin() throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"senha\":\"admin\"}"))
                .andExpect(status().isOk()).andReturn().getRequest().getSession(false);
    }
}
