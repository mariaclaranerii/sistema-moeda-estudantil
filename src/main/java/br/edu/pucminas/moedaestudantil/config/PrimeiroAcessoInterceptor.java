package br.edu.pucminas.moedaestudantil.config;

import br.edu.pucminas.moedaestudantil.service.SessaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Bloqueia operações enquanto o aluno ainda usa a senha temporária. */
@Component
public class PrimeiroAcessoInterceptor implements HandlerInterceptor {
    private final SessaoService sessao;

    public PrimeiroAcessoInterceptor(SessaoService sessao) { this.sessao = sessao; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ALUNO"))) {
            return true;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.equals("/perfil/senha") || path.equals("/perfil") || path.equals("/logout") ||
                path.equals("/error") || path.equals("/favicon.ico") || path.startsWith("/css/")) {
            return true;
        }
        if (!sessao.aluno().isTrocaSenhaPendente()) return true;
        if (path.startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Altere a senha temporária antes de acessar a API.");
        } else {
            response.sendRedirect(request.getContextPath() + "/perfil/senha");
        }
        return false;
    }
}
