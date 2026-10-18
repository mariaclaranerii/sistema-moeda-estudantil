package br.edu.pucminas.moedaestudantil.config;

import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DadosIniciaisConfig {

    @Bean
    ApplicationRunner cadastrarInstituicoesIniciais(InstituicaoEnsinoRepository repository) {
        return (ApplicationArguments ignored) -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new InstituicaoEnsino("PUC Minas"),
                        new InstituicaoEnsino("Universidade Federal de Minas Gerais"),
                        new InstituicaoEnsino("Centro Federal de Educação Tecnológica de Minas Gerais")
                ));
            }
        };
    }
}
