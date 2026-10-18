package br.edu.pucminas.moedaestudantil.web.form;

import br.edu.pucminas.moedaestudantil.model.EmpresaParceira;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmpresaParceiraForm {

    @NotBlank(message = "Informe o nome da empresa.")
    @Size(max = 120, message = "O nome deve ter até 120 caracteres.")
    private String nome = "";

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 160, message = "O e-mail deve ter até 160 caracteres.")
    private String email = "";

    @NotBlank(message = "Informe o login.")
    @Size(min = 4, max = 80, message = "O login deve ter entre 4 e 80 caracteres.")
    @Pattern(regexp = "[A-Za-z0-9._-]+", message = "Use apenas letras, números, ponto, hífen ou sublinhado.")
    private String login = "";

    @Pattern(regexp = "^$|.{8,72}", message = "A senha deve ter entre 8 e 72 caracteres.")
    private String senha = "";

    public static EmpresaParceiraForm from(EmpresaParceira empresa) {
        EmpresaParceiraForm form = new EmpresaParceiraForm();
        form.nome = empresa.getNome();
        form.email = empresa.getEmail();
        form.login = empresa.getLogin();
        return form;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
