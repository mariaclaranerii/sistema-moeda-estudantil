package br.edu.pucminas.moedaestudantil.web;

import br.edu.pucminas.moedaestudantil.model.*;
import br.edu.pucminas.moedaestudantil.repository.*;
import br.edu.pucminas.moedaestudantil.service.*;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/painel")
public class PainelController {
    private final SessaoService sessao;
    private final MovimentacaoService movimentacao;
    private final AlunoRepository alunos;
    private final VantagemRepository vantagens;
    public PainelController(SessaoService sessao, MovimentacaoService movimentacao,
                            AlunoRepository alunos, VantagemRepository vantagens) {
        this.sessao = sessao;
        this.movimentacao = movimentacao;
        this.alunos = alunos;
        this.vantagens = vantagens;
    }

    @GetMapping
    public String painel(Model model) {
        Usuario usuario = sessao.usuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("tipo", usuario instanceof Aluno ? "ALUNO" :
                            usuario instanceof Professor ? "PROFESSOR" : "EMPRESA");
        model.addAttribute("extrato", movimentacao.extrato());
        if (usuario instanceof Aluno aluno) {
            model.addAttribute("saldo", aluno.getSaldo());
            model.addAttribute("vantagens", vantagens.findAllByOrderByTituloAsc());
        } else if (usuario instanceof Professor professor) {
            model.addAttribute("saldo", professor.getSaldo());
            model.addAttribute("alunos", alunos.findAll().stream()
                .filter(Aluno::isAtivo).toList());
        } else if (usuario instanceof EmpresaParceira empresa) {
            model.addAttribute("vantagens", vantagens.findAllByOrderByTituloAsc().stream()
                .filter(v -> empresa.getId().equals(v.getEmpresaId())).toList());
        }
        return "painel";
    }

    @PostMapping("/enviar")
    public String enviar(@RequestParam Long alunoId, @RequestParam Integer quantidade,
                         @RequestParam String motivo, RedirectAttributes redirect) {
        try {
            movimentacao.enviar(alunoId, quantidade, motivo);
            redirect.addFlashAttribute("sucesso", "Moedas transferidas com sucesso.");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel";
    }

    @PostMapping("/resgatar")
    public String resgatar(@RequestParam Long vantagemId, RedirectAttributes redirect) {
        try {
            Transacao transacao = movimentacao.resgatar(vantagemId);
            redirect.addFlashAttribute("sucesso", "Resgate registrado. Código: " + transacao.getCodigoResgate());
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel";
    }

    @PostMapping("/vantagens")
    public String novaVantagem(@RequestParam String titulo, @RequestParam Integer custo,
                               @RequestParam String descricao, @RequestParam String fotoReferencia,
                               RedirectAttributes redirect) {
        try {
            movimentacao.cadastrarVantagem(titulo, custo, descricao, fotoReferencia);
            redirect.addFlashAttribute("sucesso", "Vantagem cadastrada com sucesso.");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel";
    }
}
