package br.edu.pucminas.moedaestudantil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import br.edu.pucminas.moedaestudantil.model.NotificacaoEmail;
import br.edu.pucminas.moedaestudantil.repository.NotificacaoEmailRepository;

class NotificacaoServiceTest {
    private final NotificacaoEmailRepository repository = mock(NotificacaoEmailRepository.class);
    private final JavaMailSender sender = mock(JavaMailSender.class);
    @SuppressWarnings("unchecked")
    private final ObjectProvider<JavaMailSender> mailProvider = mock(ObjectProvider.class);

    private NotificacaoService service(boolean enabled, String from, String login, String secret) {
        return new NotificacaoService(repository, mailProvider, enabled, from, login, secret);
    }

    @Test
    void naoEnviaQuandoDesabilitadoOuSemCredenciais() {
        service(false, "remetente@exemplo.com", "login", "chave").enviarPendentes();
        service(true, "", "login", "chave").enviarPendentes();
        service(true, "remetente@exemplo.com", "", "chave").enviarPendentes();
        service(true, "remetente@exemplo.com", "login", "").enviarPendentes();
        verifyNoInteractions(repository, mailProvider, sender);
    }

    @Test
    void enviaMensagemPendenteComRemetenteEConteudoCorretos() {
        NotificacaoEmail aviso = new NotificacaoEmail("aluno@exemplo.com", "Moedas recebidas", "Você recebeu moedas.");
        when(mailProvider.getIfAvailable()).thenReturn(sender);
        when(repository.findTop20ByEnviadaFalseAndTentativasLessThanOrderByCriadaEmAsc(5))
            .thenReturn(List.of(aviso));

        service(true, "remetente@exemplo.com", "login-smtp", "chave-smtp").enviarPendentes();

        var captor = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        SimpleMailMessage mensagem = captor.getValue();
        assertThat(mensagem.getFrom()).isEqualTo("remetente@exemplo.com");
        assertThat(mensagem.getTo()).containsExactly("aluno@exemplo.com");
        assertThat(mensagem.getSubject()).isEqualTo("Moedas recebidas");
        assertThat(mensagem.getText()).isEqualTo("Você recebeu moedas.");
        assertThat(aviso.isEnviada()).isTrue();
    }

    @Test
    void falhaNoSmtpNaoMarcaNotificacaoComoEnviada() {
        NotificacaoEmail aviso = new NotificacaoEmail("aluno@exemplo.com", "Assunto", "Corpo");
        when(mailProvider.getIfAvailable()).thenReturn(sender);
        when(repository.findTop20ByEnviadaFalseAndTentativasLessThanOrderByCriadaEmAsc(5))
            .thenReturn(List.of(aviso));
        doThrow(new MailSendException("Falha SMTP de teste"))
            .when(sender).send(any(SimpleMailMessage.class));

        service(true, "remetente@exemplo.com", "login-smtp", "chave-smtp").enviarPendentes();

        assertThat(aviso.isEnviada()).isFalse();
        verify(sender).send(any(SimpleMailMessage.class));
    }
}
