package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

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

    private String email;

    private String telefone;

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
}
