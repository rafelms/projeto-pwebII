package org.example.projetoweb2.controller;

import org.example.projetoweb2.model.Consulta;
import org.example.projetoweb2.repository.ConsultaRepository;
import org.example.projetoweb2.repository.MedicoRepository;
import org.example.projetoweb2.repository.PacienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsável pelas operações CRUD da entidade Consulta.
 * Gerencia as requisições HTTP e direciona para as views Thymeleaf correspondentes.
 */
@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    /** Repositório para acesso aos dados de Consulta. */
    private final ConsultaRepository consultaRepository;

    /** Repositório para acesso aos dados de Paciente (usado no formulário). */
    private final PacienteRepository pacienteRepository;

    /** Repositório para acesso aos dados de Medico (usado no formulário). */
    private final MedicoRepository medicoRepository;

    /**
     * Construtor com injeção de dependência dos repositórios.
     *
     * @param consultaRepository repositório de consultas
     * @param pacienteRepository repositório de pacientes
     * @param medicoRepository   repositório de médicos
     */
    public ConsultaController(ConsultaRepository consultaRepository,
                              PacienteRepository pacienteRepository,
                              MedicoRepository medicoRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    /**
     * Lista todas as consultas cadastradas.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de consultas
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultas", consultaRepository.findAll());
        return "consulta/list";
    }

    /**
     * Exibe o formulário para cadastrar uma nova consulta.
     * Também carrega as listas de pacientes e médicos para os selects do formulário.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view do formulário de consulta
     */
    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "consulta/form";
    }

    /**
     * Exibe o formulário preenchido para editar uma consulta existente.
     * Também carrega as listas de pacientes e médicos para os selects do formulário.
     *
     * @param id    identificador da consulta a ser editada
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view do formulário de consulta
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + id));
        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "consulta/form";
    }

    /**
     * Salva uma consulta nova ou atualiza uma existente no banco de dados.
     *
     * @param consulta objeto consulta recebido do formulário
     * @return redirecionamento para a listagem de consultas
     */
    @PostMapping("/salvar")
    public String salvar(Consulta consulta) {
        consultaRepository.save(consulta);
        return "redirect:/consultas";
    }

    /**
     * Exclui uma consulta pelo seu identificador.
     *
     * @param id identificador da consulta a ser excluída
     * @return redirecionamento para a listagem de consultas
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        consultaRepository.deleteById(id);
        return "redirect:/consultas";
    }
}
