package br.edu.pucminas.moedaestudantil.config;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SegurancaConfig {
    @Bean
    UserDetailsService userDetailsService(UsuarioRepository repository) {
        return login -> {
            Usuario usuario = repository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválidos."));
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
                .requestMatchers("/", "/login", "/css/app.css", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/instituicoes").permitAll()
                .requestMatchers(HttpMethod.GET, "/alunos/novo", "/empresas/nova").permitAll()
                .requestMatchers(HttpMethod.POST, "/alunos", "/empresas", "/api/alunos", "/api/empresas").permitAll()
                .requestMatchers("/h2-console/**").denyAll()
                .requestMatchers("/painel", "/painel/**", "/api/**", "/alunos/**", "/empresas/**", "/app",
                    "/sistema_de_moeda_estudantil_frontend.html").authenticated()
                .anyRequest().denyAll())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/painel", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .build();
    }
}
