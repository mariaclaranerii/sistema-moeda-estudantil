package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.*;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimentacaoService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final SessaoService sessao;
    private final AlunoRepository alunos;
    private final ProfessorRepository professores;
    private final EmpresaParceiraRepository empresas;
    private final VantagemRepository vantagens;
    private final TransacaoRepository transacoes;
    private final CreditoSemestralRepository creditos;

    public MovimentacaoService(SessaoService sessao, AlunoRepository alunos,
                              ProfessorRepository professores, EmpresaParceiraRepository empresas,
                              VantagemRepository vantagens, TransacaoRepository transacoes,
                              CreditoSemestralRepository creditos) {
        this.sessao = sessao;
        this.alunos = alunos;
        this.professores = professores;
        this.empresas = empresas;
        this.vantagens = vantagens;
        this.transacoes = transacoes;
        this.creditos = creditos;
    }

    @Transactional
    public Transacao enviar(Long alunoId, Integer quantidade, String motivo) {
        Professor professor = sessao.professor();
        if (alunoId == null || quantidade == null || quantidade <= 0 || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Informe aluno, quantidade positiva e motivo obrigatório.");
        }
        Aluno aluno = alunos.findById(alunoId)
            .orElseThrow(() -> new RegistroNaoEncontradoException("Aluno não encontrado."));
        if (!aluno.isAtivo()) throw new IllegalArgumentException("Aluno inativo.");
        professor.debitar(quantidade);
        aluno.creditarMoedas(quantidade);
        professores.save(professor);
        alunos.save(aluno);
        Transacao t = new Transacao("ENVIO_PROFESSOR", professor.getNome(), aluno.getNome(),
            motivo.trim(), quantidade);
        t.vincularTransferencia(professor.getId(), aluno.getId());
        t = transacoes.save(t);
        return t;
    }

    @Transactional
    public Transacao resgatar(Long vantagemId) {
        Aluno aluno = sessao.aluno();
        if (vantagemId == null) throw new IllegalArgumentException("Selecione uma vantagem.");
        Vantagem vantagem = vantagens.findById(vantagemId)
            .orElseThrow(() -> new RegistroNaoEncontradoException("Vantagem não encontrada."));
        if (!vantagem.isAtivo()) throw new IllegalArgumentException("Vantagem indisponível para resgate.");
        EmpresaParceira empresa = empresas.findById(vantagem.getEmpresaId())
            .orElseThrow(() -> new RegistroNaoEncontradoException("Empresa da vantagem não encontrada."));
        if (!empresa.isAtivo()) throw new IllegalArgumentException("Empresa inativa.");
        if (vantagem.getCustoMoedas() == null || vantagem.getCustoMoedas() <= 0)
            throw new IllegalArgumentException("Valor da vantagem inválido.");
        aluno.debitarMoedas(vantagem.getCustoMoedas());
        alunos.save(aluno);
        String codigo = HexFormat.of().formatHex(RANDOM.generateSeed(16)).toUpperCase();
        Transacao t = new Transacao("RESGATE_VANTAGEM", aluno.getNome(), empresa.getNome(),
                "Resgate: " + vantagem.getTitulo(), vantagem.getCustoMoedas());
        t.vincularResgate(aluno.getId(), empresa.getId(), vantagemId, codigo);
        t = transacoes.save(t);
        return t;
    }

    @Transactional(readOnly = true)
    public List<Transacao> extrato() {
        Usuario usuario = sessao.usuario();
        if (usuario instanceof Aluno) return transacoes.findAllByAlunoIdOrderByDataHoraDesc(usuario.getId());
        if (usuario instanceof Professor) return transacoes.findAllByProfessorIdOrderByDataHoraDesc(usuario.getId());
        return transacoes.findAllByEmpresaIdOrderByDataHoraDesc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Vantagem consultarVantagemDaEmpresa(Long vantagemId) {
        EmpresaParceira empresa = sessao.empresa();
        return exigirVantagemDaEmpresa(vantagemId, empresa.getId());
    }

    @Transactional
    public Vantagem cadastrarVantagem(String titulo, Integer custo, String descricao, String foto) {
        EmpresaParceira empresa = sessao.empresa();
        validarVantagem(titulo, custo, descricao, foto);
        Vantagem vantagem = new Vantagem(empresa.getId(), empresa.getNome(), titulo.trim(),
            custo, descricao.trim(), "fa-gift");
        vantagem.setFotoReferencia(foto.trim());
        return vantagens.save(vantagem);
    }

    @Transactional
    public Vantagem atualizarVantagem(Long vantagemId, String titulo, Integer custo,
                                     String descricao, String foto) {
        EmpresaParceira empresa = sessao.empresa();
        Vantagem vantagem = exigirVantagemDaEmpresa(vantagemId, empresa.getId());
        validarVantagem(titulo, custo, descricao, foto);
        vantagem.atualizar(titulo.trim(), custo, descricao.trim(), foto.trim());
        return vantagem;
    }

    @Transactional
    public Vantagem alternarSituacaoVantagem(Long vantagemId) {
        EmpresaParceira empresa = sessao.empresa();
        Vantagem vantagem = exigirVantagemDaEmpresa(vantagemId, empresa.getId());
        if (vantagem.isAtivo()) vantagem.desativar();
        else vantagem.reativar();
        return vantagem;
    }

    private Vantagem exigirVantagemDaEmpresa(Long vantagemId, Long empresaId) {
        Vantagem vantagem = vantagens.findById(vantagemId)
            .orElseThrow(() -> new RegistroNaoEncontradoException("Vantagem não encontrada."));
        if (!empresaId.equals(vantagem.getEmpresaId())) {
            throw new AccessDeniedException("Acesso restrito às vantagens da própria empresa.");
        }
        return vantagem;
    }

    private static void validarVantagem(String titulo, Integer custo, String descricao, String foto) {
        if (titulo == null || titulo.isBlank() || custo == null || custo <= 0 ||
            descricao == null || descricao.isBlank() || foto == null ||
            !foto.trim().startsWith("https://") || foto.trim().length() > 1000 ||
            titulo.trim().length() > 160 || descricao.trim().length() > 1000) {
            throw new IllegalArgumentException("Informe título, custo positivo, descrição e URL HTTPS da foto.");
        }
    }
}
