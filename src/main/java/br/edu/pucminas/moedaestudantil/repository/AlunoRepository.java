package br.edu.pucminas.moedaestudantil.repository;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Override
    @EntityGraph(attributePaths = "instituicao")
    List<Aluno> findAll();

    @EntityGraph(attributePaths = "instituicao")
    Optional<Aluno> findOneById(Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByRgIgnoreCase(String rg);

    boolean existsByRgIgnoreCaseAndIdNot(String rg, Long id);
}
