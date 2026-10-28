package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.service.*;
import br.edu.pucminas.moedaestudantil.web.api.dto.*;
import br.edu.pucminas.moedaestudantil.web.form.AlunoForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
public class AlunoApiController {
    private final AlunoService alunos;
    private final SessaoService sessao;
    private final Validator validator;

    public AlunoApiController(AlunoService alunos, SessaoService sessao, Validator validator) {
        this.alunos = alunos; this.sessao = sessao; this.validator = validator;
    }
    @GetMapping
    public List<AlunoDto> listar() { return List.of(AlunoDto.from(sessao.aluno())); }
    @GetMapping("/{id}")
    public AlunoDto buscar(@PathVariable Long id) {
        sessao.exigirProprietario(id);
        return AlunoDto.from(sessao.aluno());
    }
    @PostMapping
    public ResponseEntity<AlunoDto> criar(@RequestBody SalvarAlunoRequest req) {
        AlunoForm form = formulario(req);
        validar(form);
        Aluno aluno = alunos.criar(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(AlunoDto.from(aluno));
    }
    @PutMapping("/{id}")
    public AlunoDto atualizar(@PathVariable Long id, @RequestBody SalvarAlunoRequest req) {
        sessao.exigirProprietario(id);
        sessao.aluno();
        AlunoForm form = formulario(req);
        validar(form);
        return AlunoDto.from(alunos.atualizar(id, form));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        sessao.exigirProprietario(id);
        sessao.aluno();
        if (alunos.buscar(id).isAtivo()) alunos.alternarSituacao(id);
        return ResponseEntity.noContent().build();
    }
    private void validar(AlunoForm form) {
        Set<ConstraintViolation<AlunoForm>> erros = validator.validate(form);
        if (!erros.isEmpty()) throw new IllegalArgumentException(erros.iterator().next().getMessage());
    }
    private AlunoForm formulario(SalvarAlunoRequest req) {
        AlunoForm f = new AlunoForm();
        f.setNome(req.nome()); f.setEmail(req.email()); f.setCpf(req.cpf());
        f.setRg(req.rg()); f.setEndereco(req.endereco()); f.setCurso(req.curso());
        f.setInstituicaoId(req.instituicaoId());
        // Campos antigos de login e senha não são aplicados aos alunos.
        return f;
    }
}
