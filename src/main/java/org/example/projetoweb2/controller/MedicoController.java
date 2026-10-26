package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.FiltroPessoa;
import org.example.projetoweb2.model.Medico;
import org.example.projetoweb2.repository.ConsultaRepository;
import org.example.projetoweb2.repository.MedicoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoRepository repository;

    private final ConsultaRepository consultaRepository;

    /**
     * Construtor com injeção de dependência dos repositórios.
     *
     * @param repository         repositório de médicos
     * @param consultaRepository repositório de consultas (verificação antes da exclusão)
     */
    public MedicoController(MedicoRepository repository, ConsultaRepository consultaRepository) {
        this.repository = repository;
        this.consultaRepository = consultaRepository;
    }

    /**
     * Lista os médicos cadastrados, aplicando o filtro por nome se informado.
     * O filtro é validado com @Valid; se for inválido, a mensagem de erro
     * é exibida no formulário de busca e a lista completa é mostrada.
     *
     * @param filtro objeto com os parâmetros de busca (query string)
     * @param result resultado da validação do filtro
     * @param model  objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de médicos
     */
    @GetMapping
    public String listar(@Valid @ModelAttribute("filtro") FiltroPessoa filtro, BindingResult result, Model model) {
        if (result.hasErrors() || filtro.getNome() == null) {
            model.addAttribute("medicos", repository.findAll());
        } else {
            model.addAttribute("medicos", repository.findByNomeContainingIgnoreCase(filtro.getNome()));
        }
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
     * Se o ID não existir, volta para a listagem com mensagem de erro.
     *
     * @param id                 identificador do médico a ser editado
     * @param model              objeto do Spring MVC para enviar dados à view
     * @param redirectAttributes atributos "flash" que sobrevivem ao redirect
     * @return nome da view do formulário ou redirecionamento para a listagem
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Medico medico = repository.findById(id).orElse(null);
        if (medico == null) {
            redirectAttributes.addFlashAttribute("erro", "Médico não encontrado: " + id);
            return "redirect:/medicos";
        }
        model.addAttribute("medico", medico);
        return "medico/form";
    }

    /**
     * Valida e salva um médico novo ou atualiza um existente.
     *
     * @Valid dispara as anotações de Bean Validation da entidade (e das superclasses).
     * Além delas, é verificado se o CPF e o CRM já pertencem a outro médico.
     *
     * @param medico             objeto médico recebido do formulário
     * @param result             resultado da validação
     * @param redirectAttributes atributos "flash" para a mensagem de sucesso
     * @return formulário (se houver erros) ou redirecionamento para a listagem
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("medico") Medico medico, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        boolean novo = medico.getId() == null;

        if (!result.hasFieldErrors("cpf")) {
            boolean cpfDuplicado = novo
                    ? repository.existsByCpf(medico.getCpf())
                    : repository.existsByCpfAndIdNot(medico.getCpf(), medico.getId());
            if (cpfDuplicado) {
                result.rejectValue("cpf", "cpf.duplicado", "Já existe um médico cadastrado com este CPF.");
            }
        }

        if (!result.hasFieldErrors("crm")) {
            boolean crmDuplicado = novo
                    ? repository.existsByCrm(medico.getCrm())
                    : repository.existsByCrmAndIdNot(medico.getCrm(), medico.getId());
            if (crmDuplicado) {
                result.rejectValue("crm", "crm.duplicado", "Já existe um médico cadastrado com este CRM.");
            }
        }

        if (result.hasErrors()) {
            return "medico/form";
        }

        repository.save(medico);
        redirectAttributes.addFlashAttribute("sucesso", "Médico salvo com sucesso!");
        return "redirect:/medicos";
    }

    /**
     * Exclui um médico pelo seu identificador.
     * Para manter a integridade referencial, não permite excluir
     * um médico que possui consultas vinculadas.
     *
     * @param id                 identificador do médico a ser excluído
     * @param redirectAttributes atributos "flash" para mensagens de sucesso/erro
     * @return redirecionamento para a listagem de médicos
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!repository.existsById(id)) {
            redirectAttributes.addFlashAttribute("erro", "Médico não encontrado: " + id);
        } else if (consultaRepository.existsByMedicoId(id)) {
            redirectAttributes.addFlashAttribute("erro",
                    "Não é possível excluir: o médico possui consultas cadastradas.");
        } else {
            repository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Médico excluído com sucesso!");
        }
        return "redirect:/medicos";
    }
}
