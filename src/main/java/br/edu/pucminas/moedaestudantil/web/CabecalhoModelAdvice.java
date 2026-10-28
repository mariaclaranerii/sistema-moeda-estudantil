package br.edu.pucminas.moedaestudantil.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Informações da sessão exibidas no cabeçalho de todas as páginas MVC.
 */
@ControllerAdvice(basePackages = "br.edu.pucminas.moedaestudantil.web")
public class CabecalhoModelAdvice {

    public record EstadoCabecalho(boolean autenticado, String perfil) {}

    @ModelAttribute("cabecalho")
    public EstadoCabecalho cabecalho() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return new EstadoCabecalho(false, "");
        }
        String perfil = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO")) ? "Aluno"
            : auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")) ? "Professor"
            : auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA")) ? "Empresa parceira"
            : "Usuário";
        return new EstadoCabecalho(true, perfil);
    }
}
