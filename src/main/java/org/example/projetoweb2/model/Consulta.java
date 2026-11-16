package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

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
 * A entidade não é usada diretamente no formulário: os dados digitados chegam
 * em um ConsultaForm (DTO), que concentra as validações (Bean Validation).
 * Paciente e médico são verificados no ConsultaController (existência no banco).
 */
@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Dia da consulta. */
    private LocalDate data;

    /** Horário da consulta (ex.: 09:30), escolhido na lista de horários da clínica. */
    private LocalTime horario;

    /** BigDecimal é o tipo adequado para dinheiro (não tem erro de arredondamento como o Double). */
    private BigDecimal valor;

    /** @Enumerated(STRING) grava o nome ("RETORNO") no banco, e não a posição (0, 1...). */
    @Enumerated(EnumType.STRING)
    private TipoConsulta tipo;

    /** Toda consulta nova começa como AGENDADA (definido no ConsultaController). */
    @Enumerated(EnumType.STRING)
    private StatusConsulta status;

    private String observacao;

    @ManyToOne
    private Paciente paciente;

    @ManyToOne
    private Medico medico;

    public Long getId() {
        return id;
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
