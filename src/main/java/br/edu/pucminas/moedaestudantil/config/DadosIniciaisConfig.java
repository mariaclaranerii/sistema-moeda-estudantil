package br.edu.pucminas.moedaestudantil.config;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.model.Transacao;
import br.edu.pucminas.moedaestudantil.model.Professor;
import br.edu.pucminas.moedaestudantil.repository.ProfessorRepository;
import br.edu.pucminas.moedaestudantil.service.CreditoSemestralService;
import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.repository.TransacaoRepository;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DadosIniciaisConfig {

    @Bean
    ApplicationRunner cadastrarInstituicoesIniciais(InstituicaoEnsinoRepository repository) {
        return (ApplicationArguments ignored) -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new InstituicaoEnsino("PUC Minas - Coração Eucarístico"),
                        new InstituicaoEnsino("PUC Minas - Praça da Liberdade"),
                        new InstituicaoEnsino("PUC Minas - Barreiro")
                ));
            }
        };
    }

    @Bean
    @Profile("demo")
    ApplicationRunner cadastrarDadosDemonstracao(
            AlunoRepository alunoRepository,
            EmpresaParceiraRepository empresaRepository,
            InstituicaoEnsinoRepository instituicaoRepository,
            TransacaoRepository transacaoRepository,
            VantagemRepository vantagemRepository,
            PasswordEncoder passwordEncoder, ProfessorRepository professorRepository,
            CreditoSemestralService creditoSemestralService) {
        return (ApplicationArguments ignored) -> {
            List<InstituicaoEnsino> instituicoes = instituicaoRepository.findAll();
            if (instituicoes.isEmpty()) {
                return;
            }
            InstituicaoEnsino inst1 = instituicoes.get(0);
            InstituicaoEnsino inst2 = instituicoes.size() > 1 ? instituicoes.get(1) : inst1;

            if (alunoRepository.count() == 0) {
                String senhaPadrao = passwordEncoder.encode("12345678");

                Aluno a1 = new Aluno("Ana Silva", "ana.silva", senhaPadrao,
                        "ana.silva@aluno.pucminas.br", "12345678901", "MG-12.345.678",
                        "Rua Claudio Manoel, 100 - BH", "Engenharia de Software", inst1);
                a1.setSaldo(350);

                Aluno a2 = new Aluno("Lucas Oliveira", "lucas.oliveira", senhaPadrao,
                        "lucas.o@aluno.pucminas.br", "98765432102", "MG-98.765.432",
                        "Av. Afonso Pena, 500 - BH", "Ciência da Computação", inst2);
                a2.setSaldo(120);

                Aluno a3 = new Aluno("Mariana Costa", "mariana.costa", senhaPadrao,
                        "mariana.costa@aluno.pucminas.br", "45678912303", "MG-45.678.901",
                        "Rua dos Goitacazes, 30 - BH", "Sistemas de Informação", inst1);
                a3.setSaldo(500);

                alunoRepository.saveAll(List.of(a1, a2, a3));
            }

            if (empresaRepository.count() == 0) {
                String senhaPadrao = passwordEncoder.encode("12345678");

                EmpresaParceira e1 = new EmpresaParceira(
                        "Livraria Acadêmica", "livraria.academica", senhaPadrao,
                        "contato@livrariaacademica.com", "12.345.678/0001-90",
                        "Livraria Acadêmica LTDA", "Rua da Bahia, 1200 - Centro",
                        "Desconto de 20% em Livros Didáticos e 15% em Material de Papelaria."
                );

                EmpresaParceira e2 = new EmpresaParceira(
                        "TechBytes Informática", "techbytes", senhaPadrao,
                        "parcerias@techbytes.com.br", "98.765.432/0001-10",
                        "TechBytes Comercio de Informatica SA", "Av. do Contorno, 3000 - Savassi",
                        "Cupom de R$ 50,00 em Acessórios Gamer e Manutenção de Notebooks."
                );

                EmpresaParceira e3 = new EmpresaParceira(
                        "Café & Estudo", "cafe.estudo", senhaPadrao,
                        "cafe@estudo.com.br", "45.678.901/0001-55",
                        "Cafeteria Estudantil EIRELI", "Praça da Liberdade, 40 - BH",
                        "Vale Café Expresso + Pão de Queijo Duplo por apenas 30 moedas."
                );

                empresaRepository.saveAll(List.of(e1, e2, e3));
            }

            if (professorRepository.count() == 0) {
                String senhaProfessor = passwordEncoder.encode("12345678");
                professorRepository.saveAll(List.of(
                    new Professor("Carlos Eduardo", "prof.carlos", senhaProfessor,
                        "11122233344", "Engenharia de Software", inst1),
                    new Professor("Beatriz Lima", "prof.beatriz", senhaProfessor,
                        "44455566677", "Computação", inst2)
                ));
            }
            creditoSemestralService.creditarSemestreAtual();

            if (vantagemRepository.count() == 0) {
                vantagemRepository.saveAll(List.of(
                        new Vantagem(1L, "Livraria Acadêmica", "Desconto 20% em Livros", 150,
                                "Válido para qualquer livro técnico ou didático na loja física ou online.", "fa-book-open"),
                        new Vantagem(2L, "TechBytes Informática", "Cupom de R$ 50 em Periféricos", 300,
                                "Válido para teclados, mouses, headsets e acessórios diversos.", "fa-laptop-code"),
                        new Vantagem(3L, "Café & Estudo", "Combo Café + Pão de Queijo", 30,
                                "Apresente o código gerado no caixa do estabelecimento.", "fa-mug-hot")
                ));
            }

            if (transacaoRepository.count() == 0) {
                transacaoRepository.saveAll(List.of(
                        new Transacao("ENVIO_PROFESSOR", "Prof. Carlos Eduardo", "Ana Silva",
                                "Excelente nota no trabalho de arquitetura", 100, LocalDateTime.now().minusHours(24)),
                        new Transacao("RESGATE_VANTAGEM", "Ana Silva", "Café & Estudo",
                                "Resgate: Vale Café Expresso + Pão de Queijo", 30, LocalDateTime.now().minusHours(12)),
                        new Transacao("ENVIO_PROFESSOR", "Profa. Beatriz Lima", "Mariana Costa",
                                "Monitoria destacada na disciplina de Algoritmos", 200, LocalDateTime.now().minusHours(2))
                ));
            }
        };
    }
}
