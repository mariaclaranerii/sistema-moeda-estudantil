package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas_parceiras")
public class EmpresaParceira extends Usuario {

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    protected EmpresaParceira() {
    }

    public EmpresaParceira(String nome, String login, String senhaHash, String email) {
        super(nome, login, senhaHash);
        this.email = email;
    }

    public void atualizarCadastro(String nome, String login, String email) {
        atualizarDadosBasicos(nome, login);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
