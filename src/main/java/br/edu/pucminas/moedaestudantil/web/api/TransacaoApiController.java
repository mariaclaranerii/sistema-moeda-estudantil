package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.Transacao;
import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.TransacaoRepository;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import br.edu.pucminas.moedaestudantil.web.api.dto.EnviarMoedasRequest;
import br.edu.pucminas.moedaestudantil.web.api.dto.ResgatarVantagemRequest;
import br.edu.pucminas.moedaestudantil.web.api.dto.TransacaoDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoApiController {

    private final TransacaoRepository transacaoRepository;
    private final AlunoRepository alunoRepository;
    private final VantagemRepository vantagemRepository;

    public TransacaoApiController(TransacaoRepository transacaoRepository,
                                  AlunoRepository alunoRepository,
                                  VantagemRepository vantagemRepository) {
        this.transacaoRepository = transacaoRepository;
        this.alunoRepository = alunoRepository;
        this.vantagemRepository = vantagemRepository;
    }

    @GetMapping
    public List<TransacaoDto> listar() {
        return transacaoRepository.findAllByOrderByDataHoraDesc().stream()
                .map(TransacaoDto::from)
                .toList();
    }

    @PostMapping("/enviar")
    @Transactional
    public ResponseEntity<TransacaoDto> enviarMoedas(@RequestBody EnviarMoedasRequest request) {
        if (request.alunoId() == null || request.valor() == null || request.valor() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos para transferência.");
        }

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));

        aluno.creditarMoedas(request.valor());
        alunoRepository.save(aluno);

        String professor = (request.professor() != null && !request.professor().isBlank())
                ? request.professor()
                : "Prof. Carlos Eduardo";

        String motivo = (request.motivo() != null && !request.motivo().isBlank())
                ? request.motivo()
                : "Reconhecimento por mérito acadêmico";

        Transacao transacao = new Transacao(
                "ENVIO_PROFESSOR",
                professor,
                aluno.getNome(),
                motivo,
                request.valor()
        );
        transacao = transacaoRepository.save(transacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(TransacaoDto.from(transacao));
    }

    @PostMapping("/resgatar")
    @Transactional
    public ResponseEntity<TransacaoDto> resgatarVantagem(@RequestBody ResgatarVantagemRequest request) {
        if (request.vantagemId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vantagem não informada.");
        }

        Vantagem vantagem = vantagemRepository.findById(request.vantagemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vantagem não encontrada."));

        Aluno aluno;
        if (request.alunoId() != null) {
            aluno = alunoRepository.findById(request.alunoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        } else {
            List<Aluno> alunos = alunoRepository.findAll();
            if (alunos.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhum aluno cadastrado para resgatar.");
            }
            aluno = alunos.get(0);
        }

        if (aluno.getSaldo() < vantagem.getCustoMoedas()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para o resgate.");
        }

        aluno.debitarMoedas(vantagem.getCustoMoedas());
        alunoRepository.save(aluno);

        Transacao transacao = new Transacao(
                "RESGATE_VANTAGEM",
                aluno.getNome(),
                vantagem.getEmpresaNome(),
                "Resgate de Código: " + vantagem.getTitulo(),
                vantagem.getCustoMoedas()
        );
        transacao = transacaoRepository.save(transacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(TransacaoDto.from(transacao));
    }
}
