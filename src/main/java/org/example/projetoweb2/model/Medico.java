package org.example.projetoweb2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

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
     * Registro do médico no Conselho Regional de Medicina (ex.: CRM-SP 12345).
     * Único entre os médicos: unique = true no banco e verificação no controller,
     * para mostrar a mensagem no formulário. O formato é validado no MedicoForm.
     */
    @Column(unique = true)
    private String crm;

    /** Especialidade exibida junto ao nome no agendamento (ex.: Cardiologia). */
    private String especialidade;

    /** Valor padrão da consulta deste médico: preenche o valor sugerido no agendamento. */
    private BigDecimal valorConsulta;

    /** Lista de consultas vinculadas a este médico. */
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultaList = new ArrayList<>();

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
}
