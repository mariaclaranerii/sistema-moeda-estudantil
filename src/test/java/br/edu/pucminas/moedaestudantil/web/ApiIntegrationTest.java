package br.edu.pucminas.moedaestudantil.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.*;
import br.edu.pucminas.moedaestudantil.service.CreditoSemestralService;
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
class ApiIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired InstituicaoEnsinoRepository instituicoes;
    @Autowired AlunoRepository alunos;
    @Autowired ProfessorRepository professores;
    @Autowired EmpresaParceiraRepository empresas;
    @Autowired VantagemRepository vantagens;
    @Autowired TransacaoRepository transacoes;
    @Autowired NotificacaoEmailRepository emails;
    @Autowired PasswordEncoder encoder;
    @Autowired CreditoSemestralService creditos;

    @Test
    void cadastroDeAlunoPelaApiEConsultaRestrita() throws Exception {
        long instituicaoId = instituicoes.findAll().get(0).getId();
        String payload = """
            {"nome":"Aluno Exemplo","email":"aluno@exemplo.com","login":"aluno.teste",
            "senha":"segredo123","cpf":"11122233344","rg":"MG-123456","endereco":"Rua A, 1",
            "curso":"Software","instituicaoId":%d,"saldo":1000}
            """.formatted(instituicaoId);

        mockMvc.perform(post("/api/alunos").with(csrf()).with(user("prof.cadastro").roles("PROFESSOR"))
            .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.saldo").value(0));
        mockMvc.perform(get("/api/alunos")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/api/alunos").with(user("aluno@exemplo.com").roles("ALUNO")))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/perfil/senha").with(user("aluno@exemplo.com").roles("ALUNO"))
            .with(csrf()).param("senhaAtual", "Aluno@2026!")
            .param("novaSenha", "NovaSenha@2026").param("confirmacao", "NovaSenha@2026"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/api/alunos").with(user("aluno@exemplo.com").roles("ALUNO")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].login").value("aluno@exemplo.com"));
    }

    @Test
    void envioDebitaProfessorCreditaAlunoEExigeMotivo() throws Exception {
        InstituicaoEnsino i = instituicoes.findAll().get(0);
        Professor professor = professores.save(new Professor("Prof. Teste","prof.teste",
            encoder.encode("segredo123"),"11122233344","Software",i));
        Aluno aluno = alunos.save(new Aluno("Aluno Um","aluno.um",
            encoder.encode("segredo123"),"um@exemplo.com","44455566677",
            "MG-98765","Rua B, 3","Software",i));
        creditos.creditarSemestreAtual();
        assertThat(professores.findById(professor.getId()).orElseThrow().getSaldo()).isEqualTo(1000);

        String json = """
            {"alunoId":%d,"valor":150,"motivo":"Participação destacada"}
            """.formatted(aluno.getId());
        mockMvc.perform(post("/api/transacoes/enviar").with(csrf())
            .with(user("prof.teste").roles("PROFESSOR"))
            .contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.valor").value(150));

        assertThat(professores.findById(professor.getId()).orElseThrow().getSaldo()).isEqualTo(850);
        assertThat(alunos.findById(aluno.getId()).orElseThrow().getSaldo()).isEqualTo(150);
        assertThat(emails.count()).isZero();
        mockMvc.perform(post("/api/transacoes/enviar").with(csrf())
            .with(user("prof.teste").roles("PROFESSOR"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"alunoId":%d,"valor":10,"motivo":""}
                """.formatted(aluno.getId())))
            .andExpect(status().isBadRequest());
        assertThat(alunos.findById(aluno.getId()).orElseThrow().getSaldo()).isEqualTo(150);
    }

    @Test
    void resgateGeraCodigoSemExigirEmail() throws Exception {
        InstituicaoEnsino i = instituicoes.findAll().get(0);
        Aluno aluno = new Aluno("Aluno Resgate","aluno.resgate",encoder.encode("segredo123"),
            "resgate@exemplo.com","77788899900","MG-333","Rua Teste","Software",i);
        aluno.setSaldo(200);
        aluno = alunos.save(aluno);
        EmpresaParceira empresa = empresas.save(new EmpresaParceira("Parceira","parceira.teste",
            encoder.encode("segredo123"),"parceira@exemplo.com"));
        Vantagem vantagem = new Vantagem(empresa.getId(),empresa.getNome(),"Livro",30,
            "Material didático","fa-book");
        vantagem.setFotoReferencia("https://example.com/livro.jpg");
        vantagem = vantagens.save(vantagem);

        mockMvc.perform(post("/api/transacoes/resgatar").with(csrf())
            .with(user("aluno.resgate").roles("ALUNO"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"vantagemId\":" + vantagem.getId() + "}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.codigoResgate").isString());
        assertThat(alunos.findById(aluno.getId()).orElseThrow().getSaldo()).isEqualTo(170);
        assertThat(emails.count()).isZero();
        assertThat(transacoes.findAllByAlunoIdOrderByDataHoraDesc(aluno.getId()).get(0)
            .getCodigoResgate()).isNotBlank();
    }

    @Test
    void bloqueiaAcessoNaoAutenticadoEProtegeRequisicoesPost() throws Exception {
        mockMvc.perform(get("/api/transacoes")).andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON)
            .content("{}")).andExpect(status().isForbidden());
    }
}
