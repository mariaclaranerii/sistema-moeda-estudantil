package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.Transacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findAllByOrderByDataHoraDesc();
    List<Transacao> findAllByAlunoIdOrderByDataHoraDesc(Long id);
    List<Transacao> findAllByProfessorIdOrderByDataHoraDesc(Long id);
    List<Transacao> findAllByEmpresaIdOrderByDataHoraDesc(Long id);
}
