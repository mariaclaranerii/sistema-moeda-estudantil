package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.Vantagem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {

    List<Vantagem> findAllByOrderByTituloAsc();
    List<Vantagem> findByEmpresaIdOrderByTituloAsc(Long empresaId);
    Optional<Vantagem> findFirstByEmpresaIdAndEmpresaNomeAndTitulo(Long empresaId, String empresaNome, String titulo);
}
