package org.example.projetoweb2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados do formulário "Alterar senha" (em Meus dados).
 *
 * Não é uma entidade: só recebe os três campos digitados.
 * A conferência da senha atual e da confirmação é feita no UsuarioService.
 */
public class AlterarSenha {

    @NotBlank(message = "Informe a senha atual.")
    private String senhaAtual;

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 3, max = 50, message = "A nova senha deve ter entre {min} e {max} caracteres.")
    private String novaSenha;

    @NotBlank(message = "Repita a nova senha.")
    private String confirmacao;

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getConfirmacao() {
        return confirmacao;
    }

    public void setConfirmacao(String confirmacao) {
        this.confirmacao = confirmacao;
    }
}
