package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.service.*;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = "br.edu.pucminas.moedaestudantil.web.api")
public class ApiErroControllerAdvice {
    @ExceptionHandler(ConflitoCadastroException.class)
    public ResponseEntity<Map<String,String>> conflito(ConflitoCadastroException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro",e.getMessage()));
    }
    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<Map<String,String>> naoEncontrado(RegistroNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro",e.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> invalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("erro",e.getMessage()));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,String>> semAcesso(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro","Acesso não permitido."));
    }
}
