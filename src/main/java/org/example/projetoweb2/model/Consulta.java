package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Consulta de um paciente com um médico.
 *
 * Data e horário ficam separados: a data vem de um input date e o horário
 * de uma lista de horários fixos (de 30 em 30 minutos), o que permite
 * verificar conflito de agenda comparando exatamente o mesmo horário.
 *
 * Paciente e médico são verificados no ConsultaController (existência no banco).
 */
@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Dia da consulta, no formato do input date do HTML (ex.: 2026-11-20). */
    @NotNull(message = "A data da consulta é obrigatória.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data;

    /** Horário da consulta (ex.: 09:30), escolhido na lista de horários da clínica. */
    @NotNull(message = "Selecione um horário.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horario;

    /** BigDecimal é o tipo adequado para dinheiro (não tem erro de arredondamento como o Double). */
    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    @DecimalMax(value = "10000.00", message = "O valor não pode ultrapassar R$ {value}.")
    private BigDecimal valor;

    /** @Enumerated(STRING) grava o nome ("RETORNO") no banco, e não a posição (0, 1...). */
    @NotNull(message = "Selecione o tipo da consulta.")
    @Enumerated(EnumType.STRING)
    private TipoConsulta tipo;

    /** Toda consulta nova começa como AGENDADA. */
    @NotNull(message = "Selecione a situação da consulta.")
    @Enumerated(EnumType.STRING)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @Size(max = 255, message = "A observação deve ter no máximo {max} caracteres.")
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoConsulta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConsulta tipo) {
        this.tipo = tipo;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
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
}
