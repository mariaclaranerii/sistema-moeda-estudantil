package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professores")
public class Professor extends Usuario {
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    @Column(nullable = false, length = 120)
    private String departamento;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private InstituicaoEnsino instituicao;
    @Column(nullable = false)
    private Integer saldo = 0;

    protected Professor() {}

    public Professor(String nome, String login, String senhaHash, String cpf,
                     String departamento, InstituicaoEnsino instituicao) {
        super(nome, login, senhaHash);
        this.cpf = cpf;
        this.departamento = departamento;
        this.instituicao = instituicao;
    }
    public String getCpf() { return cpf; }
    public String getDepartamento() { return departamento; }
    public InstituicaoEnsino getInstituicao() { return instituicao; }
    public Integer getSaldo() { return saldo; }
    public void creditar(int valor) {
        if (valor <= 0) throw new IllegalArgumentException("Crédito inválido.");
        saldo = Math.addExact(saldo, valor);
    }
    public void debitar(int valor) {
        if (valor <= 0 || valor > saldo) throw new IllegalArgumentException("Saldo do professor insuficiente.");
        saldo -= valor;
    }
}
