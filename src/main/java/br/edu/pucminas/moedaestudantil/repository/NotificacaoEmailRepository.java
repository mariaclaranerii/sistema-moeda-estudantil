package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.NotificacaoEmail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoEmailRepository extends JpaRepository<NotificacaoEmail, Long> {
    List<NotificacaoEmail> findTop20ByEnviadaFalseAndTentativasLessThanOrderByCriadaEmAsc(int maximo);
}
