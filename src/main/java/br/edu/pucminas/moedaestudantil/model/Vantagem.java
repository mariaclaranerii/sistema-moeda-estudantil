package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vantagens")
public class Vantagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "empresa_nome", nullable = false, length = 120)
    private String empresaNome;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(name = "custo_moedas", nullable = false)
    private Integer custoMoedas;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 60)
    private String icone;

    @Column(name = "foto_referencia", length = 1000)
    private String fotoReferencia;

    protected Vantagem() {
    }

    public Vantagem(Long empresaId, String empresaNome, String titulo, Integer custoMoedas, String descricao, String icone) {
        this.empresaId = empresaId;
        this.empresaNome = empresaNome;
        this.titulo = titulo;
        this.custoMoedas = custoMoedas;
        this.descricao = descricao;
        this.icone = icone;
    }

    public Long getId() {
        return id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public String getTitulo() {
        return titulo;
    }

    public Integer getCustoMoedas() {
        return custoMoedas;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getIcone() {
        return icone;
    }
}
