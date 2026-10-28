package br.edu.pucminas.moedaestudantil.config;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.UsuarioRepository;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SegurancaConfig {
    @Bean
    UserDetailsService userDetailsService(UsuarioRepository repository, AlunoRepository alunos) {
        return login -> {
            // Aceita o e-mail de alunos antigos, sem invalidar seus logins existentes.
            Usuario usuario = repository.findByLoginIgnoreCase(login)
                .orElseGet(() -> alunos.findByEmailIgnoreCase(login)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválidos.")));
            String perfil = usuario instanceof Aluno ? "ALUNO" :
                            usuario instanceof Professor ? "PROFESSOR" : "EMPRESA";
            return User.withUsername(usuario.getLogin())
                .password(usuario.getSenhaHash()).roles(perfil)
                .disabled(!usuario.isAtivo()).build();
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/app.css", "/error", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/instituicoes").permitAll()
                .requestMatchers(HttpMethod.GET, "/alunos/novo", "/empresas/nova").hasRole("PROFESSOR")
                .requestMatchers(HttpMethod.POST, "/alunos", "/empresas", "/api/alunos", "/api/empresas")
                    .hasRole("PROFESSOR")
                .requestMatchers("/h2-console/**").denyAll()
                .requestMatchers("/painel", "/painel/**", "/perfil", "/perfil/**", "/api/**", "/alunos/**", "/empresas/**", "/app",
                    "/sistema_de_moeda_estudantil_frontend.html").authenticated()
                .anyRequest().denyAll())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/painel", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .build();
    }
}
