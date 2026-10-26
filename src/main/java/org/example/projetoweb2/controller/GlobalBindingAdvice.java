package org.example.projetoweb2.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Configuração global de binding aplicada a todos os controllers.
 *
 * Registra o StringTrimmerEditor, que remove espaços nas pontas de todo
 * texto vindo dos formulários e transforma textos vazios em null.
 * Assim, um nome digitado como "   " é tratado como não preenchido
 * e barrado pelo @NotBlank, e filtros vazios são simplesmente ignorados.
 */
@ControllerAdvice
public class GlobalBindingAdvice {

    /**
     * Executado antes do binding de cada requisição.
     *
     * @param binder objeto do Spring que converte os parâmetros da requisição em objetos Java
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
