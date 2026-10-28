package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.CreditoSemestral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditoSemestralRepository extends JpaRepository<CreditoSemestral, Long> {
    boolean existsByProfessorIdAndSemestre(Long professorId, String semestre);
}
