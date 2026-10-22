package br.edu.pucminas.moedaestudantil.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/app")
    public String app() {
        return "redirect:/css/sistema_de_moeda_estudantil_frontend.html";
    }
}
