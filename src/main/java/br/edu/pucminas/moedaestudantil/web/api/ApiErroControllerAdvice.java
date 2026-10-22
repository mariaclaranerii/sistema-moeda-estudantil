package br.edu.pucminas.moedaestudantil.web.api;

import br.edu.pucminas.moedaestudantil.service.ConflitoCadastroException;
import br.edu.pucminas.moedaestudantil.service.RegistroNaoEncontradoException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.edu.pucminas.moedaestudantil.web.api")
public class ApiErroControllerAdvice {

    @ExceptionHandler(ConflitoCadastroException.class)
    public ResponseEntity<Map<String, String>> conflito(ConflitoCadastroException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(RegistroNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", ex.getMessage()));
    }
}
