package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.web.api.dto.InstituicaoDto;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instituicoes")
@CrossOrigin(origins = "*")
public class InstituicaoApiController {

    private final InstituicaoEnsinoRepository repository;

    public InstituicaoApiController(InstituicaoEnsinoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<InstituicaoDto> listar() {
        return repository.findAllByOrderByNomeAsc().stream()
                .map(InstituicaoDto::from)
                .toList();
    }
}
