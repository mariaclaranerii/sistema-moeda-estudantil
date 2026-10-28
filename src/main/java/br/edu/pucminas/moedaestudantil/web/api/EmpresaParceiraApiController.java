package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.service.*;
import br.edu.pucminas.moedaestudantil.web.api.dto.*;
import br.edu.pucminas.moedaestudantil.web.form.EmpresaParceiraForm;
import jakarta.validation.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaParceiraApiController {
    private final EmpresaParceiraService empresas;
    private final EmpresaParceiraRepository repository;
    private final SessaoService sessao;
    private final Validator validator;
    public EmpresaParceiraApiController(EmpresaParceiraService empresas,
                                        EmpresaParceiraRepository repository,
                                        SessaoService sessao, Validator validator) {
        this.empresas = empresas; this.repository = repository;
        this.sessao = sessao; this.validator = validator;
    }
    @GetMapping
    public List<EmpresaDto> listar() { return List.of(EmpresaDto.from(sessao.empresa())); }
    @GetMapping("/{id}")
    public EmpresaDto buscar(@PathVariable Long id) {
        sessao.exigirProprietario(id);
        return EmpresaDto.from(sessao.empresa());
    }
    @PostMapping
    @Transactional
    public ResponseEntity<EmpresaDto> criar(@RequestBody SalvarEmpresaRequest req) {
        EmpresaParceiraForm form = formulario(req);
        validar(form);
        EmpresaParceira empresa = empresas.criar(form);
        extras(empresa, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmpresaDto.from(repository.save(empresa)));
    }
    @PutMapping("/{id}")
    @Transactional
    public EmpresaDto atualizar(@PathVariable Long id, @RequestBody SalvarEmpresaRequest req) {
        sessao.exigirProprietario(id);
        sessao.empresa();
        EmpresaParceiraForm form = formulario(req);
        validar(form);
        EmpresaParceira empresa = empresas.atualizar(id, form);
        extras(empresa, req);
        return EmpresaDto.from(repository.save(empresa));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        sessao.exigirProprietario(id);
        sessao.empresa();
        if (empresas.buscar(id).isAtivo()) empresas.alternarSituacao(id);
        return ResponseEntity.noContent().build();
    }
    private void validar(EmpresaParceiraForm form) {
        Set<ConstraintViolation<EmpresaParceiraForm>> erros = validator.validate(form);
        if (!erros.isEmpty()) throw new IllegalArgumentException(erros.iterator().next().getMessage());
    }
    private EmpresaParceiraForm formulario(SalvarEmpresaRequest r) {
        EmpresaParceiraForm f = new EmpresaParceiraForm();
        f.setNome(r.obterNome()); f.setEmail(r.email()); f.setLogin(r.login());
        f.setSenha(r.senha() == null ? "" : r.senha()); return f;
    }
    private void extras(EmpresaParceira e, SalvarEmpresaRequest r) {
        if (r.cnpj() != null) e.setCnpj(r.cnpj());
        if (r.razaoSocial() != null) e.setRazaoSocial(r.razaoSocial());
        if (r.endereco() != null) e.setEndereco(r.endereco());
        if (r.vantagens() != null) e.setVantagens(r.vantagens());
    }
}
