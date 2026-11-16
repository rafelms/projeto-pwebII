package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

/**
 * Representa uma pessoa jurídica no sistema.
 * Herda os atributos comuns de Pessoa (id, email, telefone)
 * e adiciona atributos específicos de pessoa jurídica: razão social e CNPJ.
 *
 * A anotação @PrimaryKeyJoinColumn indica que a coluna 'id' desta tabela
 * é tanto a chave primária quanto a chave estrangeira que referencia
 * a tabela 'pessoa' (superclasse).
 *
 * Hierarquia: Pessoa -> PessoaJuridica
 *
 * Não há formulário de pessoa jurídica: a clínica é cadastrada pelo import.sql
 * e apenas lida (rodapé), por isso a classe não tem setters nem validações.
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class PessoaJuridica extends Pessoa {

    /** Razão social da clínica, exibida no rodapé. */
    private String razaoSocial;

    /** CNPJ no formato 99.999.999/9999-99. */
    private String cnpj;

    /**
     * Retorna a razão social da pessoa jurídica.
     *
     * @return razão social
     */
    public String getRazaoSocial() {
        return razaoSocial;
    }

    /**
     * Retorna o CNPJ da pessoa jurídica.
     *
     * @return cnpj da empresa
     */
    public String getCnpj() {
        return cnpj;
    }
}
