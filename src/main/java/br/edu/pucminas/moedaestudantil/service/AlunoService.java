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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstituicaoEnsinoRepository instituicaoRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, UsuarioRepository usuarioRepository,
                        InstituicaoEnsinoRepository instituicaoRepository,
                        PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.passwordEncoder = passwordEncoder;
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
        exigirSenha(form.getSenha());
        DadosAluno dados = normalizar(form);
        validarUnicidade(dados, null);
        InstituicaoEnsino instituicao = buscarInstituicao(form.getInstituicaoId());

        Aluno aluno = new Aluno(dados.nome(), dados.login(), passwordEncoder.encode(form.getSenha()),
                dados.email(), dados.cpf(), dados.rg(), dados.endereco(), dados.curso(), instituicao);
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
        if (form.getSenha() != null && !form.getSenha().isBlank()) {
            aluno.alterarSenha(passwordEncoder.encode(form.getSenha()));
        }
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

    private static void exigirSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new ConflitoCadastroException("Informe uma senha no novo cadastro.");
        }
    }

    private static DadosAluno normalizar(AlunoForm form) {
        return new DadosAluno(limpar(form.getNome()), minusculas(form.getLogin()),
                minusculas(form.getEmail()), apenasDigitos(form.getCpf()), limpar(form.getRg()),
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
