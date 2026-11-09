package org.example.projetoweb2.model;

/**
 * Tipo da consulta: primeira consulta com o médico ou retorno.
 */
public enum TipoConsulta {

    PRIMEIRA_CONSULTA("Primeira consulta"),
    RETORNO("Retorno");

    private final String descricao;

    TipoConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
