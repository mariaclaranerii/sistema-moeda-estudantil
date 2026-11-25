package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.model.Usuario;
import br.edu.pucminas.moedaestudantil.model.Vantagem;
import br.edu.pucminas.moedaestudantil.repository.VantagemRepository;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VantagemService {
    private final SessaoService sessao;
    private final VantagemRepository vantagens;

    public VantagemService(SessaoService sessao, VantagemRepository vantagens) {
        this.sessao = sessao;
        this.vantagens = vantagens;
    }

    public List<Vantagem> listarPara(Usuario usuario) {
        if (usuario instanceof EmpresaParceira) {
            return vantagens.findByEmpresaIdOrderByTituloAsc(usuario.getId());
        }
        return vantagens.findAllByOrderByTituloAsc().stream()
            .filter(Vantagem::isAtivo)
            .toList();
    }

    public Vantagem consultarDaEmpresa(Long vantagemId) {
        return exigirVantagemDaEmpresa(vantagemId, sessao.empresa().getId());
    }

    @Transactional
    public Vantagem cadastrar(String titulo, Integer custo, String descricao, String foto) {
        EmpresaParceira empresa = sessao.empresa();
        validarVantagem(titulo, custo, descricao, foto);
        Vantagem vantagem = new Vantagem(empresa.getId(), empresa.getNome(), titulo.trim(),
            custo, descricao.trim(), "fa-gift");
        vantagem.setFotoReferencia(foto.trim());
        return vantagens.save(vantagem);
    }

    @Transactional
    public Vantagem atualizar(Long vantagemId, String titulo, Integer custo, String descricao, String foto) {
        Vantagem vantagem = exigirVantagemDaEmpresa(vantagemId, sessao.empresa().getId());
        validarVantagem(titulo, custo, descricao, foto);
        vantagem.atualizar(titulo.trim(), custo, descricao.trim(), foto.trim());
        return vantagem;
    }

    @Transactional
    public Vantagem alternarSituacao(Long vantagemId) {
        Vantagem vantagem = exigirVantagemDaEmpresa(vantagemId, sessao.empresa().getId());
        if (vantagem.isAtivo()) {
            vantagem.desativar();
        } else {
            vantagem.reativar();
        }
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
