package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.service.EmpresaParceiraService;
import br.edu.pucminas.moedaestudantil.web.api.dto.EmpresaDto;
import br.edu.pucminas.moedaestudantil.web.api.dto.SalvarEmpresaRequest;
import br.edu.pucminas.moedaestudantil.web.form.EmpresaParceiraForm;
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
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaParceiraApiController {

    private final EmpresaParceiraService empresaService;
    private final EmpresaParceiraRepository empresaRepository;

    public EmpresaParceiraApiController(EmpresaParceiraService empresaService,
                                        EmpresaParceiraRepository empresaRepository) {
        this.empresaService = empresaService;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping
    public List<EmpresaDto> listar() {
        return empresaService.listar().stream()
                .map(EmpresaDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDto> buscar(@PathVariable Long id) {
        EmpresaParceira empresa = empresaService.buscar(id);
        return ResponseEntity.ok(EmpresaDto.from(empresa));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EmpresaDto> criar(@RequestBody SalvarEmpresaRequest request) {
        EmpresaParceiraForm form = preencherForm(request, null);
        EmpresaParceira empresa = empresaService.criar(form);
        atualizarCamposExtras(empresa, request);
        empresa = empresaRepository.save(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmpresaDto.from(empresa));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<EmpresaDto> atualizar(@PathVariable Long id, @RequestBody SalvarEmpresaRequest request) {
        EmpresaParceiraForm form = preencherForm(request, id);
        EmpresaParceira empresa = empresaService.atualizar(id, form);
        atualizarCamposExtras(empresa, request);
        empresa = empresaRepository.save(empresa);
        return ResponseEntity.ok(EmpresaDto.from(empresa));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (empresaRepository.existsById(id)) {
            empresaRepository.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }

    private EmpresaParceiraForm preencherForm(SalvarEmpresaRequest req, Long idExistente) {
        EmpresaParceiraForm form = new EmpresaParceiraForm();
        form.setNome(req.obterNome());
        form.setEmail(req.email() != null ? req.email() : "");

        String login = req.login();
        if (login == null || login.isBlank()) {
            String base = (req.email() != null && req.email().contains("@"))
                    ? req.email().split("@")[0].replaceAll("[^A-Za-z0-9._-]", "")
                    : "";
            if (base.length() < 4) {
                base = "empresa." + (base.isBlank() ? System.currentTimeMillis() : base);
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

    private void atualizarCamposExtras(EmpresaParceira empresa, SalvarEmpresaRequest req) {
        if (req.cnpj() != null) empresa.setCnpj(req.cnpj());
        if (req.razaoSocial() != null) empresa.setRazaoSocial(req.razaoSocial());
        if (req.endereco() != null) empresa.setEndereco(req.endereco());
        if (req.vantagens() != null) empresa.setVantagens(req.vantagens());
    }
}
