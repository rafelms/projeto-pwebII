package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Representa uma pessoa física no sistema.
 * Herda os atributos comuns de Pessoa (id, email, telefone)
 * e adiciona atributos específicos de pessoa física: nome e CPF.
 *
 * A anotação @PrimaryKeyJoinColumn indica que a coluna 'id' desta tabela
 * é tanto a chave primária quanto a chave estrangeira que referencia
 * a tabela 'pessoa' (superclasse).
 *
 * Hierarquia: Pessoa -> PessoaFisica
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class PessoaFisica extends Pessoa {

    /** Nome obrigatório, entre 3 e 100 caracteres. */
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre {min} e {max} caracteres.")
    private String nome;

    /** CPF obrigatório no formato 999.999.999-99 (unicidade verificada no controller). */
    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "O CPF deve estar no formato 999.999.999-99.")
    private String cpf;

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public PessoaFisica() {
    }

    /**
     * Retorna o nome da pessoa física.
     *
     * @return nome da pessoa
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da pessoa física.
     *
     * @param nome novo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o CPF da pessoa física.
     *
     * @return cpf da pessoa
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o CPF da pessoa física.
     *
     * @param cpf novo CPF
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
