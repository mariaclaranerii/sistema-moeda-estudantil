package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.config.PermissoesConfig;
import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.Transacao;
import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.TransacaoRepository;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import br.edu.pucminas.moedaestudantil.web.api.dto.EnviarMoedasRequest;
import br.edu.pucminas.moedaestudantil.web.api.dto.ResgatarVantagemRequest;
import br.edu.pucminas.moedaestudantil.web.api.dto.TransacaoDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
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

    private static final String ENVIO_PROFESSOR = "ENVIO_PROFESSOR";

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
    public List<TransacaoDto> listar(HttpServletRequest httpRequest) {
        var usuario = PermissoesConfig.usuario(httpRequest);
        List<Transacao> transacoes = switch (usuario.tipo()) {
            case ALUNO -> transacaoRepository.findByAlunoIdOrderByDataHoraDesc(usuario.id());
            case PROFESSOR -> transacaoRepository.findByProfessorLoginAndTipoOrderByDataHoraDesc(
                    usuario.login(), ENVIO_PROFESSOR);
            case EMPRESA -> transacaoRepository.findByEmpresaIdOrderByDataHoraDesc(usuario.id());
            case ADMIN -> transacaoRepository.findAllByOrderByDataHoraDesc();
        };
        return transacoes.stream()
                .map(TransacaoDto::from)
                .toList();
    }

    @PostMapping("/enviar")
    @Transactional
    public ResponseEntity<TransacaoDto> enviarMoedas(@RequestBody EnviarMoedasRequest request,
                                                      HttpServletRequest httpRequest) {
        if (request.alunoId() == null || request.valor() == null || request.valor() <= 0
                || request.motivo() == null || request.motivo().isBlank() || request.motivo().length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos para transferência.");
        }

        Aluno aluno = alunoRepository.findLockedById(request.alunoId()).filter(Aluno::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));

        aluno.creditarMoedas(request.valor());
        alunoRepository.save(aluno);

        var professor = PermissoesConfig.usuario(httpRequest);

        Transacao transacao = new Transacao(
                ENVIO_PROFESSOR,
                professor.nome(),
                aluno.getNome(),
                request.motivo().strip(),
                request.valor()
        );
        transacao.vincularParticipantes(aluno.getId(), professor.login(), null);
        transacao = transacaoRepository.save(transacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(TransacaoDto.from(transacao));
    }

    @PostMapping("/resgatar")
    @Transactional
    public ResponseEntity<TransacaoDto> resgatarVantagem(@RequestBody ResgatarVantagemRequest request,
                                                           HttpServletRequest httpRequest) {
        if (request.vantagemId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vantagem não informada.");
        }

        Vantagem vantagem = vantagemRepository.findById(request.vantagemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vantagem não encontrada."));

        Long alunoId = PermissoesConfig.usuario(httpRequest).id();
        if (request.alunoId() != null && !alunoId.equals(request.alunoId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resgate permitido apenas para a própria conta.");
        }
        Aluno aluno = alunoRepository.findLockedById(alunoId).filter(Aluno::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (vantagem.getCustoMoedas() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Custo da vantagem inválido.");
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
        transacao.vincularParticipantes(aluno.getId(), null, vantagem.getEmpresaId());
        transacao.registrarCodigoResgate("MC-" + UUID.randomUUID().toString().replace("-", "").toUpperCase(java.util.Locale.ROOT));
        transacao = transacaoRepository.save(transacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(TransacaoDto.from(transacao));
    }
}
