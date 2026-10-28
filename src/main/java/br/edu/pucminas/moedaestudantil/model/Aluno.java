package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "alunos")
public class Aluno extends Usuario {

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, length = 20)
    private String rg;

    @Column(nullable = false, length = 240)
    private String endereco;

    @Column(nullable = false, length = 120)
    private String curso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEnsino instituicao;

    @Column(nullable = false)
    private Integer saldo = 0;

    // Nulo em registros anteriores equivale a senha já definida.
    @Column(name = "troca_senha_pendente")
    private Boolean trocaSenhaPendente = false;

    protected Aluno() {
    }

    public Aluno(String nome, String login, String senhaHash, String email, String cpf,
                 String rg, String endereco, String curso, InstituicaoEnsino instituicao) {
        super(nome, login, senhaHash);
        this.email = email;
        this.cpf = cpf;
        this.rg = rg;
        this.endereco = endereco;
        this.curso = curso;
        this.instituicao = instituicao;
        this.saldo = 0;
    }

    public void atualizarCadastro(String nome, String login, String email, String cpf,
                                  String rg, String endereco, String curso,
                                  InstituicaoEnsino instituicao) {
        atualizarDadosBasicos(nome, login);
        this.email = email;
        this.cpf = cpf;
        this.rg = rg;
        this.endereco = endereco;
        this.curso = curso;
        this.instituicao = instituicao;
    }

    public boolean isTrocaSenhaPendente() { return Boolean.TRUE.equals(trocaSenhaPendente); }
    public void exigirTrocaSenhaInicial() { trocaSenhaPendente = true; }
    public void confirmarTrocaSenha() { trocaSenhaPendente = false; }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRg() {
        return rg;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCurso() {
        return curso;
    }

    public InstituicaoEnsino getInstituicao() {
        return instituicao;
    }

    public Integer getSaldo() {
        return saldo == null ? 0 : saldo;
    }

    public void setSaldo(Integer saldo) {
        if (saldo != null && saldo < 0) throw new IllegalArgumentException("Saldo inválido.");
        this.saldo = saldo == null ? 0 : saldo;
    }

    public void creditarMoedas(int quantidade) {
        if (quantidade > 0) {
            this.saldo = Math.addExact(getSaldo(), quantidade);
        }
    }

    public void debitarMoedas(int quantidade) {
        if (quantidade <= 0 || getSaldo() < quantidade) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
        this.saldo = getSaldo() - quantidade;
    }
}
