package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.*;
import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditoSemestralService {
    private final ProfessorRepository professores;
    private final CreditoSemestralRepository creditos;
    private final TransacaoRepository transacoes;
    public CreditoSemestralService(ProfessorRepository professores, CreditoSemestralRepository creditos,
                                   TransacaoRepository transacoes) {
        this.professores = professores;
        this.creditos = creditos;
        this.transacoes = transacoes;
    }
    @Scheduled(cron = "0 5 0 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void creditarSemestreAtual() {
        LocalDate hoje = LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo"));
        String semestre = hoje.getYear() + "-" + (hoje.getMonthValue() <= 6 ? "1" : "2");
        for (Professor professor : professores.findAll()) {
            if (!professor.isAtivo() || creditos.existsByProfessorIdAndSemestre(professor.getId(), semestre)) continue;
            professor.creditar(1000);
            professores.save(professor);
            creditos.save(new CreditoSemestral(professor, semestre, 1000));
            Transacao transacao = new Transacao("CREDITO_SEMESTRAL", "Sistema",
                professor.getNome(), "Crédito semestral " + semestre, 1000);
            transacao.vincularCredito(professor.getId());
            transacoes.save(transacao);
        }
    }
}
