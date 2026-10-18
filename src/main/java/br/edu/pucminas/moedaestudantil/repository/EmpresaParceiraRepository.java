package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaParceiraRepository extends JpaRepository<EmpresaParceira, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
