package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vantagens")
@CrossOrigin(origins = "*")
public class VantagemApiController {

    private final VantagemRepository repository;

    public VantagemApiController(VantagemRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Vantagem> listar() {
        return repository.findAllByOrderByTituloAsc();
    }

    @PostMapping
    public ResponseEntity<Vantagem> criar(@RequestBody Vantagem vantagem) {
        Vantagem salva = repository.save(vantagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }
}
