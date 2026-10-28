package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import br.edu.pucminas.moedaestudantil.service.MovimentacaoService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vantagens")
public class VantagemApiController {
    private final VantagemRepository repository;
    private final MovimentacaoService movimentacao;
    public VantagemApiController(VantagemRepository repository, MovimentacaoService movimentacao) {
        this.repository = repository;
        this.movimentacao = movimentacao;
    }
    public record CadastroVantagem(String titulo, Integer custoMoedas, String descricao, String fotoReferencia) {}
    @GetMapping
    public List<Vantagem> listar() { return repository.findAllByOrderByTituloAsc(); }
    @PostMapping
    public ResponseEntity<Vantagem> criar(@RequestBody CadastroVantagem req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao.cadastrarVantagem(
            req.titulo(), req.custoMoedas(), req.descricao(), req.fotoReferencia()));
    }
}
