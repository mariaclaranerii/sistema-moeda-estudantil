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
import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

    @Test
    void deveListarInstituicoesViaRest() throws Exception {
        mockMvc.perform(get("/api/instituicoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").exists());
    }

    @Test
    void deveExecutarCicloCompletoAlunoViaRest() throws Exception {
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

        String resposta = mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCriar))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Carlos Drummond"))
                .andExpect(jsonPath("$.saldo").value(100))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/alunos"))
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

        mockMvc.perform(put("/api/alunos/" + alunoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizar))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Drummond Atualizado"))
                .andExpect(jsonPath("$.saldo").value(150));

        mockMvc.perform(delete("/api/alunos/" + alunoId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveExecutarCicloCompletoEmpresaViaRest() throws Exception {
        String jsonCriar = """
                {
                    "nomeFantasia": "Papelaria Nova",
                    "razaoSocial": "Papelaria Nova LTDA",
                    "cnpj": "12.345.678/0001-99",
                    "email": "contato@papelarianova.com",
                    "endereco": "Av Central, 10",
                    "vantagens": "10% de desconto"
                }
                """;

        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCriar))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nomeFantasia").value("Papelaria Nova"));

        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        Long empresaId = empresaRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/empresas/" + empresaId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveTransferirMoedasEResgatarVantagem() throws Exception {
        InstituicaoEnsino inst = instituicaoRepository.findAllByOrderByNomeAsc().get(0);
        Aluno aluno = new Aluno("Aluno Teste", "aluno.teste", "hash",
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

        mockMvc.perform(post("/api/transacoes/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEnvio))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(100))
                .andExpect(jsonPath("$.tipo").value("ENVIO_PROFESSOR"));

        String jsonResgate = """
                {
                    "alunoId": %d,
                    "vantagemId": %d
                }
                """.formatted(aluno.getId(), vantagem.getId());

        mockMvc.perform(post("/api/transacoes/resgatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonResgate))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("RESGATE_VANTAGEM"))
                .andExpect(jsonPath("$.valor").value(30));

        mockMvc.perform(get("/api/transacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}
