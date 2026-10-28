package br.edu.pucminas.moedaestudantil.service;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessaoService {
    private final UsuarioRepository usuarios;
    public SessaoService(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    @Transactional(readOnly = true)
    public Usuario usuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("Faça login para continuar.");
        }
        Usuario usuario = usuarios.findByLoginIgnoreCase(auth.getName())
            .orElseThrow(() -> new AccessDeniedException("Usuário inválido."));
        if (!usuario.isAtivo()) throw new AccessDeniedException("Cadastro inativo.");
        return usuario;
    }

    @Transactional(readOnly = true)
    public Aluno aluno() {
        Usuario usuario = usuario();
        if (!(usuario instanceof Aluno aluno)) throw new AccessDeniedException("Operação exclusiva de alunos.");
        return aluno;
    }
    @Transactional(readOnly = true)
    public Professor professor() {
        Usuario usuario = usuario();
        if (!(usuario instanceof Professor professor)) throw new AccessDeniedException("Operação exclusiva de professores.");
        return professor;
    }
    @Transactional(readOnly = true)
    public EmpresaParceira empresa() {
        Usuario usuario = usuario();
        if (!(usuario instanceof EmpresaParceira empresa)) throw new AccessDeniedException("Operação exclusiva de empresas.");
        return empresa;
    }
    public void exigirProprietario(Long id) {
        if (!usuario().getId().equals(id)) throw new AccessDeniedException("Acesso restrito ao titular do cadastro.");
    }
}
