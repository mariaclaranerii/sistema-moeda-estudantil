package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.service.SessaoService;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessao")
public class SessaoApiController {
    private final SessaoService sessao;
    public SessaoApiController(SessaoService sessao) { this.sessao = sessao; }

    @GetMapping
    public Map<String, Object> atual(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Usuario usuario = sessao.usuario();
        String perfil = usuario instanceof Aluno ? "ALUNO" :
                         usuario instanceof Professor ? "PROFESSOR" : "EMPRESA";
        int saldo = usuario instanceof Aluno aluno ? aluno.getSaldo() :
                    usuario instanceof Professor professor ? professor.getSaldo() : 0;
        return Map.of("id", usuario.getId(), "nome", usuario.getNome(), "perfil", perfil,
            "saldo", saldo, "csrfHeader", csrf.getHeaderName(), "csrfToken", csrf.getToken());
    }
}
