package br.edu.pucminas.moedaestudantil.config;

import br.edu.pucminas.moedaestudantil.service.SessaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Bloqueia operações de alunos e empresas enquanto utilizam senhas iniciais ou redefinidas. */
@Component
public class PrimeiroAcessoInterceptor implements HandlerInterceptor {
    private final SessaoService sessao;

    public PrimeiroAcessoInterceptor(SessaoService sessao) { this.sessao = sessao; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return true;
        boolean aluno = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));
        boolean empresa = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"));
        if (!aluno && !empresa) return true;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.equals("/perfil/senha") || path.equals("/perfil") || path.equals("/logout") ||
                path.equals("/error") || path.equals("/favicon.ico") || path.startsWith("/css/")) {
            return true;
        }
        boolean pendente = aluno ? sessao.aluno().isTrocaSenhaPendente()
            : sessao.empresa().isTrocaSenhaPendente();
        if (!pendente) return true;
        if (path.startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Altere a senha temporária antes de acessar a API.");
        } else {
            response.sendRedirect(request.getContextPath() + "/perfil/senha");
        }
        return false;
    }
}
