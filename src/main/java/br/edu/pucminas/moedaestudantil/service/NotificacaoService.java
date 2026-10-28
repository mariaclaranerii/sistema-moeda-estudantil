package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.NotificacaoEmail;
import br.edu.pucminas.moedaestudantil.repository.NotificacaoEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoService {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);
    private final NotificacaoEmailRepository repository;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final boolean habilitado;
    private final String remetente;
    private final String usuarioSmtp;
    private final String chaveSmtp;

    public NotificacaoService(NotificacaoEmailRepository repository, ObjectProvider<JavaMailSender> mailSender,
                              @Value("${app.email.enabled:false}") boolean habilitado,
                              @Value("${app.email.from:}") String remetente,
                              @Value("${spring.mail.username:}") String usuarioSmtp,
                              @Value("${spring.mail.password:}") String chaveSmtp) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.habilitado = habilitado;
        this.remetente = remetente;
        this.usuarioSmtp = usuarioSmtp;
        this.chaveSmtp = chaveSmtp;
    }

    @Transactional
    public void registrar(String destinatario, String assunto, String mensagem) {
        repository.save(new NotificacaoEmail(destinatario, assunto, mensagem));
    }

    @Scheduled(fixedDelayString = "${app.email.intervalo-ms:60000}")
    @Transactional
    public void enviarPendentes() {
        if (!habilitado) return;
        // Não consumir tentativas de envio enquanto a conta Brevo não estiver configurada.
        if (remetente == null || remetente.isBlank() || usuarioSmtp == null || usuarioSmtp.isBlank()
                || chaveSmtp == null || chaveSmtp.isBlank()) {
            log.warn("Notificações pendentes: configure EMAIL_FROM, SMTP_USER e SMTP_PASSWORD para enviar.");
            return;
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.warn("Envio de e-mail habilitado sem servidor SMTP configurado.");
            return;
        }
        for (NotificacaoEmail notificacao : repository.findTop20ByEnviadaFalseAndTentativasLessThanOrderByCriadaEmAsc(5)) {
            try {
                SimpleMailMessage mensagem = new SimpleMailMessage();
                mensagem.setFrom(remetente);
                mensagem.setTo(notificacao.getDestinatario());
                mensagem.setSubject(notificacao.getAssunto());
                mensagem.setText(notificacao.getMensagem());
                sender.send(mensagem);
                notificacao.marcarEnviada();
            } catch (RuntimeException ex) {
                notificacao.marcarFalha();
                log.error("Falha no envio da notificação {}: {}", notificacao.getId(), ex.getMessage());
            }
        }
    }
}
