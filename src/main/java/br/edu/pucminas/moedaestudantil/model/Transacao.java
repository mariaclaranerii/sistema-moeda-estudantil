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
    private LocalDateTime dataHora;

    @Column(name = "aluno_id")
    private Long alunoId;
    @Column(name = "professor_id")
    private Long professorId;
    @Column(name = "empresa_id")
    private Long empresaId;
    @Column(name = "vantagem_id")
    private Long vantagemId;
    @Column(name = "codigo_resgate", unique = true, length = 40)
    private String codigoResgate;

    protected Transacao() {
    }

    private Transacao(String tipo, String origem, String destino, String motivo, Integer valor) {
        this(tipo, origem, destino, motivo, valor, LocalDateTime.now());
    }

    public Transacao(String tipo, String origem, String destino, String motivo, Integer valor, LocalDateTime dataHora) {
        this.tipo = tipo;
        this.origem = origem;
        this.destino = destino;
        this.motivo = motivo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public static Transacao transferencia(Professor professor, Aluno aluno, String motivo, int quantidade) {
        Transacao transacao = new Transacao("ENVIO_PROFESSOR", professor.getNome(), aluno.getNome(), motivo, quantidade);
        transacao.professorId = professor.getId();
        transacao.alunoId = aluno.getId();
        return transacao;
    }

    public static Transacao resgate(Aluno aluno, EmpresaParceira empresa, Vantagem vantagem, String codigo) {
        Transacao transacao = new Transacao("RESGATE_VANTAGEM", aluno.getNome(), empresa.getNome(),
            "Resgate: " + vantagem.getTitulo(), vantagem.getCustoMoedas());
        transacao.alunoId = aluno.getId();
        transacao.empresaId = empresa.getId();
        transacao.vantagemId = vantagem.getId();
        transacao.codigoResgate = codigo;
        return transacao;
    }

    public static Transacao creditoSemestral(Professor professor, String semestre, int quantidade) {
        Transacao transacao = new Transacao("CREDITO_SEMESTRAL", "Sistema",
            professor.getNome(), "Crédito semestral " + semestre, quantidade);
        transacao.professorId = professor.getId();
        return transacao;
    }

    public Long getAlunoId() { return alunoId; }
    public Long getProfessorId() { return professorId; }
    public Long getEmpresaId() { return empresaId; }
    public Long getVantagemId() { return vantagemId; }
    public String getCodigoResgate() { return codigoResgate; }
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
