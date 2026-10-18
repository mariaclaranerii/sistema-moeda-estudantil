package br.edu.pucminas.moedaestudantil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.web.form.AlunoForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AlunoServiceIntegrationTest {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private InstituicaoEnsinoRepository instituicaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarAlunoComDadosNormalizadosESenhaProtegida() {
        AlunoForm form = novoFormulario("Aluno.Um", "aluno@exemplo.com", "123.456.789-01");

        Aluno aluno = alunoService.criar(form);

        assertThat(aluno.getId()).isNotNull();
        assertThat(aluno.getLogin()).isEqualTo("aluno.um");
        assertThat(aluno.getCpf()).isEqualTo("12345678901");
        assertThat(aluno.isAtivo()).isTrue();
        assertThat(aluno.getSenhaHash()).isNotEqualTo("segredo123");
        assertThat(passwordEncoder.matches("segredo123", aluno.getSenhaHash())).isTrue();
    }

    @Test
    void deveImpedirCpfDuplicado() {
        alunoService.criar(novoFormulario("primeiro", "primeiro@exemplo.com", "12345678901"));

        AlunoForm duplicado = novoFormulario("segundo", "segundo@exemplo.com", "123.456.789-01");

        assertThatThrownBy(() -> alunoService.criar(duplicado))
                .isInstanceOf(ConflitoCadastroException.class)
                .hasMessage("Este CPF já está cadastrado.");
    }

    @Test
    void deveAtualizarSemTrocarSenhaEDesativarLogicamente() {
        Aluno aluno = alunoService.criar(novoFormulario("aluno.tres", "tres@exemplo.com", "98765432100"));
        String senhaAnterior = aluno.getSenhaHash();
        AlunoForm atualizacao = AlunoForm.from(aluno);
        atualizacao.setNome("Nome Atualizado");

        Aluno atualizado = alunoService.atualizar(aluno.getId(), atualizacao);
        alunoService.alternarSituacao(aluno.getId());

        assertThat(atualizado.getNome()).isEqualTo("Nome Atualizado");
        assertThat(atualizado.getSenhaHash()).isEqualTo(senhaAnterior);
        assertThat(atualizado.isAtivo()).isFalse();
    }

    private AlunoForm novoFormulario(String login, String email, String cpf) {
        InstituicaoEnsino instituicao = instituicaoRepository.findAllByOrderByNomeAsc().get(0);
        AlunoForm form = new AlunoForm();
        form.setNome("Maria Estudante");
        form.setEmail(email);
        form.setCpf(cpf);
        form.setRg("MG-12.345.678");
        form.setEndereco("Rua do Conhecimento, 100");
        form.setCurso("Engenharia de Software");
        form.setLogin(login);
        form.setSenha("segredo123");
        form.setInstituicaoId(instituicao.getId());
        return form;
    }
}
