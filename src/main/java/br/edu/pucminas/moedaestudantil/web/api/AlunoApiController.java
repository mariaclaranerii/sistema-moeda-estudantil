package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.service.AlunoService;
import br.edu.pucminas.moedaestudantil.web.api.dto.AlunoDto;
import br.edu.pucminas.moedaestudantil.web.api.dto.SalvarAlunoRequest;
import br.edu.pucminas.moedaestudantil.web.form.AlunoForm;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
public class AlunoApiController {

    private final AlunoService alunoService;
    private final AlunoRepository alunoRepository;
    private final InstituicaoEnsinoRepository instituicaoRepository;

    public AlunoApiController(AlunoService alunoService, AlunoRepository alunoRepository,
                              InstituicaoEnsinoRepository instituicaoRepository) {
        this.alunoService = alunoService;
        this.alunoRepository = alunoRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    @GetMapping
    public List<AlunoDto> listar() {
        return alunoService.listar().stream()
                .map(AlunoDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoDto> buscar(@PathVariable Long id) {
        Aluno aluno = alunoService.buscar(id);
        return ResponseEntity.ok(AlunoDto.from(aluno));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<AlunoDto> criar(@RequestBody SalvarAlunoRequest request) {
        AlunoForm form = preencherForm(request, null);
        Aluno aluno = alunoService.criar(form);
        if (request.saldo() != null && request.saldo() > 0) {
            aluno.setSaldo(request.saldo());
            aluno = alunoRepository.save(aluno);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(AlunoDto.from(aluno));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<AlunoDto> atualizar(@PathVariable Long id, @RequestBody SalvarAlunoRequest request) {
        AlunoForm form = preencherForm(request, id);
        Aluno aluno = alunoService.atualizar(id, form);
        if (request.saldo() != null) {
            aluno.setSaldo(request.saldo());
            aluno = alunoRepository.save(aluno);
        }
        return ResponseEntity.ok(AlunoDto.from(aluno));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (alunoRepository.existsById(id)) {
            alunoRepository.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }

    private AlunoForm preencherForm(SalvarAlunoRequest req, Long idExistente) {
        AlunoForm form = new AlunoForm();
        form.setNome(req.nome() != null ? req.nome() : "");
        form.setEmail(req.email() != null ? req.email() : "");
        form.setCpf(req.cpf() != null ? req.cpf() : "");
        form.setRg(req.rg() != null ? req.rg() : "");
        form.setEndereco(req.endereco() != null ? req.endereco() : "");
        form.setCurso(req.curso() != null ? req.curso() : "");

        Long instId = req.instituicaoId();
        if (instId == null) {
            List<InstituicaoEnsino> insts = instituicaoRepository.findAll();
            if (!insts.isEmpty()) {
                instId = insts.get(0).getId();
            }
        }
        form.setInstituicaoId(instId);

        String login = req.login();
        if (login == null || login.isBlank()) {
            String base = (req.email() != null && req.email().contains("@"))
                    ? req.email().split("@")[0].replaceAll("[^A-Za-z0-9._-]", "")
                    : "";
            if (base.length() < 4) {
                base = "aluno." + (base.isBlank() ? System.currentTimeMillis() : base);
            }
            login = base;
        }
        form.setLogin(login);

        String senha = req.senha();
        if (senha == null || senha.isBlank()) {
            senha = idExistente == null ? "12345678" : "";
        }
        form.setSenha(senha);

        return form;
    }
}
