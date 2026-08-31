package org.example.projetoweb2.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;

    private double valor;

    private String observacao;

    @ManyToOne
    private Paciente paciente;

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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
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
