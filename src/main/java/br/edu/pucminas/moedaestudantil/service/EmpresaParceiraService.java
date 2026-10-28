package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.repository.UsuarioRepository;
import br.edu.pucminas.moedaestudantil.web.form.EmpresaParceiraForm;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.security.SecureRandom;
import java.util.HexFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmpresaParceiraService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmpresaParceiraRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessaoService sessao;

    public EmpresaParceiraService(EmpresaParceiraRepository empresaRepository,
                                  UsuarioRepository usuarioRepository,
                                  PasswordEncoder passwordEncoder, SessaoService sessao) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessao = sessao;
    }

    public List<EmpresaParceira> listar() {
        return empresaRepository.findAll().stream()
                .sorted(Comparator.comparing(EmpresaParceira::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public EmpresaParceira buscar(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Empresa parceira não encontrada."));
    }

    @Transactional
    public EmpresaParceira criar(EmpresaParceiraForm form) {
        exigirSenha(form.getSenha());
        DadosEmpresa dados = normalizar(form);
        validarUnicidade(dados, null);
        EmpresaParceira empresa = new EmpresaParceira(dados.nome(), dados.login(),
                passwordEncoder.encode(form.getSenha()), dados.email());
        // O professor define a senha inicial; a empresa precisa personalizá-la.
        empresa.exigirTrocaSenhaInicial();
        return empresaRepository.save(empresa);
    }

    @Transactional
    public EmpresaParceira atualizar(Long id, EmpresaParceiraForm form) {
        EmpresaParceira empresa = buscar(id);
        DadosEmpresa dados = normalizar(form);
        validarUnicidade(dados, id);
        empresa.atualizarCadastro(dados.nome(), dados.login(), dados.email());
        // A alteração de senha é uma operação separada, com confirmação da senha atual.
        return empresa;
    }

    /** Somente professor pode gerar uma nova credencial temporária. */
    @Transactional
    public String redefinirSenhaPeloProfessor(Long empresaId) {
        sessao.professor();
        EmpresaParceira empresa = buscar(empresaId);
        byte[] bytes = new byte[12];
        RANDOM.nextBytes(bytes);
        String temporaria = HexFormat.of().formatHex(bytes).toUpperCase(Locale.ROOT);
        empresa.alterarSenha(passwordEncoder.encode(temporaria));
        empresa.exigirTrocaSenhaInicial();
        return temporaria;
    }

    /** Alteração de senha local, sem e-mail, feita somente pela própria empresa. */
    @Transactional
    public void alterarMinhaSenha(String atual, String nova, String confirmacao) {
        EmpresaParceira empresa = buscar(sessao.empresa().getId());
        if (atual == null || !passwordEncoder.matches(atual, empresa.getSenhaHash())) {
            throw new IllegalArgumentException("A senha atual está incorreta.");
        }
        if (nova == null || nova.length() < 8 || nova.length() > 72) {
            throw new IllegalArgumentException("A nova senha deve ter entre 8 e 72 caracteres.");
        }
        if (!nova.equals(confirmacao)) {
            throw new IllegalArgumentException("A confirmação da senha não confere.");
        }
        if (passwordEncoder.matches(nova, empresa.getSenhaHash())) {
            throw new IllegalArgumentException("A nova senha deve ser diferente da atual.");
        }
        empresa.alterarSenha(passwordEncoder.encode(nova));
        empresa.confirmarTrocaSenha();
    }

    @Transactional
    public void alternarSituacao(Long id) {
        EmpresaParceira empresa = buscar(id);
        if (empresa.isAtivo()) {
            empresa.desativar();
        } else {
            empresa.reativar();
        }
    }

    private void validarUnicidade(DadosEmpresa dados, Long idAtual) {
        boolean loginExiste = idAtual == null
                ? usuarioRepository.existsByLoginIgnoreCase(dados.login())
                : usuarioRepository.existsByLoginIgnoreCaseAndIdNot(dados.login(), idAtual);
        boolean emailExiste = idAtual == null
                ? empresaRepository.existsByEmailIgnoreCase(dados.email())
                : empresaRepository.existsByEmailIgnoreCaseAndIdNot(dados.email(), idAtual);
        if (loginExiste) throw new ConflitoCadastroException("Este login já está em uso.");
        if (emailExiste) throw new ConflitoCadastroException("Este e-mail já está em uso por outra empresa.");
    }

    private static void exigirSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new ConflitoCadastroException("Informe uma senha no novo cadastro.");
        }
    }

    private static DadosEmpresa normalizar(EmpresaParceiraForm form) {
        return new DadosEmpresa(limpar(form.getNome()), minusculas(form.getLogin()),
                minusculas(form.getEmail()));
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private static String minusculas(String valor) {
        return limpar(valor).toLowerCase(Locale.ROOT);
    }

    private record DadosEmpresa(String nome, String login, String email) {
    }
}
