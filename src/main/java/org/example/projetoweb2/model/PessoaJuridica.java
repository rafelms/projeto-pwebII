package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

    /** Razão social obrigatória, entre 3 e 150 caracteres. */
    @NotBlank(message = "A razão social é obrigatória.")
    @Size(min = 3, max = 150, message = "A razão social deve ter entre {min} e {max} caracteres.")
    private String razaoSocial;

    /** CNPJ obrigatório no formato 99.999.999/9999-99. */
    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
            message = "O CNPJ deve estar no formato 99.999.999/9999-99.")
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
