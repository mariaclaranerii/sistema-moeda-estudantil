package br.edu.pucminas.moedaestudantil.web;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import br.edu.pucminas.moedaestudantil.model.Professor;
import br.edu.pucminas.moedaestudantil.model.InstituicaoEnsino;
import br.edu.pucminas.moedaestudantil.repository.InstituicaoEnsinoRepository;
import br.edu.pucminas.moedaestudantil.service.AlunoService;
import br.edu.pucminas.moedaestudantil.service.SessaoService;
import br.edu.pucminas.moedaestudantil.service.ConflitoCadastroException;
import br.edu.pucminas.moedaestudantil.web.form.AlunoForm;
import jakarta.validation.Valid;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final SessaoService sessao;
    private final InstituicaoEnsinoRepository instituicaoRepository;

    public AlunoController(AlunoService alunoService, InstituicaoEnsinoRepository instituicaoRepository, SessaoService sessao) {
        this.sessao = sessao;
        this.alunoService = alunoService;
        this.instituicaoRepository = instituicaoRepository;
    }

    @ModelAttribute("instituicoes")
    public List<InstituicaoEnsino> instituicoes() {
        return instituicaoRepository.findAllByOrderByNomeAsc();
    }

    @GetMapping
    public String listar(Model model) {
        if (sessao.usuario() instanceof Professor) {
            model.addAttribute("alunos", alunoService.listar());
            return "alunos/lista";
        }
        sessao.aluno();
        return "redirect:/alunos/" + sessao.usuario().getId();
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("alunoForm", new AlunoForm());
        model.addAttribute("modo", "novo");
        return "alunos/formulario";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute AlunoForm alunoForm, BindingResult bindingResult,
                        Model model, RedirectAttributes redirectAttributes,
                        Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modo", "novo");
            return "alunos/formulario";
        }
        try {
            Aluno aluno = alunoService.criar(alunoForm);
            redirectAttributes.addFlashAttribute("sucesso", "Aluno cadastrado. O login é "
                + aluno.getEmail() + ". Informe a senha inicial ao aluno para que a altere no primeiro acesso.");
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_PROFESSOR"))) {
                return "redirect:/painel";
            }
            return "redirect:/login?cadastro";
        } catch (ConflitoCadastroException exception) {
            bindingResult.reject("cadastro.conflito", exception.getMessage());
            model.addAttribute("modo", "novo");
            return "alunos/formulario";
        }
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model) {
        sessao.aluno();
        sessao.exigirProprietario(id);
        model.addAttribute("aluno", alunoService.buscar(id));
        return "alunos/detalhe";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        sessao.aluno();
        sessao.exigirProprietario(id);
        model.addAttribute("alunoForm", AlunoForm.from(alunoService.buscar(id)));
        model.addAttribute("alunoId", id);
        model.addAttribute("modo", "editar");
        return "alunos/formulario";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute AlunoForm alunoForm,
                            BindingResult bindingResult, Model model,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        sessao.aluno();
        sessao.exigirProprietario(id);
        if (bindingResult.hasErrors()) {
            prepararEdicao(model, id);
            return "alunos/formulario";
        }
        try {
            String loginAntes = sessao.usuario().getLogin();
            Aluno atualizado = alunoService.atualizar(id, alunoForm);
            if (!loginAntes.equalsIgnoreCase(atualizado.getLogin())) {
                if (request.getSession(false) != null) request.getSession(false).invalidate();
                SecurityContextHolder.clearContext();
                return "redirect:/login?emailAtualizado";
            }
            redirectAttributes.addFlashAttribute("sucesso", "Cadastro do aluno atualizado.");
            return "redirect:/alunos/" + id;
        } catch (ConflitoCadastroException exception) {
            bindingResult.reject("cadastro.conflito", exception.getMessage());
            prepararEdicao(model, id);
            return "alunos/formulario";
        }
    }

    @PostMapping("/{id}/situacao")
    public String alternarSituacao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        sessao.aluno();
        sessao.exigirProprietario(id);
        alunoService.alternarSituacao(id);
        redirectAttributes.addFlashAttribute("sucesso", "Situação do aluno alterada.");
        return "redirect:/alunos/" + id;
    }

    private static void prepararEdicao(Model model, Long id) {
        model.addAttribute("alunoId", id);
        model.addAttribute("modo", "editar");
    }
}
