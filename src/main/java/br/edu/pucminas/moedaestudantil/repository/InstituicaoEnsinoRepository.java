package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstituicaoEnsinoRepository extends JpaRepository<InstituicaoEnsino, Long> {

    List<InstituicaoEnsino> findAllByOrderByNomeAsc();
}
