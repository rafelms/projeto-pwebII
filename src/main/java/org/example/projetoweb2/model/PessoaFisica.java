package org.example.projetoweb2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

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

    /** Nome da pessoa (validado no PessoaFisicaForm). */
    private String nome;

    /**
     * CPF no formato 999.999.999-99 (validado no PessoaFisicaForm).
     * É único entre TODAS as pessoas físicas (pacientes e médicos): unique = true no banco
     * e verificação no controller, para mostrar a mensagem no formulário.
     */
    @Column(unique = true)
    private String cpf;

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
