package br.edu.pucminas.moedaestudantil.config;

import br.edu.pucminas.moedaestudantil.model.TipoUsuario;
import br.edu.pucminas.moedaestudantil.repository.AlunoRepository;
import br.edu.pucminas.moedaestudantil.repository.EmpresaParceiraRepository;
import br.edu.pucminas.moedaestudantil.web.api.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ServletRequestPathUtils;

@Configuration
public class PermissoesConfig implements WebMvcConfigurer, HandlerInterceptor {

    private static final String USUARIO = PermissoesConfig.class.getName() + ".usuario";

    private final AlunoRepository alunos;
    private final EmpresaParceiraRepository empresas;

    public PermissoesConfig(AlunoRepository alunos, EmpresaParceiraRepository empresas) {
        this.alunos = alunos;
        this.empresas = empresas;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/api/**", "/alunos/**", "/empresas/**");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication().value();
        String metodo = request.getMethod();
        HttpSession session = request.getSession(false);
        LoginResponseDto usuario = session == null ? null : (LoginResponseDto) session.getAttribute(USUARIO);
        request.setAttribute(USUARIO, usuario);

        String origem = request.getHeader("Sec-Fetch-Site");
        if (!"GET".equals(metodo) && !"HEAD".equals(metodo) && origem != null
                && !"same-origin".equals(origem) && !"none".equals(origem)) {
            rejeitar(response, 403, "Abra o sistema para continuar.");
            return false;
        }
        if (rotaPublica(metodo, path, usuario)) {
            return true;
        }
        if (usuario == null || !contaAtiva(usuario)) {
            rejeitar(response, 401, "Entre no sistema para continuar.");
            return false;
        }
        if (permitido(usuario.tipo(), metodo, path)) {
            return true;
        }
        rejeitar(response, 403, "Seu perfil não tem permissão para esta operação.");
        return false;
    }

    private static boolean rotaPublica(String metodo, String path, LoginResponseDto usuario) {
        return ("GET".equals(metodo) && "/api/instituicoes".equals(path))
                || ("POST".equals(metodo) && ("/api/auth/login".equals(path)
                    || (usuario == null && ("/api/alunos".equals(path) || "/api/empresas".equals(path)))));
    }

    private static boolean permitido(TipoUsuario perfil, String metodo, String path) {
        boolean leitura = "GET".equals(metodo);
        boolean criacao = "POST".equals(metodo);
        if ((leitura && "/api/auth/me".equals(path)) || (criacao && "/api/auth/logout".equals(path))) return true;
        if (leitura && "/api/transacoes".equals(path)) return true;
        if (leitura && "/api/vantagens".equals(path)) return perfil != TipoUsuario.PROFESSOR;
        return switch (perfil) {
            case ADMIN -> path.matches("/(?:api/)?(?:alunos|empresas)(?:/.*)?");
            case PROFESSOR -> (leitura && "/api/alunos".equals(path))
                    || (criacao && "/api/transacoes/enviar".equals(path));
            case ALUNO -> criacao && "/api/transacoes/resgatar".equals(path);
            case EMPRESA -> criacao && "/api/vantagens".equals(path);
        };
    }

    private boolean contaAtiva(LoginResponseDto usuario) {
        return switch (usuario.tipo()) {
            case ALUNO -> alunos.findById(usuario.id()).map(aluno -> aluno.isAtivo()).orElse(false);
            case EMPRESA -> empresas.findById(usuario.id()).map(empresa -> empresa.isAtivo()).orElse(false);
            case ADMIN, PROFESSOR -> true;
        };
    }

    private static void rejeitar(HttpServletResponse response, int status, String mensagem)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"erro\":\"" + mensagem + "\"}");
    }

    public static LoginResponseDto usuario(HttpServletRequest request) {
        return (LoginResponseDto) request.getAttribute(USUARIO);
    }

    public static void iniciarSessao(HttpServletRequest request, LoginResponseDto usuario) {
        HttpSession session = request.getSession();
        request.changeSessionId();
        session.setAttribute(USUARIO, usuario);
    }
}
