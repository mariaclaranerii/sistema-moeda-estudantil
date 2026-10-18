package br.edu.pucminas.moedaestudantil.web;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import br.edu.pucminas.moedaestudantil.service.ConflitoCadastroException;
import br.edu.pucminas.moedaestudantil.service.EmpresaParceiraService;
import br.edu.pucminas.moedaestudantil.web.form.EmpresaParceiraForm;
import jakarta.validation.Valid;
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
@RequestMapping("/empresas")
public class EmpresaParceiraController {

    private final EmpresaParceiraService empresaService;

    public EmpresaParceiraController(EmpresaParceiraService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empresas", empresaService.listar());
        return "empresas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("empresaParceiraForm", new EmpresaParceiraForm());
        model.addAttribute("modo", "novo");
        return "empresas/formulario";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute EmpresaParceiraForm empresaParceiraForm,
                        BindingResult bindingResult, Model model,
                        RedirectAttributes redirectAttributes) {
        if (empresaParceiraForm.getSenha() == null || empresaParceiraForm.getSenha().isBlank()) {
            bindingResult.rejectValue("senha", "senha.obrigatoria", "Informe uma senha.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("modo", "novo");
            return "empresas/formulario";
        }
        try {
            EmpresaParceira empresa = empresaService.criar(empresaParceiraForm);
            redirectAttributes.addFlashAttribute("sucesso", "Empresa parceira cadastrada com sucesso.");
            return "redirect:/empresas/" + empresa.getId();
        } catch (ConflitoCadastroException exception) {
            bindingResult.reject("cadastro.conflito", exception.getMessage());
            model.addAttribute("modo", "novo");
            return "empresas/formulario";
        }
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model) {
        model.addAttribute("empresa", empresaService.buscar(id));
        return "empresas/detalhe";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("empresaParceiraForm", EmpresaParceiraForm.from(empresaService.buscar(id)));
        prepararEdicao(model, id);
        return "empresas/formulario";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute EmpresaParceiraForm empresaParceiraForm,
                            BindingResult bindingResult, Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararEdicao(model, id);
            return "empresas/formulario";
        }
        try {
            empresaService.atualizar(id, empresaParceiraForm);
            redirectAttributes.addFlashAttribute("sucesso", "Cadastro da empresa atualizado.");
            return "redirect:/empresas/" + id;
        } catch (ConflitoCadastroException exception) {
            bindingResult.reject("cadastro.conflito", exception.getMessage());
            prepararEdicao(model, id);
            return "empresas/formulario";
        }
    }

    @PostMapping("/{id}/situacao")
    public String alternarSituacao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        empresaService.alternarSituacao(id);
        redirectAttributes.addFlashAttribute("sucesso", "Situação da empresa alterada.");
        return "redirect:/empresas/" + id;
    }

    private static void prepararEdicao(Model model, Long id) {
        model.addAttribute("empresaId", id);
        model.addAttribute("modo", "editar");
    }
}
