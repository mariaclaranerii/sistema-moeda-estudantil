package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.TipoUsuario;
import br.edu.pucminas.moedaestudantil.config.PermissoesConfig;
import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.web.api.dto.LoginRequest;
import br.edu.pucminas.moedaestudantil.web.api.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    private final AlunoRepository alunoRepository;
    private final EmpresaParceiraRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final String senhaAdmin;
    private final String senhaProfessor;

    public AuthApiController(AlunoRepository alunoRepository,
                             EmpresaParceiraRepository empresaRepository,
                             PasswordEncoder passwordEncoder,
                             @Value("${ADMIN_PASSWORD:}") String senhaAdmin,
                             @Value("${PROFESSOR_PASSWORD:12345678}") String senhaProfessor) {
        this.alunoRepository = alunoRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.senhaAdmin = senhaAdmin;
        this.senhaProfessor = senhaProfessor;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        if (request == null || request.login() == null || request.login().isBlank()
                || request.senha() == null || request.senha().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Informe o login e a senha."));
        }

        String login = request.login().trim();
        String senha = request.senha().trim();

        boolean reservado = login.equalsIgnoreCase("admin") || login.equalsIgnoreCase("professor")
                || login.equalsIgnoreCase("carlos.eduardo");

        // 1. Verificar Alunos
        Optional<Aluno> alunoOpt = reservado ? Optional.empty() : alunoRepository.findByLoginIgnoreCase(login);
        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();
            if (!aluno.isAtivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", "Usuário inativo no sistema."));
            }

            boolean senhaCorreta = passwordEncoder.matches(senha, aluno.getSenhaHash());

            if (!senhaCorreta) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
            }

            LoginResponseDto response = new LoginResponseDto(
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getLogin(),
                    aluno.getEmail(),
                    TipoUsuario.ALUNO,
                    aluno.getSaldo(),
                    aluno.getCurso()
            );
            PermissoesConfig.iniciarSessao(httpRequest, response);
            return ResponseEntity.ok(response);
        }

        // 2. Verificar Empresas Parceiras
        Optional<EmpresaParceira> empresaOpt = reservado ? Optional.empty() : empresaRepository.findByLoginIgnoreCase(login);
        if (empresaOpt.isPresent()) {
            EmpresaParceira empresa = empresaOpt.get();
            if (!empresa.isAtivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", "Empresa parceira inativa."));
            }

            boolean senhaCorreta = passwordEncoder.matches(senha, empresa.getSenhaHash());

            if (!senhaCorreta) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
            }

            LoginResponseDto response = new LoginResponseDto(
                    empresa.getId(),
                    empresa.getNome(),
                    empresa.getLogin(),
                    empresa.getEmail(),
                    TipoUsuario.EMPRESA,
                    null,
                    null
            );
            PermissoesConfig.iniciarSessao(httpRequest, response);
            return ResponseEntity.ok(response);
        }

        // 3. Professor (Carlos Eduardo)
        if (login.equalsIgnoreCase("professor") || login.equalsIgnoreCase("carlos.eduardo")) {
            if (senhaProfessor.equals(senha)) {
                LoginResponseDto response = new LoginResponseDto(
                        999L,
                        "Prof. Carlos Eduardo",
                        "carlos.eduardo",
                        "carlos.eduardo@pucminas.br",
                        TipoUsuario.PROFESSOR,
                        null,
                        null
                );
                PermissoesConfig.iniciarSessao(httpRequest, response);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
        }

        // 4. Administrador Geral
        if (login.equalsIgnoreCase("admin")) {
            boolean senhaCorreta = senhaAdmin.isEmpty()
                    ? "admin".equals(senha) || "12345678".equals(senha)
                    : senhaAdmin.equals(senha);
            if (senhaCorreta) {
                LoginResponseDto response = new LoginResponseDto(
                        1L,
                        "Administrador Geral",
                        "admin",
                        "admin@pucminas.br",
                        TipoUsuario.ADMIN,
                        null,
                        null
                );
                PermissoesConfig.iniciarSessao(httpRequest, response);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Login ou senha incorretos."));
    }
    @GetMapping("/me")
    public LoginResponseDto sessaoAtual(HttpServletRequest request) {
        LoginResponseDto usuario = PermissoesConfig.usuario(request);
        if (usuario.tipo() != TipoUsuario.ALUNO) return usuario;

        Aluno aluno = alunoRepository.findById(usuario.id()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return new LoginResponseDto(aluno.getId(), aluno.getNome(), aluno.getLogin(), aluno.getEmail(),
                usuario.tipo(), aluno.getSaldo(), aluno.getCurso());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> sair(HttpServletRequest request) {
        request.getSession(false).invalidate();
        return ResponseEntity.noContent().build();
    }
}
