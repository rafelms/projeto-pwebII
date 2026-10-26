package org.example.projetoweb2.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import org.example.projetoweb2.validation.PeriodoValido;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Objeto de filtro (DTO) da listagem de consultas.
 *
 * Não é uma entidade JPA: serve apenas para receber os parâmetros
 * da busca (query string do GET /consultas) e validá-los com @Valid.
 *
 * A anotação @PeriodoValido (customizada) garante que dataFim >= dataInicio.
 */
@PeriodoValido
public class FiltroConsulta {

    /** Data inicial do período (input type="date" envia yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataInicio;

    /** Data final do período (input type="date" envia yyyy-MM-dd). */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFim;

    /** Identificador do paciente selecionado no filtro (opcional). */
    @Positive(message = "Paciente inválido.")
    private Long pacienteId;

    /** Identificador do médico selecionado no filtro (opcional). */
    @Positive(message = "Médico inválido.")
    private Long medicoId;

    /** Valor mínimo da consulta (opcional). */
    @DecimalMin(value = "0.00", message = "O valor mínimo não pode ser negativo.")
    private Double valorMinimo;

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Double getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(Double valorMinimo) {
        this.valorMinimo = valorMinimo;
    }
}
