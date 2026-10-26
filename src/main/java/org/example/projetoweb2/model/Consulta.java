package org.example.projetoweb2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data e hora da consulta. O padrão do @DateTimeFormat é o mesmo
     * enviado pelo input datetime-local do HTML (ex.: 2026-09-01T09:00).
     */
    @NotNull(message = "A data e hora da consulta são obrigatórias.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime data;

    /**
     * Valor da consulta. Usa o wrapper Double (e não o primitivo double)
     * para que um campo vazio chegue como null e seja barrado pelo @NotNull.
     */
    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    @DecimalMax(value = "10000.00", message = "O valor não pode ultrapassar R$ {value}.")
    private Double valor;

    @Size(max = 255, message = "A observação deve ter no máximo {max} caracteres.")
    private String observacao;

    /** Paciente obrigatório (existência verificada no ConsultaController). */
    @NotNull(message = "Selecione um paciente.")
    @ManyToOne
    private Paciente paciente;

    /** Médico obrigatório (existência verificada no ConsultaController). */
    @NotNull(message = "Selecione um médico.")
    @ManyToOne
    private Medico medico;

    public Consulta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

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
