package org.example.projetoweb2.controller;

import org.example.projetoweb2.model.Paciente;
import org.example.projetoweb2.repository.PacienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsável pelas operações CRUD da entidade Paciente.
 * Gerencia as requisições HTTP e direciona para as views Thymeleaf correspondentes.
 */
@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    /** Repositório para acesso aos dados de Paciente. */
    private final PacienteRepository repository;

    /**
     * Construtor com injeção de dependência do repositório.
     *
     * @param repository repositório de pacientes
     */
    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todos os pacientes cadastrados.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de pacientes
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", repository.findAll());
        return "paciente/list";
    }

    /**
     * Exibe o formulário para cadastrar um novo paciente.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view do formulário de paciente
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "paciente/form";
    }

    /**
     * Exibe o formulário preenchido para editar um paciente existente.
     *
     * @param id    identificador do paciente a ser editado
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view do formulário de paciente
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));
        model.addAttribute("paciente", paciente);
        return "paciente/form";
    }

    /**
     * Salva um paciente novo ou atualiza um existente no banco de dados.
     *
     * @param paciente objeto paciente recebido do formulário
     * @return redirecionamento para a listagem de pacientes
     */
    @PostMapping("/salvar")
    public String salvar(Paciente paciente) {
        repository.save(paciente);
        return "redirect:/pacientes";
    }

    /**
     * Exclui um paciente pelo seu identificador.
     *
     * @param id identificador do paciente a ser excluído
     * @return redirecionamento para a listagem de pacientes
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/pacientes";
    }
}
