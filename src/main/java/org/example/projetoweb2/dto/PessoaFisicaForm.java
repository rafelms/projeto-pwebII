package org.example.projetoweb2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.projetoweb2.model.PessoaFisica;

/**
 * Dados do formulário de pessoa física (DTO), usado no cadastro e na edição de pacientes.
 * O MedicoForm herda desta classe e acrescenta os campos do médico.
 *
 * Não é uma entidade: o formulário NÃO é ligado direto à entidade JPA. Assim:
 * - só os campos declarados aqui podem ser enviados pelo navegador (ninguém altera
 *   perfis, consultas ou o id de outra pessoa adulterando o HTML);
 * - a senha digitada fica só aqui; a entidade Usuario guarda apenas o hash;
 * - as validações (Bean Validation) ficam em um único lugar.
 *
 * O controller valida este objeto com @Valid e depois copia os dados para a entidade.
 */
public class PessoaFisicaForm {

    /** Id da pessoa em edição (null no cadastro). O controller usa sempre o id da URL. */
    private Long id;

    /** Nome obrigatório, entre 3 e 100 caracteres. */
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre {min} e {max} caracteres.")
    private String nome;

    /** CPF obrigatório no formato 999.999.999-99. A unicidade é verificada no controller. */
    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "O CPF deve estar no formato 999.999.999-99.")
    private String cpf;

    /** Email obrigatório, em formato válido e com no máximo 100 caracteres. */
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Informe um email válido (ex.: nome@dominio.com).")
    @Size(max = 100, message = "O email deve ter no máximo {max} caracteres.")
    private String email;

    /** Telefone obrigatório no formato (99) 9999-9999 ou (99) 99999-9999. */
    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
            message = "O telefone deve estar no formato (99) 99999-9999 ou (99) 9999-9999.")
    private String telefone;

    /** Nome de login da conta de acesso: obrigatório, sem espaços. A unicidade é verificada no UsuarioService. */
    @NotBlank(message = "O usuário é obrigatório.")
    @Size(min = 3, max = 50, message = "O usuário deve ter entre {min} e {max} caracteres.")
    @Pattern(regexp = "[a-zA-Z0-9._-]*", message = "O usuário não pode ter espaços nem acentos.")
    private String usuario;

    /**
     * Senha digitada (texto puro, nunca gravada assim). Em branco chega como null
     * (GlobalBindingAdvice) e o @Size é ignorado: na edição, isso significa "manter a senha atual".
     * A obrigatoriedade ao criar a conta é verificada no UsuarioService.
     */
    @Size(min = 3, max = 50, message = "A senha deve ter entre {min} e {max} caracteres.")
    private String senha;

    /**
     * Preenche o formulário com os dados já gravados (tela de edição).
     * A senha nunca é carregada: o banco só tem o hash.
     *
     * @param pessoa paciente ou médico vindo do banco
     */
    public void carregar(PessoaFisica pessoa) {
        id = pessoa.getId();
        nome = pessoa.getNome();
        cpf = pessoa.getCpf();
        email = pessoa.getEmail();
        telefone = pessoa.getTelefone();
        if (pessoa.getUsuario() != null) {
            usuario = pessoa.getUsuario().getUsuario();
        }
    }

    /**
     * Copia os dados pessoais digitados para a entidade.
     * A conta (login e senha) é montada à parte, pelo UsuarioService.
     *
     * @param pessoa paciente ou médico que será gravado
     */
    public void aplicar(PessoaFisica pessoa) {
        pessoa.setNome(nome);
        pessoa.setCpf(cpf);
        pessoa.setEmail(email);
        pessoa.setTelefone(telefone);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
