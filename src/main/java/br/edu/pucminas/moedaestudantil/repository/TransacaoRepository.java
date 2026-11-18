package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.Transacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findAllByOrderByDataHoraDesc();
    List<Transacao> findByAlunoIdOrderByDataHoraDesc(Long alunoId);
    List<Transacao> findByProfessorLoginAndTipoOrderByDataHoraDesc(String professorLogin, String tipo);
    List<Transacao> findByEmpresaIdOrderByDataHoraDesc(Long empresaId);
}
