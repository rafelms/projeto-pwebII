package org.example.projetoweb2.model;

/**
 * Situação de uma consulta.
 *
 * - AGENDADA e CONFIRMADA: consulta "em aberto", precisa estar no futuro;
 * - REALIZADA e FALTOU: consulta encerrada, só pode ser marcada depois do horário;
 * - CANCELADA: libera o horário para outra consulta.
 *
 * A cor é a classe do Bootstrap usada no badge da listagem.
 */
public enum StatusConsulta {

    AGENDADA("Agendada", "text-bg-primary"),
    CONFIRMADA("Confirmada", "text-bg-info"),
    REALIZADA("Realizada", "text-bg-success"),
    FALTOU("Paciente faltou", "text-bg-warning"),
    CANCELADA("Cancelada", "text-bg-secondary");

    private final String descricao;

    private final String cor;

    StatusConsulta(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCor() {
        return cor;
    }

    /**
     * Indica se a consulta ainda vai acontecer (agendada ou confirmada).
     *
     * @return true para AGENDADA e CONFIRMADA
     */
    public boolean isEmAberto() {
        return this == AGENDADA || this == CONFIRMADA;
    }

    /**
     * Indica se a consulta já aconteceu (realizada ou falta do paciente).
     *
     * @return true para REALIZADA e FALTOU
     */
    public boolean isEncerrada() {
        return this == REALIZADA || this == FALTOU;
    }
}
