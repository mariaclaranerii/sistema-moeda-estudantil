package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.repository.UsuarioRepository;
import br.edu.pucminas.moedaestudantil.web.form.AlunoForm;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.security.SecureRandom;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlunoService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstituicaoEnsinoRepository instituicaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessaoService sessao;
    private final String senhaInicial;

    public AlunoService(AlunoRepository alunoRepository, UsuarioRepository usuarioRepository,
                        InstituicaoEnsinoRepository instituicaoRepository,
                        PasswordEncoder passwordEncoder, SessaoService sessao,
                        @Value("${app.aluno.senha-inicial:Aluno@2026!}") String senhaInicial) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessao = sessao;
        if (senhaInicial == null || senhaInicial.length() < 8 || senhaInicial.length() > 72) {
            throw new IllegalArgumentException("A senha inicial deve ter de 8 a 72 caracteres.");
        }
        this.senhaInicial = senhaInicial;
    }

    public List<Aluno> listar() {
        return alunoRepository.findAll().stream()
                .sorted(Comparator.comparing(Aluno::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Aluno buscar(Long id) {
        return alunoRepository.findOneById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Aluno não encontrado."));
    }

    @Transactional
    public Aluno criar(AlunoForm form) {
        DadosAluno dados = normalizar(form);
        validarUnicidade(dados, null);
        InstituicaoEnsino instituicao = buscarInstituicao(form.getInstituicaoId());

        Aluno aluno = new Aluno(dados.nome(), dados.login(), passwordEncoder.encode(senhaInicial),
                dados.email(), dados.cpf(), dados.rg(), dados.endereco(), dados.curso(), instituicao);
        aluno.exigirTrocaSenhaInicial();
        return alunoRepository.save(aluno);
    }

    @Transactional
    public Aluno atualizar(Long id, AlunoForm form) {
        Aluno aluno = buscar(id);
        DadosAluno dados = normalizar(form);
        validarUnicidade(dados, id);
        InstituicaoEnsino instituicao = buscarInstituicao(form.getInstituicaoId());

        aluno.atualizarCadastro(dados.nome(), dados.login(), dados.email(), dados.cpf(), dados.rg(),
                dados.endereco(), dados.curso(), instituicao);
        return aluno;
    }

    @Transactional
    public void alternarSituacao(Long id) {
        Aluno aluno = buscar(id);
        if (aluno.isAtivo()) {
            aluno.desativar();
        } else {
            aluno.reativar();
        }
    }

    private InstituicaoEnsino buscarInstituicao(Long id) {
        return instituicaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Instituição não encontrada."));
    }

    private void validarUnicidade(DadosAluno dados, Long idAtual) {
        boolean loginExiste = idAtual == null
                ? usuarioRepository.existsByLoginIgnoreCase(dados.login())
                : usuarioRepository.existsByLoginIgnoreCaseAndIdNot(dados.login(), idAtual);
        boolean emailExiste = idAtual == null
                ? alunoRepository.existsByEmailIgnoreCase(dados.email())
                : alunoRepository.existsByEmailIgnoreCaseAndIdNot(dados.email(), idAtual);
        boolean cpfExiste = idAtual == null
                ? alunoRepository.existsByCpf(dados.cpf())
                : alunoRepository.existsByCpfAndIdNot(dados.cpf(), idAtual);
        boolean rgExiste = idAtual == null
                ? alunoRepository.existsByRgIgnoreCase(dados.rg())
                : alunoRepository.existsByRgIgnoreCaseAndIdNot(dados.rg(), idAtual);

        if (loginExiste) throw new ConflitoCadastroException("Este login já está em uso.");
        if (emailExiste) throw new ConflitoCadastroException("Este e-mail já está em uso por outro aluno.");
        if (cpfExiste) throw new ConflitoCadastroException("Este CPF já está cadastrado.");
        if (rgExiste) throw new ConflitoCadastroException("Este RG já está cadastrado.");
    }

    /** Redefinição presencial pelo professor: senha aleatória exibida uma única vez. */
    @Transactional
    public String redefinirSenhaPeloProfessor(Long alunoId) {
        sessao.professor();
        Aluno aluno = buscar(alunoId);
        byte[] bytes = new byte[12];
        RANDOM.nextBytes(bytes);
        String temporaria = HexFormat.of().formatHex(bytes).toUpperCase(Locale.ROOT);
        aluno.alterarSenha(passwordEncoder.encode(temporaria));
        aluno.exigirTrocaSenhaInicial();
        return temporaria;
    }

    @Transactional
    public void alterarMinhaSenha(String atual, String nova, String confirmacao) {
        Aluno aluno = alunoRepository.findById(sessao.aluno().getId())
            .orElseThrow(() -> new RegistroNaoEncontradoException("Aluno não encontrado."));
        if (atual == null || !passwordEncoder.matches(atual, aluno.getSenhaHash())) {
            throw new IllegalArgumentException("A senha atual está incorreta.");
        }
        if (nova == null || nova.length() < 8 || nova.length() > 72) {
            throw new IllegalArgumentException("A nova senha deve ter entre 8 e 72 caracteres.");
        }
        if (!nova.equals(confirmacao)) {
            throw new IllegalArgumentException("A confirmação da senha não confere.");
        }
        if (passwordEncoder.matches(nova, aluno.getSenhaHash())) {
            throw new IllegalArgumentException("A nova senha deve ser diferente da atual.");
        }
        aluno.alterarSenha(passwordEncoder.encode(nova));
        aluno.confirmarTrocaSenha();
    }

    private static DadosAluno normalizar(AlunoForm form) {
        String email = minusculas(form.getEmail());
        return new DadosAluno(limpar(form.getNome()), email, email,
                apenasDigitos(form.getCpf()), limpar(form.getRg()),
                limpar(form.getEndereco()), limpar(form.getCurso()));
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private static String minusculas(String valor) {
        return limpar(valor).toLowerCase(Locale.ROOT);
    }

    private static String apenasDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private record DadosAluno(String nome, String login, String email, String cpf, String rg,
                              String endereco, String curso) {
    }
}
