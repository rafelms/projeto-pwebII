package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.ConsultaForm;
import org.example.projetoweb2.dto.FiltroConsulta;
import org.example.projetoweb2.model.Consulta;
import org.example.projetoweb2.model.Medico;
import org.example.projetoweb2.model.Paciente;
import org.example.projetoweb2.model.StatusConsulta;
import org.example.projetoweb2.model.TipoConsulta;
import org.example.projetoweb2.repository.ConsultaRepository;
import org.example.projetoweb2.repository.MedicoRepository;
import org.example.projetoweb2.repository.PacienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller das consultas.
 *
 * Rotas (cada operação usa o verbo HTTP correspondente):
 * - GET    /consultas              lista (com filtro)
 * - GET    /consultas/nova         formulário de cadastro
 * - POST   /consultas              cadastra
 * - GET    /consultas/{id}/editar  formulário de edição
 * - PUT    /consultas/{id}         altera
 * - DELETE /consultas/{id}         exclui
 *
 * O HTML só envia GET e POST; para PUT e DELETE o Thymeleaf (th:method) adiciona
 * um campo oculto "_method", que o HiddenHttpMethodFilter do Spring converte no verbo certo.
 *
 * O formulário é ligado a um DTO (ConsultaForm), e não à entidade Consulta:
 * o controller valida o DTO e só depois copia os dados para a entidade.
 * No model o DTO continua com o nome "consulta", usado pelo template.
 */
