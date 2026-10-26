package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.FiltroPessoa;
import org.example.projetoweb2.model.Paciente;
import org.example.projetoweb2.repository.ConsultaRepository;
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


@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    private final ConsultaRepository consultaRepository;

    /**
     * Construtor com injeção de dependência dos repositórios.
     *
     * @param repository         repositório de pacientes
     * @param consultaRepository repositório de consultas (verificação antes da exclusão)
     */
    public PacienteController(PacienteRepository repository, ConsultaRepository consultaRepository) {
        this.repository = repository;
        this.consultaRepository = consultaRepository;
    }

    /**
     * Lista os pacientes cadastrados, aplicando o filtro por nome se informado.
     * O filtro é validado com @Valid; se for inválido, a mensagem de erro
     * é exibida no formulário de busca e a lista completa é mostrada.
     *
     * @param filtro objeto com os parâmetros de busca (query string)
     * @param result resultado da validação do filtro
     * @param model  objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de pacientes
     */
    @GetMapping
    public String listar(@Valid @ModelAttribute("filtro") FiltroPessoa filtro, BindingResult result, Model model) {
        if (result.hasErrors() || filtro.getNome() == null) {
            model.addAttribute("pacientes", repository.findAll());
        } else {
            model.addAttribute("pacientes", repository.findByNomeContainingIgnoreCase(filtro.getNome()));
        }
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
     * Se o ID não existir, volta para a listagem com mensagem de erro.
     *
     * @param id                 identificador do paciente a ser editado
     * @param model              objeto do Spring MVC para enviar dados à view
     * @param redirectAttributes atributos "flash" que sobrevivem ao redirect
     * @return nome da view do formulário ou redirecionamento para a listagem
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("erro", "Paciente não encontrado: " + id);
            return "redirect:/pacientes";
        }
        model.addAttribute("paciente", paciente);
        return "paciente/form";
    }

    /**
     * Valida e salva um paciente novo ou atualiza um existente.
     *
     * @Valid dispara as anotações de Bean Validation da entidade (e das superclasses).
     * O BindingResult, que DEVE vir logo após o objeto validado, guarda os erros;
     * se houver algum, o formulário é exibido novamente com as mensagens.
     * Também é verificado se o CPF já pertence a outro paciente.
     *
     * @param paciente           objeto paciente recebido do formulário
     * @param result             resultado da validação
     * @param redirectAttributes atributos "flash" para a mensagem de sucesso
     * @return formulário (se houver erros) ou redirecionamento para a listagem
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("paciente") Paciente paciente, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (!result.hasFieldErrors("cpf")) {
            boolean cpfDuplicado = paciente.getId() == null
                    ? repository.existsByCpf(paciente.getCpf())
                    : repository.existsByCpfAndIdNot(paciente.getCpf(), paciente.getId());
            if (cpfDuplicado) {
                result.rejectValue("cpf", "cpf.duplicado", "Já existe um paciente cadastrado com este CPF.");
            }
        }

        if (result.hasErrors()) {
            return "paciente/form";
        }

        repository.save(paciente);
        redirectAttributes.addFlashAttribute("sucesso", "Paciente salvo com sucesso!");
        return "redirect:/pacientes";
    }

    /**
     * Exclui um paciente pelo seu identificador.
     * Para manter a integridade referencial, não permite excluir
     * um paciente que possui consultas vinculadas.
     *
     * @param id                 identificador do paciente a ser excluído
     * @param redirectAttributes atributos "flash" para mensagens de sucesso/erro
     * @return redirecionamento para a listagem de pacientes
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!repository.existsById(id)) {
            redirectAttributes.addFlashAttribute("erro", "Paciente não encontrado: " + id);
        } else if (consultaRepository.existsByPacienteId(id)) {
            redirectAttributes.addFlashAttribute("erro",
                    "Não é possível excluir: o paciente possui consultas cadastradas.");
        } else {
            repository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Paciente excluído com sucesso!");
        }
        return "redirect:/pacientes";
    }
}
