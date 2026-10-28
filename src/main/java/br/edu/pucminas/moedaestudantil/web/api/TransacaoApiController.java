package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.service.MovimentacaoService;
import br.edu.pucminas.moedaestudantil.web.api.dto.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoApiController {
    private final MovimentacaoService movimentacoes;
    public TransacaoApiController(MovimentacaoService movimentacoes) { this.movimentacoes = movimentacoes; }

    @GetMapping
    public List<TransacaoDto> listar() {
        return movimentacoes.extrato().stream().map(TransacaoDto::from).toList();
    }
    @PostMapping("/enviar")
    public ResponseEntity<TransacaoDto> enviar(@RequestBody EnviarMoedasRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TransacaoDto.from(movimentacoes.enviar(request.alunoId(), request.valor(), request.motivo())));
    }
    @PostMapping("/resgatar")
    public ResponseEntity<TransacaoDto> resgatar(@RequestBody ResgatarVantagemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TransacaoDto.from(movimentacoes.resgatar(request.vantagemId())));
    }
}
