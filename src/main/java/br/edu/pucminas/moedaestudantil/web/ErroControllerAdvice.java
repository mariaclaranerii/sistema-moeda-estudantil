package br.edu.pucminas.moedaestudantil.web;

import br.edu.pucminas.moedaestudantil.service.RegistroNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErroControllerAdvice {

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String registroNaoEncontrado(RegistroNaoEncontradoException exception, Model model) {
        model.addAttribute("titulo", "Registro não encontrado");
        model.addAttribute("mensagem", exception.getMessage());
        return "erro";
    }
}
