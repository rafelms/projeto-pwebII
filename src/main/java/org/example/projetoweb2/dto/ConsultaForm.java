package org.example.projetoweb2.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.projetoweb2.model.Consulta;
import org.example.projetoweb2.model.Medico;
import org.example.projetoweb2.model.Paciente;
import org.example.projetoweb2.model.StatusConsulta;
import org.example.projetoweb2.model.TipoConsulta;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dados do formulário de consulta (DTO).
 *
 * Não é uma entidade: recebe o que foi digitado e concentra as validações de campo.
 * Paciente e médico chegam só como id (o valor do select); o ConsultaController
 * busca os dois no banco e aplica as regras de agenda antes de gravar a entidade Consulta.
 */
public class ConsultaForm {

    /** Id da consulta em edição (null no cadastro). O controller usa sempre o id da URL. */
    private Long id;

    @NotNull(message = "Selecione um paciente.")
    private Long pacienteId;

    @NotNull(message = "Selecione um médico.")
    private Long medicoId;

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

    @NotNull(message = "Selecione o tipo da consulta.")
    private TipoConsulta tipo;

    /**
     * Começa como AGENDADA porque o formulário de consulta nova não envia a situação
     * (ela só pode ser alterada na edição).
     */
    @NotNull(message = "Selecione a situação da consulta.")
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @Size(max = 255, message = "A observação deve ter no máximo {max} caracteres.")
    private String observacao;

    /**
     * Preenche o formulário com os dados da consulta gravada (tela de edição).
     *
     * @param consulta consulta vinda do banco
     */
    public void carregar(Consulta consulta) {
        id = consulta.getId();
        pacienteId = consulta.getPaciente().getId();
        medicoId = consulta.getMedico().getId();
        data = consulta.getData();
        horario = consulta.getHorario();
        valor = consulta.getValor();
        tipo = consulta.getTipo();
        status = consulta.getStatus();
        observacao = consulta.getObservacao();
    }

    /**
     * Copia os dados digitados para a entidade.
     *
     * @param consulta consulta que será gravada
     * @param paciente paciente já buscado no banco pelo controller
     * @param medico   médico já buscado no banco pelo controller
     */
    public void aplicar(Consulta consulta, Paciente paciente, Medico medico) {
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setData(data);
        consulta.setHorario(horario);
        consulta.setValor(valor);
        consulta.setTipo(tipo);
        consulta.setStatus(status);
        consulta.setObservacao(observacao);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
