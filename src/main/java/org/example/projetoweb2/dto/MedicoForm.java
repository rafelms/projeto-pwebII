package org.example.projetoweb2.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.projetoweb2.model.Medico;

import java.math.BigDecimal;

/**
 * Dados do formulário de médico (DTO).
 *
 * Herda os campos de pessoa física e da conta de acesso (PessoaFisicaForm)
 * e acrescenta os que só o médico tem: CRM, especialidade e valor da consulta.
 */
public class MedicoForm extends PessoaFisicaForm {

    /** CRM obrigatório no formato CRM-UF 99999 (ex.: CRM-SP 12345). A unicidade é verificada no controller. */
    @NotBlank(message = "O CRM é obrigatório.")
    @Pattern(regexp = "CRM-[A-Z]{2} \\d{4,6}",
            message = "O CRM deve estar no formato CRM-UF 99999 (ex.: CRM-SP 12345).")
    private String crm;

    @NotBlank(message = "A especialidade é obrigatória.")
    @Size(max = 60, message = "A especialidade deve ter no máximo {max} caracteres.")
    private String especialidade;

    /** BigDecimal é o tipo adequado para dinheiro (não tem erro de arredondamento como o Double). */
    @NotNull(message = "O valor da consulta é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    @DecimalMax(value = "10000.00", message = "O valor não pode ultrapassar R$ {value}.")
    private BigDecimal valorConsulta;

    /**
     * Preenche o formulário com os dados do médico gravado (tela de edição).
     *
     * @param medico médico vindo do banco
     */
    public void carregar(Medico medico) {
        super.carregar(medico);
        crm = medico.getCrm();
        especialidade = medico.getEspecialidade();
        valorConsulta = medico.getValorConsulta();
    }

    /**
     * Copia os dados digitados para a entidade.
     *
     * @param medico médico que será gravado
     */
    public void aplicar(Medico medico) {
        super.aplicar(medico);
        medico.setCrm(crm);
        medico.setEspecialidade(especialidade);
        medico.setValorConsulta(valorConsulta);
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
}
