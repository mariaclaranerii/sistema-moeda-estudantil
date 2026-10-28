package br.edu.pucminas.moedaestudantil.web;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.service.SessaoService;
import br.edu.pucminas.moedaestudantil.service.AlunoService;
import br.edu.pucminas.moedaestudantil.service.EmpresaParceiraService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {

    private final SessaoService sessao;
    private final AlunoService alunos;
    private final EmpresaParceiraService empresas;

    public PerfilController(SessaoService sessao, AlunoService alunos, EmpresaParceiraService empresas) {
        this.sessao = sessao;
        this.alunos = alunos;
        this.empresas = empresas;
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
            model.addAttribute("trocaSenhaPendente", aluno.isTrocaSenhaPendente());
            model.addAttribute("cadastroUrl", "/alunos/" + aluno.getId());
        } else if (usuario instanceof Professor professor) {
            model.addAttribute("tipo", "Professor");
            model.addAttribute("departamento", professor.getDepartamento());
        } else if (usuario instanceof EmpresaParceira empresa) {
            model.addAttribute("tipo", "Empresa parceira");
            model.addAttribute("email", empresa.getEmail());
            model.addAttribute("trocaSenhaPendente", empresa.isTrocaSenhaPendente());
            model.addAttribute("cadastroUrl", "/empresas/" + empresa.getId());
        }
        return "perfil";
    }

    @GetMapping("/perfil/senha")
    public String editarSenha(Model model) {
        Usuario usuario = sessao.usuario();
        if (usuario instanceof Aluno aluno) {
            model.addAttribute("trocaSenhaPendente", aluno.isTrocaSenhaPendente());
        } else if (usuario instanceof EmpresaParceira empresa) {
            model.addAttribute("trocaSenhaPendente", empresa.isTrocaSenhaPendente());
        } else {
            throw new org.springframework.security.access.AccessDeniedException(
                "Alteração de senha disponível para alunos e empresas.");
        }
        return "alterar-senha";
    }

    @PostMapping("/perfil/senha")
    public String salvarSenha(@RequestParam String senhaAtual, @RequestParam String novaSenha,
                             @RequestParam String confirmacao, RedirectAttributes redirect) {
        try {
            Usuario usuario = sessao.usuario();
            if (usuario instanceof Aluno) {
                alunos.alterarMinhaSenha(senhaAtual, novaSenha, confirmacao);
            } else if (usuario instanceof EmpresaParceira) {
                empresas.alterarMinhaSenha(senhaAtual, novaSenha, confirmacao);
            } else {
                throw new org.springframework.security.access.AccessDeniedException(
                    "Alteração de senha disponível para alunos e empresas.");
            }
            redirect.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
            return "redirect:/perfil";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/perfil/senha";
        }
    }
}
