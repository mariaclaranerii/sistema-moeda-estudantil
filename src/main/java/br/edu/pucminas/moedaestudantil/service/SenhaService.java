package br.edu.pucminas.moedaestudantil.service;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SenhaService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PasswordEncoder encoder;

    public SenhaService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public String codificar(String senha) {
        return encoder.encode(senha);
    }

    public String gerarTemporaria() {
        byte[] bytes = new byte[12];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes).toUpperCase(Locale.ROOT);
    }

    public String codificarNovaSenha(String hashAtual, String atual, String nova, String confirmacao) {
        if (atual == null || !encoder.matches(atual, hashAtual)) {
            throw new IllegalArgumentException("A senha atual está incorreta.");
        }
        if (nova == null || nova.length() < 8 || nova.length() > 72) {
            throw new IllegalArgumentException("A nova senha deve ter entre 8 e 72 caracteres.");
        }
        if (!nova.equals(confirmacao)) {
            throw new IllegalArgumentException("A confirmação da senha não confere.");
        }
        if (encoder.matches(nova, hashAtual)) {
            throw new IllegalArgumentException("A nova senha deve ser diferente da atual.");
        }
        return codificar(nova);
    }
}
