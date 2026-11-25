package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.service.SessaoService;
import br.edu.pucminas.moedaestudantil.service.VantagemService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vantagens")
public class VantagemApiController {
    private final VantagemService vantagens;
    private final SessaoService sessao;
    public VantagemApiController(VantagemService vantagens, SessaoService sessao) {
        this.vantagens = vantagens;
        this.sessao = sessao;
    }
    public record CadastroVantagem(String titulo, Integer custoMoedas, String descricao, String fotoReferencia) {}
    @GetMapping
    public List<Vantagem> listar() {
        return vantagens.listarPara(sessao.usuario());
    }
    @PostMapping
    public ResponseEntity<Vantagem> criar(@RequestBody CadastroVantagem req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vantagens.cadastrar(
            req.titulo(), req.custoMoedas(), req.descricao(), req.fotoReferencia()));
    }

    @PutMapping("/{id}")
    public Vantagem editar(@PathVariable Long id, @RequestBody CadastroVantagem req) {
        return vantagens.atualizar(id, req.titulo(), req.custoMoedas(),
            req.descricao(), req.fotoReferencia());
    }

    @PostMapping("/{id}/situacao")
    public Vantagem alterarSituacao(@PathVariable Long id) {
        return vantagens.alternarSituacao(id);
    }
}
