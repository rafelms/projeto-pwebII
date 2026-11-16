package org.example.projetoweb2.model;

/**
 * Papéis (roles) de acesso ao sistema.
 *
 * É um enum para que os nomes fiquem em um único lugar e sejam imutáveis:
 * o compilador impede um papel digitado errado (ex.: "MEDCO").
 * Cada valor é gravado pelo nome na tabela perfil, SEM o prefixo "ROLE_".
 *
 * - ADMIN:    acesso total (inclusive cadastro de médicos, exclusões, usuários e console H2);
 * - RECEPCAO: cadastra e edita pacientes e consultas; apenas visualiza médicos;
 * - MEDICO:   apenas visualiza pacientes, médicos e consultas;
 * - PACIENTE: usuário comum, acessa somente "Meus dados" (seus dados e suas consultas).
 */
public enum Papel {

    ADMIN,
    RECEPCAO,
    MEDICO,
    PACIENTE
}
