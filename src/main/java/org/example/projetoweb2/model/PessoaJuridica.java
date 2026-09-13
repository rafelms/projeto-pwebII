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
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class PessoaJuridica extends Pessoa {

    private String razaoSocial;

    private String cnpj;

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public PessoaJuridica() {
    }

    /**
     * Retorna a razão social da pessoa jurídica.
     *
     * @return razão social
     */
    public String getRazaoSocial() {
        return razaoSocial;
    }

    /**
     * Define a razão social da pessoa jurídica.
     *
     * @param razaoSocial nova razão social
     */
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    /**
     * Retorna o CNPJ da pessoa jurídica.
     *
     * @return cnpj da empresa
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * Define o CNPJ da pessoa jurídica.
     *
     * @param cnpj novo CNPJ
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    /**
     * Retorna uma string com os dados resumidos da pessoa jurídica.
     *
     * @return string formatada com id, razão social e CNPJ
     */
    public String dados() {
        return "PessoaJuridica [ID=" + getId()
                + ", Razão Social=" + razaoSocial
                + ", CNPJ=" + cnpj
                + ", Email=" + getEmail()
                + ", Telefone=" + getTelefone() + "]";
    }
}
