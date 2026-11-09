package org.example.projetoweb2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Conta de acesso ao sistema (credenciais de login).
 *
 * - usuario: nome de login, único no sistema;
 * - senha:   guardada como HASH BCrypt (60 caracteres), nunca em texto puro;
 * - perfis:  papéis do usuário (relacionamento muitos-para-muitos com Perfil).
 *
 * A classe NÃO implementa interfaces do Spring Security: a conversão para
 * o formato que o Spring entende (UserDetails) é feita no UsuarioDetailsService.
 */
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome de login: obrigatório, único, sem espaços. */
    @NotBlank(message = "O usuário é obrigatório.")
    @Size(min = 3, max = 50, message = "O usuário deve ter entre {min} e {max} caracteres.")
    @Pattern(regexp = "[a-zA-Z0-9._-]*", message = "O usuário não pode ter espaços nem acentos.")
    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    /**
     * Hash da senha. Não tem anotação de validação porque o valor gravado é o hash;
     * a senha digitada no formulário é validada no UsuarioService.
     * O tamanho 100 deixa margem acima dos 60 caracteres do BCrypt.
     */
    @Column(nullable = false, length = 100)
    private String senha;

    /**
     * Lado "dono" do relacionamento muitos-para-muitos:
     * - um usuário pode ter vários perfis;
     * - um perfil pode pertencer a vários usuários.
     * O @JoinTable define a tabela intermediária usuario_perfil (usuario_id, perfil_id).
     */
    @ManyToMany
    @JoinTable(name = "usuario_perfil",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id"))
    private List<Perfil> perfis = new ArrayList<>();

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Usuario() {
    }

    /**
     * Cria um usuário com login e senha (a senha já deve vir codificada).
     *
     * @param usuario nome de login
     * @param senha   hash da senha
     */
    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    /**
     * Adiciona um perfil ao usuário, mantendo os dois lados do relacionamento atualizados.
     *
     * @param perfil perfil a ser atribuído
     */
    public void adicionarPerfil(Perfil perfil) {
        perfis.add(perfil);
        perfil.getUsuarios().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Perfil> getPerfis() {
        return perfis;
    }
}
