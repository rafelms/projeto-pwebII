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

    /** Email de contato (validado no PessoaFisicaForm). */
    private String email;

    /** Telefone no formato (99) 9999-9999 ou (99) 99999-9999 (validado no PessoaFisicaForm). */
    private String telefone;

    /**
     * Conta de acesso da pessoa (relacionamento um-para-um com Usuario).
     *
     * - cascade = ALL: ao salvar/excluir a pessoa, o usuário é salvo/excluído junto;
     * - @JoinColumn: cria a coluna usuario_id na tabela pessoa (unique = uma conta por pessoa).
     * No banco a coluna aceita null (a clínica e parte das pessoas do import.sql não têm conta),
     * mas os formulários de paciente e médico sempre gravam a conta junto com a pessoa.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    /**
     * Retorna o identificador único da pessoa.
     *
     * @return id da pessoa
     */
    public Long getId() {
        return id;
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
