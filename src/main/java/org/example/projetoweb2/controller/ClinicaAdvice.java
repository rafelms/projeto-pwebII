package org.example.projetoweb2.controller;

import org.example.projetoweb2.model.PessoaJuridica;
import org.example.projetoweb2.repository.PessoaJuridicaRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Disponibiliza os dados da clínica (PessoaJuridica) para todas as views.
 *
 * Utiliza @ControllerAdvice para que o rodapé exiba os dados de contato
 * da clínica em todas as telas, sem que cada controller precise carregá-los.
 */
@ControllerAdvice
public class ClinicaAdvice {

    private final PessoaJuridicaRepository repository;

    /**
     * Construtor com injeção de dependência do repositório.
     *
     * @param repository repositório de pessoas jurídicas
     */
    public ClinicaAdvice(PessoaJuridicaRepository repository) {
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
        return repository.findFirstByOrderByIdAsc().orElse(null);
    }
}
