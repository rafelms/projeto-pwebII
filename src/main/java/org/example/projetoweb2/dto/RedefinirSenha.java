package org.example.projetoweb2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Nova senha de uma conta, com a confirmação.
 *
 * É usado pelo administrador na lista de usuários (não exige a senha atual, pois ele
 * não a conhece) e serve de base para {@link AlterarSenha}, em que o próprio usuário
 * informa também a senha atual.
 */
public class RedefinirSenha {

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 3, max = 50, message = "A nova senha deve ter entre {min} e {max} caracteres.")
    private String novaSenha;

    @NotBlank(message = "Repita a nova senha.")
    private String confirmacao;

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
