package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(nullable = false, length = 120)
    private String origem;

    @Column(nullable = false, length = 120)
    private String destino;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(nullable = false)
    private Integer valor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    protected Transacao() {
    }

    public Transacao(String tipo, String origem, String destino, String motivo, Integer valor) {
        this.tipo = tipo;
        this.origem = origem;
        this.destino = destino;
        this.motivo = motivo;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public Transacao(String tipo, String origem, String destino, String motivo, Integer valor, LocalDateTime dataHora) {
        this.tipo = tipo;
        this.origem = origem;
        this.destino = destino;
        this.motivo = motivo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public String getMotivo() {
        return motivo;
    }

    public Integer getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
