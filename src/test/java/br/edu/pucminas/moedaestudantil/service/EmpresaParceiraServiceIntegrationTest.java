package br.edu.pucminas.moedaestudantil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.web.form.EmpresaParceiraForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmpresaParceiraServiceIntegrationTest {

    @Autowired
    private EmpresaParceiraService empresaService;

    @Test
    void deveExecutarCicloDoCadastroDaEmpresa() {
        EmpresaParceiraForm form = formulario("parceira", "contato@parceira.com");
        EmpresaParceira empresa = empresaService.criar(form);

        EmpresaParceiraForm atualizacao = EmpresaParceiraForm.from(empresa);
        atualizacao.setNome("Parceira Atualizada");
        EmpresaParceira atualizada = empresaService.atualizar(empresa.getId(), atualizacao);
        empresaService.alternarSituacao(empresa.getId());

        assertThat(atualizada.getNome()).isEqualTo("Parceira Atualizada");
        assertThat(atualizada.isAtivo()).isFalse();
        assertThat(empresaService.listar()).extracting(EmpresaParceira::getId).contains(empresa.getId());
    }

    @Test
    void deveManterLoginUnicoEntreTiposDeUsuario() {
        empresaService.criar(formulario("login.comum", "um@parceira.com"));

        assertThatThrownBy(() -> empresaService.criar(formulario("LOGIN.COMUM", "dois@parceira.com")))
                .isInstanceOf(ConflitoCadastroException.class)
                .hasMessage("Este login já está em uso.");
    }

    private static EmpresaParceiraForm formulario(String login, String email) {
        EmpresaParceiraForm form = new EmpresaParceiraForm();
        form.setNome("Empresa Parceira");
        form.setEmail(email);
        form.setLogin(login);
        form.setSenha("segredo123");
        return form;
    }
}
