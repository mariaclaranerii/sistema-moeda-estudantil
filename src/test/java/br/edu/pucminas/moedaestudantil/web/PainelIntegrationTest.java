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
            .andExpect(content().string(containsString("Cadastrar aluno")))
            .andExpect(content().string(containsString("Cadastrar empresa")))
            .andExpect(content().string(containsString("Ver alunos cadastrados")))
            .andExpect(content().string(containsString("Ver empresas cadastradas")))
            .andExpect(content().string(not(containsString(">Entrar</a>"))));
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
    void alunosEEmpresasNaoPodemAbrirOuEnviarNovosCadastros() throws Exception {
        mockMvc.perform(get("/alunos/novo").with(user("aluno.teste").roles("ALUNO")))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/empresas/nova").with(user("aluno.teste").roles("ALUNO")))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/alunos").with(user("aluno.teste").roles("ALUNO")).with(csrf()))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/empresas").with(user("empresa.teste").roles("EMPRESA")).with(csrf()))
            .andExpect(status().isForbidden());
    }


    @Test
    void visitanteNaoPodeCadastrarAlunoOuEmpresaPorMvcOuApi() throws Exception {
        mockMvc.perform(get("/alunos/novo"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/empresas/nova"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/alunos").with(csrf()))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/empresas").with(csrf()))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/api/alunos").with(csrf())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/api/empresas").with(csrf())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void professorPodeAbrirERealizarCadastros() throws Exception {
        mockMvc.perform(get("/alunos/novo").with(user("prof.cadastro").roles("PROFESSOR")))
            .andExpect(status().isOk()).andExpect(view().name("alunos/formulario"));
        mockMvc.perform(get("/empresas/nova").with(user("prof.cadastro").roles("PROFESSOR")))
            .andExpect(status().isOk()).andExpect(view().name("empresas/formulario"));

        long instituicaoId = instituicoes.findAll().get(0).getId();
        mockMvc.perform(post("/alunos").with(user("prof.cadastro").roles("PROFESSOR")).with(csrf())
            .param("nome", "Aluno cadastrado pelo professor")
            .param("email", "criado.professor@exemplo.com")
            .param("cpf", "89765432100")
            .param("rg", "MG-1974653")
            .param("endereco", "Rua Cadastro, 13")
            .param("curso", "Engenharia de Software")
            .param("login", "aluno.criado.professor")
            .param("senha", "senha12345")
            .param("instituicaoId", Long.toString(instituicaoId)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));

        mockMvc.perform(post("/empresas").with(user("prof.cadastro").roles("PROFESSOR")).with(csrf())
            .param("nome", "Empresa cadastrada pelo professor")
            .param("email", "empresa.cadastro@exemplo.com")
            .param("login", "empresa.criada.professor")
            .param("senha", "senha12345"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/painel"));
    }

    @Test
    void professorPodeCadastrarPorApiSemLiberarParaAluno() throws Exception {
        long instituicaoId = instituicoes.findAll().get(0).getId();
        String aluno = """
            {"nome":"Cadastro API", "email":"cadastro.api@exemplo.com",
             "login":"cadastro.api", "senha":"senha12345", "cpf":"65432109876",
             "rg":"MG-14578", "endereco":"Rua API, 25",
             "curso":"Software", "instituicaoId":%d}
            """.formatted(instituicaoId);
        mockMvc.perform(post("/api/alunos").with(user("prof.cadastro").roles("PROFESSOR")).with(csrf())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .content(aluno))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/api/alunos").with(user("aluno.outro").roles("ALUNO")).with(csrf())
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .content(aluno))
            .andExpect(status().isForbidden());
    }

    @Test
    void professorConsultaListasDeAlunosEEmpresasComSituacao() throws Exception {
        InstituicaoEnsino instituicao = instituicoes.findAll().get(0);
        professores.save(new Professor("Professor Listas", "prof.listas", encoder.encode("senha12345"),
            "77788899900", "Computação", instituicao));
        Aluno aluno = alunos.save(new Aluno("Aluna da Lista", "aluna.lista", encoder.encode("senha12345"),
            "aluna.lista@exemplo.com", "12345670123", "MG-92745",
            "Rua Lista, 10", "Engenharia de Software", instituicao));
        aluno.desativar();
        alunos.save(aluno);
        EmpresaParceira empresa = empresas.save(new EmpresaParceira("Empresa da Lista", "empresa.lista",
            encoder.encode("senha12345"), "empresa.lista@exemplo.com"));
        empresa.desativar();
        empresas.save(empresa);

        mockMvc.perform(get("/alunos").with(user("prof.listas").roles("PROFESSOR")))
            .andExpect(status().isOk())
            .andExpect(view().name("alunos/lista"))
            .andExpect(content().string(containsString("Alunos cadastrados")))
            .andExpect(content().string(containsString("Aluna da Lista")))
            .andExpect(content().string(containsString("Inativo")))
            .andExpect(content().string(not(containsString("MG-92745"))));

        mockMvc.perform(get("/empresas").with(user("prof.listas").roles("PROFESSOR")))
            .andExpect(status().isOk())
            .andExpect(view().name("empresas/lista"))
            .andExpect(content().string(containsString("Empresas cadastradas")))
            .andExpect(content().string(containsString("Empresa da Lista")))
            .andExpect(content().string(containsString("Inativa")));
    }

    @Test
    void listasNaoFicamDisponiveisParaOutrosPerfis() throws Exception {
        InstituicaoEnsino instituicao = instituicoes.findAll().get(0);
        alunos.save(new Aluno("Aluno Sem Listas", "aluno.sem.listas", encoder.encode("senha12345"),
            "sem.listas@exemplo.com", "23232323232", "MG-99999",
            "Rua Sem Listas, 1", "Engenharia de Software", instituicao));
        empresas.save(new EmpresaParceira("Empresa Sem Listas", "empresa.sem.listas",
            encoder.encode("senha12345"), "sem.listas.empresa@exemplo.com"));

        mockMvc.perform(get("/alunos").with(user("aluno.sem.listas").roles("ALUNO")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/alunos/*"));
        mockMvc.perform(get("/empresas").with(user("aluno.sem.listas").roles("ALUNO")))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/alunos").with(user("empresa.sem.listas").roles("EMPRESA")))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/empresas").with(user("empresa.sem.listas").roles("EMPRESA")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/empresas/*"));
        mockMvc.perform(get("/alunos")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/empresas")).andExpect(status().is3xxRedirection());
    }

    @Test
    void encerrarSessaoExigeCsrfERedirecionaParaLogin() throws Exception {
        mockMvc.perform(post("/logout").with(user("aluno.teste").roles("ALUNO")).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?logout"));
    }

}
