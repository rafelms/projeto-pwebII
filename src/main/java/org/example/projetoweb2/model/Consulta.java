package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidade que representa uma Consulta médica no sistema da clínica.
 * Possui relacionamento muitos para um com Paciente e Médico.
 */
@Entity
public class Consulta {

    /** Identificador único da consulta (chave primária). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Data e hora da consulta. */
    private LocalDateTime data;

    /** Valor cobrado pela consulta. */
    private double valor;

    /** Observações adicionais sobre a consulta. */
    private String observacao;

    /** Paciente vinculado a esta consulta. */
    @ManyToOne
    private Paciente paciente;

    /** Médico responsável por esta consulta. */
    @ManyToOne
    private Medico medico;

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Consulta() {
    }

    /**
     * Retorna o identificador da consulta.
     *
     * @return id da consulta
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador da consulta.
     *
     * @param id identificador a ser definido
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna a data e hora da consulta.
     *
     * @return data da consulta
     */
    public LocalDateTime getData() {
        return data;
    }

    /**
     * Define a data e hora da consulta.
     *
     * @param data data a ser definida
     */
    public void setData(LocalDateTime data) {
        this.data = data;
    }

    /**
     * Retorna o valor da consulta.
     *
     * @return valor da consulta
     */
    public double getValor() {
        return valor;
    }

    /**
     * Define o valor da consulta.
     *
     * @param valor valor a ser definido
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * Retorna a observação da consulta.
     *
     * @return observação da consulta
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Define a observação da consulta.
     *
     * @param observacao observação a ser definida
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    /**
     * Retorna o paciente vinculado a esta consulta.
     *
     * @return paciente da consulta
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Define o paciente vinculado a esta consulta.
     *
     * @param paciente paciente a ser definido
     */
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    /**
     * Retorna o médico responsável por esta consulta.
     *
     * @return médico da consulta
     */
    public Medico getMedico() {
        return medico;
    }

    /**
     * Define o médico responsável por esta consulta.
     *
     * @param medico médico a ser definido
     */
    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    /**
     * Retorna a data formatada no padrão brasileiro (dd/MM/yyyy HH:mm).
     * Utilizado nas views para exibir a data de forma legível.
     *
     * @return string com a data formatada, ou vazio se a data for nula
     */
    public String getDataFormatada() {
        if (data == null) return "";
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Retorna uma string formatada com os dados da consulta,
     * incluindo data, valor, paciente e médico.
     *
     * @return string com os dados da consulta
     */
    public String dados() {
        String nomePaciente = (paciente != null) ? paciente.getNome() : "N/A";
        String nomeMedico = (medico != null) ? medico.getNome() : "N/A";
        return "Consulta [ID=" + id
                + ", Data=" + getDataFormatada()
                + ", Valor=R$" + String.format("%.2f", valor)
                + ", Paciente=" + nomePaciente
                + ", Medico=" + nomeMedico
                + ", Obs=" + observacao + "]";
    }
}
