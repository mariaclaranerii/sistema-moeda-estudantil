package br.edu.pucminas.moedaestudantil.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes_email")
public class NotificacaoEmail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String destinatario;
    @Column(nullable = false, length = 200)
    private String assunto;
    @Column(nullable = false, length = 3000)
    private String mensagem;
    @Column(nullable = false)
    private boolean enviada;
    @Column(nullable = false)
    private int tentativas;
    @Column(nullable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();
    protected NotificacaoEmail() {}
    public NotificacaoEmail(String destinatario, String assunto, String mensagem) {
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.mensagem = mensagem;
    }
    public Long getId() { return id; }
    public String getDestinatario() { return destinatario; }
    public String getAssunto() { return assunto; }
    public String getMensagem() { return mensagem; }
    public boolean isEnviada() { return enviada; }
    public void marcarEnviada() { enviada = true; tentativas++; }
    public void marcarFalha() { tentativas++; }
}
