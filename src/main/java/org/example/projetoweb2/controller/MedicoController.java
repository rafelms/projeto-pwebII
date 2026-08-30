package org.example.projetoweb2.controller;

import org.example.projetoweb2.model.Medico;
import org.example.projetoweb2.repository.MedicoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsável pelas operações CRUD da entidade Medico.
 * Gerencia as requisições HTTP e direciona para as views Thymeleaf correspondentes.
 */
@Controller
@RequestMapping("/medicos")
public class MedicoController {

    /** Repositório para acesso aos dados de Medico. */
    private final MedicoRepository repository;

    /**
     * Construtor com injeção de dependência do repositório.
     *
     * @param repository repositório de médicos
     */
    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todos os médicos cadastrados.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de médicos
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicos", repository.findAll());
        return "medico/list";
    }

    /**
     * Exibe o formulário para cadastrar um novo médico.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view do formulário de médico
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("medico", new Medico());
        return "medico/form";
    }

    /**
     * Exibe o formulário preenchido para editar um médico existente.
     *
     * @param id    identificador do médico a ser editado
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view do formulário de médico
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Medico medico = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + id));
        model.addAttribute("medico", medico);
        return "medico/form";
    }

    /**
     * Salva um médico novo ou atualiza um existente no banco de dados.
     *
     * @param medico objeto médico recebido do formulário
     * @return redirecionamento para a listagem de médicos
     */
    @PostMapping("/salvar")
    public String salvar(Medico medico) {
        repository.save(medico);
        return "redirect:/medicos";
    }

    /**
     * Exclui um médico pelo seu identificador.
     *
     * @param id identificador do médico a ser excluído
     * @return redirecionamento para a listagem de médicos
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/medicos";
    }
}
