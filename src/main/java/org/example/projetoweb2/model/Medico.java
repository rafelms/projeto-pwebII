package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um médico da clínica.
 * Herda de PessoaFisica, que por sua vez herda de Pessoa,
 * formando a hierarquia: Pessoa -> PessoaFisica -> Medico.
 *
 * Com a estratégia JOINED, o JPA cria uma tabela separada para Medico.
 * A anotação @PrimaryKeyJoinColumn indica que a coluna 'id' desta tabela
 * é a chave estrangeira que referencia a tabela 'pessoa_fisica'.
 *
 * Atributos herdados de Pessoa: id, email, telefone, usuario
 * Atributos herdados de PessoaFisica: nome, cpf
 * Atributos próprios: crm, especialidade, valorConsulta, consultaList
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Medico extends PessoaFisica {

    /**
     * Registro do médico no Conselho Regional de Medicina.
     * Obrigatório no formato CRM-UF 999999 (ex.: CRM-SP 12345).
     */
    @NotBlank(message = "O CRM é obrigatório.")
    @Pattern(regexp = "CRM-[A-Z]{2} \\d{4,6}",
            message = "O CRM deve estar no formato CRM-UF 99999 (ex.: CRM-SP 12345).")
    private String crm;

    /** Especialidade exibida junto ao nome no agendamento (ex.: Cardiologia). */
    @NotBlank(message = "A especialidade é obrigatória.")
    @Size(max = 60, message = "A especialidade deve ter no máximo {max} caracteres.")
    private String especialidade;

    /** Valor padrão da consulta deste médico: preenche o valor sugerido no agendamento. */
    @NotNull(message = "O valor da consulta é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    @DecimalMax(value = "10000.00", message = "O valor não pode ultrapassar R$ {value}.")
    private BigDecimal valorConsulta;

    /** Lista de consultas vinculadas a este médico. */
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultaList = new ArrayList<>();

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Medico() {
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public BigDecimal getValorConsulta() {
        return valorConsulta;
    }

    public void setValorConsulta(BigDecimal valorConsulta) {
        this.valorConsulta = valorConsulta;
    }

    public List<Consulta> getConsultaList() {
        return consultaList;
    }

    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }
}
