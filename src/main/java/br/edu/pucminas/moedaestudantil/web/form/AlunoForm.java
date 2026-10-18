package br.edu.pucminas.moedaestudantil.web.form;

import br.edu.pucminas.moedaestudantil.model.Aluno;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AlunoForm {

    @NotBlank(message = "Informe o nome.")
    @Size(max = 120, message = "O nome deve ter até 120 caracteres.")
    private String nome = "";

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 160, message = "O e-mail deve ter até 160 caracteres.")
    private String email = "";

    @NotBlank(message = "Informe o CPF.")
    @Pattern(regexp = "(?:\\d{11}|\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2})", message = "Informe um CPF com 11 dígitos.")
    private String cpf = "";

    @NotBlank(message = "Informe o RG.")
    @Size(max = 20, message = "O RG deve ter até 20 caracteres.")
    private String rg = "";

    @NotBlank(message = "Informe o endereço.")
    @Size(max = 240, message = "O endereço deve ter até 240 caracteres.")
    private String endereco = "";

    @NotBlank(message = "Informe o curso.")
    @Size(max = 120, message = "O curso deve ter até 120 caracteres.")
    private String curso = "";

    @NotBlank(message = "Informe o login.")
    @Size(min = 4, max = 80, message = "O login deve ter entre 4 e 80 caracteres.")
    @Pattern(regexp = "[A-Za-z0-9._-]+", message = "Use apenas letras, números, ponto, hífen ou sublinhado.")
    private String login = "";

    @Pattern(regexp = "^$|.{8,72}", message = "A senha deve ter entre 8 e 72 caracteres.")
    private String senha = "";

    @NotNull(message = "Selecione uma instituição.")
    private Long instituicaoId;

    public static AlunoForm from(Aluno aluno) {
        AlunoForm form = new AlunoForm();
        form.nome = aluno.getNome();
        form.email = aluno.getEmail();
        form.cpf = aluno.getCpf();
        form.rg = aluno.getRg();
        form.endereco = aluno.getEndereco();
        form.curso = aluno.getCurso();
        form.login = aluno.getLogin();
        form.instituicaoId = aluno.getInstituicao().getId();
        return form;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Long getInstituicaoId() { return instituicaoId; }
    public void setInstituicaoId(Long instituicaoId) { this.instituicaoId = instituicaoId; }
}
