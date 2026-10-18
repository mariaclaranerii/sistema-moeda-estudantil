package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.repository.UsuarioRepository;
import br.edu.pucminas.moedaestudantil.web.form.EmpresaParceiraForm;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmpresaParceiraService {

    private final EmpresaParceiraRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpresaParceiraService(EmpresaParceiraRepository empresaRepository,
                                  UsuarioRepository usuarioRepository,
                                  PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        return empresaRepository.save(empresa);
    }

    @Transactional
    public EmpresaParceira atualizar(Long id, EmpresaParceiraForm form) {
        EmpresaParceira empresa = buscar(id);
        DadosEmpresa dados = normalizar(form);
        validarUnicidade(dados, id);
        empresa.atualizarCadastro(dados.nome(), dados.login(), dados.email());
        if (form.getSenha() != null && !form.getSenha().isBlank()) {
            empresa.alterarSenha(passwordEncoder.encode(form.getSenha()));
        }
        return empresa;
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
