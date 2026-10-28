package br.edu.pucminas.moedaestudantil.web;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.service.SessaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {

    private final SessaoService sessao;

    public PerfilController(SessaoService sessao) {
        this.sessao = sessao;
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = sessao.usuario();
        model.addAttribute("nome", usuario.getNome());
        model.addAttribute("login", usuario.getLogin());
        if (usuario instanceof Aluno aluno) {
            model.addAttribute("tipo", "Aluno");
            model.addAttribute("email", aluno.getEmail());
            model.addAttribute("curso", aluno.getCurso());
            model.addAttribute("cadastroUrl", "/alunos/" + aluno.getId());
        } else if (usuario instanceof Professor professor) {
            model.addAttribute("tipo", "Professor");
            model.addAttribute("departamento", professor.getDepartamento());
        } else if (usuario instanceof EmpresaParceira empresa) {
            model.addAttribute("tipo", "Empresa parceira");
            model.addAttribute("email", empresa.getEmail());
            model.addAttribute("cadastroUrl", "/empresas/" + empresa.getId());
        }
        return "perfil";
    }
}
