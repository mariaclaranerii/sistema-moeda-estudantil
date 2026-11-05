package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.web.api.dto.LoginRequest;
import br.edu.pucminas.moedaestudantil.web.api.dto.LoginResponseDto;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    private final AlunoRepository alunoRepository;
    private final EmpresaParceiraRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthApiController(AlunoRepository alunoRepository,
                             EmpresaParceiraRepository empresaRepository,
                             PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        if (request == null || request.login() == null || request.login().isBlank()
                || request.senha() == null || request.senha().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Informe o login e a senha."));
        }

        String login = request.login().trim();
        String senha = request.senha().trim();

        // 1. Verificar Alunos
        Optional<Aluno> alunoOpt = alunoRepository.findByLoginIgnoreCase(login);
        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();
            if (!aluno.isAtivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", "Usuário inativo no sistema."));
            }

            boolean senhaCorreta = passwordEncoder.matches(senha, aluno.getSenhaHash())
                    || senha.equals(aluno.getSenhaHash());

            if (!senhaCorreta) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
            }

            LoginResponseDto response = new LoginResponseDto(
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getLogin(),
                    aluno.getEmail(),
                    "ALUNO",
                    aluno.getSaldo(),
                    aluno.getCurso(),
                    UUID.randomUUID().toString()
            );
            return ResponseEntity.ok(response);
        }

        // 2. Verificar Empresas Parceiras
        Optional<EmpresaParceira> empresaOpt = empresaRepository.findByLoginIgnoreCase(login);
        if (empresaOpt.isPresent()) {
            EmpresaParceira empresa = empresaOpt.get();
            if (!empresa.isAtivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", "Empresa parceira inativa."));
            }

            boolean senhaCorreta = passwordEncoder.matches(senha, empresa.getSenhaHash())
                    || senha.equals(empresa.getSenhaHash());

            if (!senhaCorreta) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
            }

            LoginResponseDto response = new LoginResponseDto(
                    empresa.getId(),
                    empresa.getNome(),
                    empresa.getLogin(),
                    empresa.getEmail(),
                    "EMPRESA",
                    null,
                    null,
                    UUID.randomUUID().toString()
            );
            return ResponseEntity.ok(response);
        }

        // 3. Professor (Carlos Eduardo)
        if (login.equalsIgnoreCase("professor") || login.equalsIgnoreCase("carlos.eduardo")) {
            if ("12345678".equals(senha)) {
                LoginResponseDto response = new LoginResponseDto(
                        999L,
                        "Prof. Carlos Eduardo",
                        "carlos.eduardo",
                        "carlos.eduardo@pucminas.br",
                        "PROFESSOR",
                        null,
                        null,
                        UUID.randomUUID().toString()
                );
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
        }

        // 4. Administrador Geral
        if (login.equalsIgnoreCase("admin")) {
            if ("admin".equals(senha) || "12345678".equals(senha)) {
                LoginResponseDto response = new LoginResponseDto(
                        1L,
                        "Administrador Geral",
                        "admin",
                        "admin@pucminas.br",
                        "ADMIN",
                        null,
                        null,
                        UUID.randomUUID().toString()
                );
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
    }
}
