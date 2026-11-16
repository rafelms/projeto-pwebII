package org.example.projetoweb2.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados do formulário "Alterar senha" (em Meus dados): a nova senha e a confirmação
 * (herdadas de {@link RedefinirSenha}) mais a senha atual.
 *
 * Não é uma entidade: só recebe os três campos digitados.
 * A conferência da senha atual e da confirmação é feita no UsuarioService.
 */
public class AlterarSenha extends RedefinirSenha {

    @NotBlank(message = "Informe a senha atual.")
    private String senhaAtual;

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }
}
