package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.TipoUsuario;
import br.edu.pucminas.moedaestudantil.config.PermissoesConfig;
import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/vantagens")
@CrossOrigin(origins = "*")
public class VantagemApiController {

    private final VantagemRepository repository;
    private final EmpresaParceiraRepository empresas;

    public VantagemApiController(VantagemRepository repository, EmpresaParceiraRepository empresas) {
        this.repository = repository;
        this.empresas = empresas;
    }

    @GetMapping
    public List<Vantagem> listar(HttpServletRequest request) {
        var usuario = PermissoesConfig.usuario(request);
        if (usuario.tipo() == TipoUsuario.EMPRESA) {
            return repository.findByEmpresaIdOrderByTituloAsc(usuario.id());
        }
        return repository.findAllByOrderByTituloAsc();
    }

    @PostMapping
    public ResponseEntity<Vantagem> criar(@RequestBody NovaVantagem dados, HttpServletRequest request) {
        if (dados.titulo() == null || dados.titulo().isBlank() || dados.titulo().length() > 160
                || dados.descricao() == null || dados.descricao().isBlank() || dados.descricao().length() > 1000
                || dados.custoMoedas() == null || dados.custoMoedas() <= 0) {
            throw new IllegalArgumentException("Informe título, descrição e custo positivo.");
        }
        var empresa = empresas.findById(PermissoesConfig.usuario(request).id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var vantagem = new Vantagem(empresa.getId(), empresa.getNome(), dados.titulo().strip(),
                dados.custoMoedas(), dados.descricao().strip(), null);
        Vantagem salva = repository.save(vantagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    public record NovaVantagem(String titulo, Integer custoMoedas, String descricao) {}
}
