package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import br.edu.pucminas.moedaestudantil.service.MovimentacaoService;
import br.edu.pucminas.moedaestudantil.service.SessaoService;
import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.model.Usuario;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vantagens")
public class VantagemApiController {
    private final VantagemRepository repository;
    private final MovimentacaoService movimentacao;
    private final SessaoService sessao;
    public VantagemApiController(VantagemRepository repository, MovimentacaoService movimentacao,
                                SessaoService sessao) {
        this.repository = repository;
        this.movimentacao = movimentacao;
        this.sessao = sessao;
    }
    public record CadastroVantagem(String titulo, Integer custoMoedas, String descricao, String fotoReferencia) {}
    @GetMapping
    public List<Vantagem> listar() {
        Usuario usuario = sessao.usuario();
        if (usuario instanceof EmpresaParceira) {
            return repository.findByEmpresaIdOrderByTituloAsc(usuario.getId());
        }
        return repository.findAllByOrderByTituloAsc().stream().filter(Vantagem::isAtivo).toList();
    }
    @PostMapping
    public ResponseEntity<Vantagem> criar(@RequestBody CadastroVantagem req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao.cadastrarVantagem(
            req.titulo(), req.custoMoedas(), req.descricao(), req.fotoReferencia()));
    }

    @PutMapping("/{id}")
    public Vantagem editar(@PathVariable Long id, @RequestBody CadastroVantagem req) {
        return movimentacao.atualizarVantagem(id, req.titulo(), req.custoMoedas(),
            req.descricao(), req.fotoReferencia());
    }

    @PostMapping("/{id}/situacao")
    public Vantagem alterarSituacao(@PathVariable Long id) {
        return movimentacao.alternarSituacaoVantagem(id);
    }
}
