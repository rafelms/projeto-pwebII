package org.example.projetoweb2.controller;

import org.example.projetoweb2.model.PessoaJuridica;
import org.example.projetoweb2.repository.PessoaJuridicaRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller global para PessoaJuridica.
 *
 * Utiliza @ControllerAdvice para disponibilizar os dados de contato
 * da clínica (pessoa jurídica) em todas as views do sistema,
 * permitindo que o rodapé exiba essas informações automaticamente.
 */
@ControllerAdvice
public class PessoaJuridicaController {

    private final PessoaJuridicaRepository repository;

    /**
     * Construtor com injeção de dependência do repositório.
     *
     * @param repository repositório de pessoas jurídicas
     */
    public PessoaJuridicaController(PessoaJuridicaRepository repository) {
        this.repository = repository;
    }

    /**
     * Disponibiliza a primeira pessoa jurídica cadastrada como atributo
     * global do model, acessível em todas as views Thymeleaf sob o nome "clinica".
     *
     * @return a pessoa jurídica encontrada, ou null se nenhuma estiver cadastrada
     */
    @ModelAttribute("clinica")
    public PessoaJuridica carregarClinica() {
        return repository.findAll()
                .stream()
                .findFirst()
                .orElse(null);
    }
}
