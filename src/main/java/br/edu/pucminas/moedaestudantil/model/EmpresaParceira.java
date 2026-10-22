package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas_parceiras")
public class EmpresaParceira extends Usuario {

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(length = 20)
    private String cnpj;

    @Column(name = "razao_social", length = 160)
    private String razaoSocial;

    @Column(length = 240)
    private String endereco;

    @Column(length = 1000)
    private String vantagens;

    protected EmpresaParceira() {
    }

    public EmpresaParceira(String nome, String login, String senhaHash, String email) {
        super(nome, login, senhaHash);
        this.email = email;
    }

    public EmpresaParceira(String nome, String login, String senhaHash, String email,
                           String cnpj, String razaoSocial, String endereco, String vantagens) {
        super(nome, login, senhaHash);
        this.email = email;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.endereco = endereco;
        this.vantagens = vantagens;
    }

    public void atualizarCadastro(String nome, String login, String email) {
        atualizarDadosBasicos(nome, login);
        this.email = email;
    }

    public void atualizarCompleto(String nome, String login, String email,
                                  String cnpj, String razaoSocial, String endereco, String vantagens) {
        atualizarDadosBasicos(nome, login);
        this.email = email;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.endereco = endereco;
        this.vantagens = vantagens;
    }

    public String getEmail() {
        return email;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getVantagens() {
        return vantagens;
    }

    public void setVantagens(String vantagens) {
        this.vantagens = vantagens;
    }
}