@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    /** Horários de atendimento da clínica: das 08:00 às 17:30, de 30 em 30 minutos. */
    private static final List<LocalTime> HORARIOS = gerarHorarios();

    private final ConsultaRepository consultaRepository;

    private final PacienteRepository pacienteRepository;

    private final MedicoRepository medicoRepository;

    public ConsultaController(ConsultaRepository consultaRepository,
                              PacienteRepository pacienteRepository,
                              MedicoRepository medicoRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    /**
     * Lista as consultas, aplicando o filtro se for válido.
     * Com filtro inválido, as mensagens aparecem no formulário de busca e a lista completa
     * é mostrada — pela mesma consulta do filtro (sem parâmetros), para manter a ordem por data e horário.
     *
     * @param filtro objeto com os parâmetros de busca (query string)
     * @param result resultado da validação do filtro
     * @param model  objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de consultas
     */
    @GetMapping
    public String listar(@Valid @ModelAttribute("filtro") FiltroConsulta filtro, BindingResult result, Model model) {
        FiltroConsulta aplicado = result.hasErrors() ? new FiltroConsulta() : filtro;
        model.addAttribute("consultas", consultaRepository.filtrar(aplicado.getDataInicio(), aplicado.getDataFim(),
                aplicado.getPacienteId(), aplicado.getMedicoId(), aplicado.getValorMinimo()));
        carregarListas(model);
        return "consulta/list";
    }

    /**
     * Exibe o formulário de nova consulta.
     * O parâmetro pacienteId (opcional) vem do botão "Agendar" da lista de pacientes
     * e deixa o paciente já selecionado.
     *
     * @param pacienteId paciente pré-selecionado (opcional)
     * @param model      objeto do Spring MVC para enviar dados à view
     * @return nome da view do formulário de consulta
     */
    @GetMapping("/nova")
    public String nova(@RequestParam(required = false) Long pacienteId, Model model) {
        ConsultaForm form = new ConsultaForm();
        form.setPacienteId(pacienteId);
        model.addAttribute("consulta", form);
        carregarListas(model);
        return "consulta/form";
    }

    /**
     * Exibe o formulário preenchido para editar uma consulta existente.
     *
     * @param id                 identificador da consulta
     * @param model              objeto do Spring MVC para enviar dados à view
     * @param redirectAttributes atributos "flash" que sobrevivem ao redirect
     * @return nome da view do formulário ou redirecionamento para a listagem
     */
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaRepository.findById(id).orElse(null);
        if (consulta == null) {
            redirectAttributes.addFlashAttribute("erro", "Consulta não encontrada: " + id);
            return "redirect:/consultas";
        }
        ConsultaForm form = new ConsultaForm();
        form.carregar(consulta);
        model.addAttribute("consulta", form);
        carregarListas(model);
        return "consulta/form";
    }

    /**
     * Cadastra uma nova consulta (POST). Toda consulta nova começa como AGENDADA,
     * mesmo que o formulário seja adulterado para enviar outra situação.
     */
    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("consulta") ConsultaForm form, BindingResult result,
                            Model model, RedirectAttributes redirectAttributes) {
        form.setId(null);
        form.setStatus(StatusConsulta.AGENDADA);
        return salvar(new Consulta(), form, result, model, redirectAttributes);
    }

    /**
     * Altera uma consulta existente (PUT). O id vem da URL, e não do formulário,
     * e a consulta é sempre buscada no banco: se não existir, volta à listagem com a mensagem de erro.
     */
    @PutMapping("/{id}")
    public String alterar(@PathVariable Long id, @Valid @ModelAttribute("consulta") ConsultaForm form,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaRepository.findById(id).orElse(null);
        if (consulta == null) {
            redirectAttributes.addFlashAttribute("erro", "Consulta não encontrada: " + id);
            return "redirect:/consultas";
        }
        form.setId(id);
        return salvar(consulta, form, result, model, redirectAttributes);
    }

    /**
     * Exclui uma consulta (DELETE).
     *
     * @param id                 identificador da consulta
     * @param redirectAttributes atributos "flash" para mensagens de sucesso/erro
     * @return redirecionamento para a listagem de consultas
     */
    @DeleteMapping("/{id}")
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
     * Validações comuns ao cadastro e à alteração; se tudo estiver certo, grava a consulta.
     * A entidade só é alterada depois que todas as validações passam.
     *
     * @param consulta entidade nova (cadastro) ou buscada no banco (alteração)
     * @param form     dados digitados
     */
    private String salvar(Consulta consulta, ConsultaForm form, BindingResult result,
                          Model model, RedirectAttributes redirectAttributes) {
        // O formulário envia só o id; se o id não existir no banco, o erro vai para o campo
        Paciente paciente = form.getPacienteId() == null ? null
                : pacienteRepository.findById(form.getPacienteId()).orElse(null);
        if (paciente == null && !result.hasFieldErrors("pacienteId")) {
            result.rejectValue("pacienteId", "paciente.invalido", "Selecione um paciente.");
        }
        Medico medico = form.getMedicoId() == null ? null
                : medicoRepository.findById(form.getMedicoId()).orElse(null);
        if (medico == null && !result.hasFieldErrors("medicoId")) {
            result.rejectValue("medicoId", "medico.invalido", "Selecione um médico.");
        }

        validarAgenda(form, result);

        if (result.hasErrors()) {
            carregarListas(model);
            return "consulta/form";
        }

        form.aplicar(consulta, paciente, medico);
        consultaRepository.save(consulta);
        redirectAttributes.addFlashAttribute("sucesso", "Consulta salva com sucesso!");
        return "redirect:/consultas";
    }

    /**
     * Dados usados pelos selects do formulário e do filtro.
     */
    private void carregarListas(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        model.addAttribute("horarios", HORARIOS);
        model.addAttribute("tipos", TipoConsulta.values());
        model.addAttribute("situacoes", StatusConsulta.values());
    }

    /**
     * Regras de agenda:
     * - a clínica atende de segunda a sexta, nos horários da lista;
     * - consulta agendada/confirmada precisa estar no futuro;
     * - consulta realizada (ou falta) só depois do horário — assim uma consulta
     *   que já passou não volta a ficar "agendada";
     * - o médico e o paciente não podem ter duas consultas no mesmo horário
     *   (consultas canceladas não ocupam o horário).
     */
    private void validarAgenda(ConsultaForm form, BindingResult result) {
        if (result.hasFieldErrors("data") || result.hasFieldErrors("horario") || result.hasFieldErrors("status")) {
            return;
        }

        DayOfWeek dia = form.getData().getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            result.rejectValue("data", "data.fimDeSemana", "A clínica atende de segunda a sexta-feira.");
            return;
        }
        if (!HORARIOS.contains(form.getHorario())) {
            result.rejectValue("horario", "horario.invalido", "Escolha um dos horários da lista.");
            return;
        }

        boolean noFuturo = LocalDateTime.of(form.getData(), form.getHorario()).isAfter(LocalDateTime.now());
        StatusConsulta status = form.getStatus();

        if (status.isEmAberto() && !noFuturo) {
            result.rejectValue("data", "data.passada",
                    "Uma consulta " + status.getDescricao().toLowerCase() + " precisa estar em data e horário futuros.");
            return;
        }
        if (status.isEncerrada() && noFuturo) {
            result.rejectValue("status", "status.futuro",
                    "Só é possível marcar \"" + status.getDescricao() + "\" depois do horário da consulta.");
            return;
        }

        if (status == StatusConsulta.CANCELADA) {
            return;
        }
        if (form.getMedicoId() != null && consultaRepository.medicoOcupado(
                form.getMedicoId(), form.getData(), form.getHorario(), form.getId())) {
            result.rejectValue("horario", "horario.medicoOcupado", "O médico já possui uma consulta neste horário.");
        }
        if (form.getPacienteId() != null && consultaRepository.pacienteOcupado(
                form.getPacienteId(), form.getData(), form.getHorario(), form.getId())) {
            result.rejectValue("horario", "horario.pacienteOcupado", "O paciente já possui uma consulta neste horário.");
        }
    }

    /**
     * Gera a lista de horários: 08:00, 08:30, ..., 17:30.
     */
    private static List<LocalTime> gerarHorarios() {
        List<LocalTime> horarios = new ArrayList<>();
        for (LocalTime h = LocalTime.of(8, 0); h.isBefore(LocalTime.of(18, 0)); h = h.plusMinutes(30)) {
            horarios.add(h);
        }
        return horarios;
    }
}
