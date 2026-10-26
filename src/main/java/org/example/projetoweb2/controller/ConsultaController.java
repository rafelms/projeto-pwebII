package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.FiltroConsulta;
import org.example.projetoweb2.model.Consulta;
import org.example.projetoweb2.model.Medico;
import org.example.projetoweb2.model.Paciente;
import org.example.projetoweb2.repository.ConsultaRepository;
import org.example.projetoweb2.repository.MedicoRepository;
import org.example.projetoweb2.repository.PacienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaRepository consultaRepository;

    private final PacienteRepository pacienteRepository;

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
     * Lista as consultas, aplicando os filtros informados (período, paciente,
     * médico e valor mínimo). O filtro é validado com @Valid, incluindo a
     * anotação customizada @PeriodoValido; se for inválido, as mensagens são
     * exibidas no formulário de busca e a lista completa é mostrada.
     *
     * @param filtro objeto com os parâmetros de busca (query string)
     * @param result resultado da validação do filtro
     * @param model  objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de consultas
     */
    @GetMapping
    public String listar(@Valid @ModelAttribute("filtro") FiltroConsulta filtro, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("consultas", consultaRepository.findAll());
        } else {
            // dataFim é inclusiva: busca até o início do dia seguinte
            LocalDateTime inicio = filtro.getDataInicio() != null ? filtro.getDataInicio().atStartOfDay() : null;
            LocalDateTime fim = filtro.getDataFim() != null ? filtro.getDataFim().plusDays(1).atStartOfDay() : null;
            model.addAttribute("consultas", consultaRepository.filtrar(inicio, fim,
                    filtro.getPacienteId(), filtro.getMedicoId(), filtro.getValorMinimo()));
        }
        carregarListas(model);
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
        carregarListas(model);
        return "consulta/form";
    }

    /**
     * Exibe o formulário preenchido para editar uma consulta existente.
     * Se o ID não existir, volta para a listagem com mensagem de erro.
     *
     * @param id                 identificador da consulta a ser editada
     * @param model              objeto do Spring MVC para enviar dados à view
     * @param redirectAttributes atributos "flash" que sobrevivem ao redirect
     * @return nome da view do formulário ou redirecionamento para a listagem
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaRepository.findById(id).orElse(null);
        if (consulta == null) {
            redirectAttributes.addFlashAttribute("erro", "Consulta não encontrada: " + id);
            return "redirect:/consultas";
        }
        model.addAttribute("consulta", consulta);
        carregarListas(model);
        return "consulta/form";
    }

    /**
     * Valida e salva uma consulta nova ou atualiza uma existente.
     *
     * Além das anotações da entidade (@NotNull, @DecimalMin, @Size...), aplica
     * regras de negócio que dependem do banco de dados:
     * - paciente e médico selecionados precisam existir;
     * - uma nova consulta não pode ser marcada em data/hora passada;
     * - o médico e o paciente não podem ter outra consulta no mesmo horário.
     *
     * @param consulta           objeto consulta recebido do formulário
     * @param result             resultado da validação
     * @param model              objeto do Spring MVC (recarrega os selects em caso de erro)
     * @param redirectAttributes atributos "flash" para a mensagem de sucesso
     * @return formulário (se houver erros) ou redirecionamento para a listagem
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("consulta") Consulta consulta, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        validarPacienteEMedico(consulta, result);
        validarDataEHorario(consulta, result);

        if (result.hasErrors()) {
            carregarListas(model);
            return "consulta/form";
        }

        consultaRepository.save(consulta);
        redirectAttributes.addFlashAttribute("sucesso", "Consulta salva com sucesso!");
        return "redirect:/consultas";
    }

    /**
     * Exclui uma consulta pelo seu identificador.
     *
     * @param id                 identificador da consulta a ser excluída
     * @param redirectAttributes atributos "flash" para mensagens de sucesso/erro
     * @return redirecionamento para a listagem de consultas
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!consultaRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("erro", "Consulta não encontrada: " + id);
        } else {
            consultaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Consulta excluída com sucesso!");
        }
        return "redirect:/consultas";
    }

    /**
     * Adiciona ao model as listas de pacientes e médicos usadas nos selects.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     */
    private void carregarListas(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
    }

    /**
     * Verifica se o paciente e o médico foram selecionados e existem no banco.
     * Quando existem, substitui o objeto vindo do formulário (que só tem o id)
     * pela entidade carregada do banco.
     *
     * Obs.: como o select usa th:field="*{paciente.id}", o Spring sempre cria
     * um objeto Paciente, mesmo sem seleção; por isso o @NotNull da entidade
     * é complementado aqui verificando se o id veio preenchido.
     *
     * @param consulta consulta recebida do formulário
     * @param result   resultado da validação, onde os erros são registrados
     */
    private void validarPacienteEMedico(Consulta consulta, BindingResult result) {
        Long pacienteId = consulta.getPaciente() != null ? consulta.getPaciente().getId() : null;
        Paciente paciente = pacienteId != null ? pacienteRepository.findById(pacienteId).orElse(null) : null;
        if (paciente == null && !result.hasFieldErrors("paciente")) {
            result.rejectValue("paciente", "paciente.obrigatorio",
                    pacienteId == null ? "Selecione um paciente." : "Paciente não encontrado.");
        }
        consulta.setPaciente(paciente);

        Long medicoId = consulta.getMedico() != null ? consulta.getMedico().getId() : null;
        Medico medico = medicoId != null ? medicoRepository.findById(medicoId).orElse(null) : null;
        if (medico == null && !result.hasFieldErrors("medico")) {
            result.rejectValue("medico", "medico.obrigatorio",
                    medicoId == null ? "Selecione um médico." : "Médico não encontrado.");
        }
        consulta.setMedico(medico);
    }

    /**
     * Regras de data/horário:
     * - consulta NOVA não pode ser marcada no passado (consultas antigas
     *   continuam podendo ser editadas, por isso a regra não fica na entidade);
     * - médico e paciente não podem ter duas consultas no mesmo horário.
     *
     * @param consulta consulta recebida do formulário
     * @param result   resultado da validação, onde os erros são registrados
     */
    private void validarDataEHorario(Consulta consulta, BindingResult result) {
        if (result.hasFieldErrors("data")) {
            return;
        }
        boolean nova = consulta.getId() == null;

        if (nova && consulta.getData().isBefore(LocalDateTime.now())) {
            result.rejectValue("data", "data.passada", "Uma nova consulta não pode ser marcada em data/hora passada.");
            return;
        }

        if (consulta.getMedico() != null) {
            boolean ocupado = nova
                    ? consultaRepository.existsByMedicoIdAndData(consulta.getMedico().getId(), consulta.getData())
                    : consultaRepository.existsByMedicoIdAndDataAndIdNot(
                            consulta.getMedico().getId(), consulta.getData(), consulta.getId());
            if (ocupado) {
                result.rejectValue("data", "data.medicoOcupado", "O médico já possui uma consulta neste horário.");
            }
        }

        if (consulta.getPaciente() != null) {
            boolean ocupado = nova
                    ? consultaRepository.existsByPacienteIdAndData(consulta.getPaciente().getId(), consulta.getData())
                    : consultaRepository.existsByPacienteIdAndDataAndIdNot(
                            consulta.getPaciente().getId(), consulta.getData(), consulta.getId());
            if (ocupado) {
                result.rejectValue("data", "data.pacienteOcupado", "O paciente já possui uma consulta neste horário.");
            }
        }
    }
}
