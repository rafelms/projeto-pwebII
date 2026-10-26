package org.example.projetoweb2.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Objeto de filtro (DTO) usado nas listagens de pacientes e de médicos.
 *
 * O campo é opcional: quando vazio, o GlobalBindingAdvice o converte em null
 * e as anotações abaixo não são aplicadas (Bean Validation ignora null
 * em @Size e @Pattern), listando todos os registros.
 */
public class FiltroPessoa {

    /** Trecho do nome a ser buscado (sem diferenciar maiúsculas/minúsculas). */
    @Size(min = 2, max = 100, message = "A busca deve ter entre {min} e {max} caracteres.")
    @Pattern(regexp = "[\\p{L} .'-]+", message = "A busca por nome deve conter apenas letras.")
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
