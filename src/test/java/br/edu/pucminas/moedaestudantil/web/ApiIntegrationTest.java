package br.edu.pucminas.moedaestudantil.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.model.Transacao;
import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.repository.TransacaoRepository;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InstituicaoEnsinoRepository instituicaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EmpresaParceiraRepository empresaRepository;

    @Autowired
    private VantagemRepository vantagemRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveListarInstituicoesViaRest() throws Exception {
        mockMvc.perform(get("/api/instituicoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").exists());
    }

    @Test
    void deveExecutarCicloCompletoAlunoViaRest() throws Exception {
        MockHttpSession admin = login("admin", "admin");
        InstituicaoEnsino inst = instituicaoRepository.findAllByOrderByNomeAsc().get(0);

        String jsonCriar = """
                {
                    "nome": "Carlos Drummond",
                    "email": "carlos.drummond@email.com",
                    "cpf": "11122233399",
                    "rg": "MG-99.888.777",
                    "endereco": "Rua das Letras, 42",
                    "curso": "Letras",
                    "instituicaoId": %d,
                    "saldo": 100
                }
                """.formatted(inst.getId());

        String resposta = mockMvc.perform(post("/api/alunos").session(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCriar))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Carlos Drummond"))
                .andExpect(jsonPath("$.saldo").value(100))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/alunos").session(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        Long alunoId = alunoRepository.findAll().get(0).getId();

        String jsonAtualizar = """
                {
                    "nome": "Carlos Drummond Atualizado",
                    "email": "carlos.drummond@email.com",
                    "cpf": "11122233399",
                    "rg": "MG-99.888.777",
                    "endereco": "Rua das Letras, 42",
                    "curso": "Letras",
                    "instituicaoId": %d,
                    "saldo": 150
                }
                """.formatted(inst.getId());

        mockMvc.perform(put("/api/alunos/" + alunoId).session(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizar))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Drummond Atualizado"))
                .andExpect(jsonPath("$.saldo").value(150));

        mockMvc.perform(delete("/api/alunos/" + alunoId).session(admin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveGerarLoginsDistintosParaEmailsComMesmoPrefixo() throws Exception {
        for (int i = 1; i <= 2; i++) {
            mockMvc.perform(post("/api/empresas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nomeFantasia":"Empresa %d","email":"contato.regressao@empresa%d.com","senha":"senha-forte123"}
                                    """.formatted(i, i)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.login").value(i == 1 ? "contato.regressao" : "contato.regressao.2"));
        }
    }

    @Test
    void devePreservarLoginAoEditarEmailSemInformarLogin() throws Exception {
        MockHttpSession admin = login("admin", "admin");
        EmpresaParceira empresa = empresaRepository.save(new EmpresaParceira(
                "Empresa Original", "login.original", "hash", "original@regressao.com"));
        mockMvc.perform(put("/api/empresas/" + empresa.getId()).session(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomeFantasia":"Empresa Atualizada","email":"novo@regressao.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login.original"))
                .andExpect(jsonPath("$.email").value("novo@regressao.com"));
    }

    @Test
    void deveRejeitarLoginExplicitoJaUtilizado() throws Exception {
        empresaRepository.save(new EmpresaParceira(
                "Empresa Existente", "login.ocupado", "hash", "existente@regressao.com"));
        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomeFantasia":"Nova Empresa","email":"nova@regressao.com","login":"LOGIN.OCUPADO","senha":"senha-forte123"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void deveExecutarCicloCompletoEmpresaViaRest() throws Exception {
        MockHttpSession admin = login("admin", "admin");
        String jsonCriar = """
                {
                    "nomeFantasia": "Papelaria Nova",
                    "razaoSocial": "Papelaria Nova LTDA",
                    "cnpj": "12.345.678/0001-99",
                    "email": "contato@papelarianova.com",
                    "endereco": "Av Central, 10",
                    "vantagens": "10% de desconto",
                    "senha": "senha-forte123"
                }
                """;

        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCriar))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nomeFantasia").value("Papelaria Nova"));

        mockMvc.perform(get("/api/empresas").session(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        Long empresaId = empresaRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/empresas/" + empresaId).session(admin))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveTransferirMoedasEResgatarVantagem() throws Exception {
        InstituicaoEnsino inst = instituicaoRepository.findAllByOrderByNomeAsc().get(0);
        Aluno aluno = new Aluno("Aluno Teste", "aluno.teste", passwordEncoder.encode("senha-forte123"),
                "teste.moedas@email.com", "55566677788", "MG-55.666.777",
                "Rua Teste, 1", "Engenharia", inst);
        aluno.setSaldo(50);
        aluno = alunoRepository.save(aluno);

        Vantagem vantagem = new Vantagem(1L, "Empresa Teste", "Vale Café", 30, "Desc", "fa-coffee");
        vantagem = vantagemRepository.save(vantagem);

        String jsonEnvio = """
                {
                    "alunoId": %d,
                    "valor": 100,
                    "motivo": "Bom desempenho",
                    "professor": "Prof. Dumbledore"
                }
                """.formatted(aluno.getId());

        MockHttpSession professor = login("professor", "12345678");
        MockHttpSession admin = login("admin", "admin");
        MockHttpSession sessaoAluno = login("aluno.teste", "senha-forte123");

        mockMvc.perform(get("/api/alunos").session(sessaoAluno)).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/empresas").session(sessaoAluno)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/vantagens").session(sessaoAluno)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/transacoes/enviar").session(admin)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonEnvio))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/transacoes/enviar").session(professor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEnvio))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(100))
                .andExpect(jsonPath("$.tipo").value("ENVIO_PROFESSOR"))
                .andExpect(jsonPath("$.origem").value("Prof. Carlos Eduardo"));

        String jsonResgate = """
                {
                    "alunoId": %d,
                    "vantagemId": %d
                }
                """.formatted(aluno.getId(), vantagem.getId());

        mockMvc.perform(post("/api/transacoes/resgatar").session(sessaoAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonResgate.replace("\"alunoId\": " + aluno.getId(), "\"alunoId\": " + (aluno.getId() + 1))))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/transacoes/resgatar").session(sessaoAluno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonResgate))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("RESGATE_VANTAGEM"))
                .andExpect(jsonPath("$.codigoResgate").isNotEmpty())
                .andExpect(jsonPath("$.valor").value(30));

        mockMvc.perform(get("/api/auth/me").session(sessaoAluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(120));

        mockMvc.perform(get("/api/transacoes")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/alunos").session(professor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value(aluno.getNome()));
        mockMvc.perform(delete("/api/alunos/" + aluno.getId()).session(professor))
                .andExpect(status().isForbidden());
        Transacao outroEnvio = new Transacao("ENVIO_PROFESSOR", "Outro Professor",
                aluno.getNome(), "Outro envio", 25);
        outroEnvio.vincularParticipantes(aluno.getId(), "outro.professor", null);
        transacaoRepository.save(outroEnvio);

        mockMvc.perform(get("/api/transacoes").session(professor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("ENVIO_PROFESSOR"))
                .andExpect(jsonPath("$[0].origem").value("Prof. Carlos Eduardo"));
        mockMvc.perform(get("/api/transacoes").session(sessaoAluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(post("/api/auth/logout").session(sessaoAluno)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void deveVincularNovaVantagemAEmpresaAutenticada() throws Exception {
        String senha = "senha-forte123";
        EmpresaParceira empresa = empresaRepository.save(new EmpresaParceira(
                "Empresa A", "empresa.a", passwordEncoder.encode(senha), "a@regressao.com"));
        EmpresaParceira outra = empresaRepository.save(new EmpresaParceira(
                "Empresa B", "empresa.b", passwordEncoder.encode(senha), "b@regressao.com"));
        MockHttpSession sessao = login(empresa.getLogin(), senha);
        String dados = """
                {"titulo":"Vale café","descricao":"Um café","custoMoedas":10,"empresaId":%d}
                """.formatted(outra.getId());

        mockMvc.perform(post("/api/vantagens").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON).content(dados))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.empresaId").value(empresa.getId()));
        mockMvc.perform(get("/api/vantagens").session(login(outra.getLogin(), senha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/alunos").session(sessao)).andExpect(status().isForbidden());
    }

    private MockHttpSession login(String login, String senha) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"%s\",\"senha\":\"%s\"}".formatted(login, senha)))
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession(false);
    }
}
