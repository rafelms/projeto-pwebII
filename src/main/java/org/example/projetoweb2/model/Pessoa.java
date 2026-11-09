package org.example.projetoweb2.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Classe base da hierarquia de herança.
 * Representa uma pessoa genérica com os atributos comuns
 * compartilhados por todas as subclasses (PessoaFisica, PessoaJuridica, etc).
 *
 * Utiliza a estratégia JOINED do JPA, onde cada classe da hierarquia
 * possui sua própria tabela no banco de dados. As tabelas filhas são
 * unidas à tabela pai através da chave primária.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    /**
     * Conta de acesso da pessoa (relacionamento um-para-um com Usuario).
     *
     * - cascade = ALL: ao salvar/excluir a pessoa, o usuário é salvo/excluído junto;
     * - @JoinColumn: cria a coluna usuario_id na tabela pessoa (unique = uma conta por pessoa);
     * - @Valid: aplica também as validações da classe Usuario (ex.: @NotBlank no login).
     * É opcional (pode ser null), pois nem toda pessoa precisa acessar o sistema.
     */
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Pessoa() {
    }

    /**
     * Retorna o identificador único da pessoa.
     *
     * @return id da pessoa
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador único da pessoa.
     *
     * @param id novo id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o email da pessoa.
     *
     * @return email da pessoa
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o email da pessoa.
     *
     * @param email novo email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna o telefone da pessoa.
     *
     * @return telefone da pessoa
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o telefone da pessoa.
     *
     * @param telefone novo telefone
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Retorna a conta de acesso da pessoa.
     *
     * @return usuário vinculado (ou null se a pessoa não tiver conta)
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Define a conta de acesso da pessoa.
     *
     * @param usuario novo usuário vinculado
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
