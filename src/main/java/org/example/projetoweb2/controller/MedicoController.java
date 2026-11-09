package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.FiltroPessoa;
import org.example.projetoweb2.model.Medico;
import org.example.projetoweb2.model.Usuario;
import org.example.projetoweb2.repository.ConsultaRepository;
import org.example.projetoweb2.repository.MedicoRepository;
import org.example.projetoweb2.repository.PessoaFisicaRepository;
import org.example.projetoweb2.service.UsuarioService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller dos médicos.
 *
 * Rotas (cada operação usa o verbo HTTP correspondente):
 * - GET    /medicos              lista (com filtro por nome)
 * - GET    /medicos/novo         formulário de cadastro
 * - POST   /medicos              cadastra
 * - GET    /medicos/{id}/editar  formulário de edição
 * - PUT    /medicos/{id}         altera
 * - DELETE /medicos/{id}         exclui
 */
@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoRepository repository;

    private final PessoaFisicaRepository pessoaFisicaRepository;

    private final ConsultaRepository consultaRepository;

    private final UsuarioService usuarioService;

    public MedicoController(MedicoRepository repository, PessoaFisicaRepository pessoaFisicaRepository,
                            ConsultaRepository consultaRepository, UsuarioService usuarioService) {
        this.repository = repository;
        this.pessoaFisicaRepository = pessoaFisicaRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista os médicos cadastrados, aplicando o filtro por nome se informado.
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
        Medico medico = new Medico();
        medico.setUsuario(new Usuario()); // conta vazia para os campos usuário/senha do formulário
        model.addAttribute("medico", medico);
        return "medico/form";
    }

    /**
     * Exibe o formulário preenchido para editar um médico existente.
     *
     * @param id                 identificador do médico
     * @param model              objeto do Spring MVC para enviar dados à view
     * @param redirectAttributes atributos "flash" que sobrevivem ao redirect
     * @return nome da view do formulário ou redirecionamento para a listagem
     */
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Medico medico = repository.findById(id).orElse(null);
        if (medico == null) {
            redirectAttributes.addFlashAttribute("erro", "Médico não encontrado: " + id);
            return "redirect:/medicos";
        }
        if (medico.getUsuario() == null) {
            medico.setUsuario(new Usuario()); // médico ainda sem conta: será criada ao salvar
        }
        model.addAttribute("medico", medico);
        return "medico/form";
    }

    /**
     * Cadastra um novo médico (POST).
     */
    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("medico") Medico medico, BindingResult result,
                            RedirectAttributes redirectAttributes) {
        medico.setId(null);
        return salvar(medico, result, redirectAttributes);
    }

    /**
     * Altera um médico existente (PUT). O id vem da URL, e não do formulário.
     */
    @PutMapping("/{id}")
    public String alterar(@PathVariable Long id, @Valid @ModelAttribute("medico") Medico medico,
                          BindingResult result, RedirectAttributes redirectAttributes) {
        medico.setId(id);
        return salvar(medico, result, redirectAttributes);
    }

    /**
     * Exclui um médico (DELETE). Não permite excluir quem possui consultas.
     *
     * @param id                 identificador do médico
     * @param redirectAttributes atributos "flash" para mensagens de sucesso/erro
     * @return redirecionamento para a listagem de médicos
     */
    @DeleteMapping("/{id}")
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

    /**
     * Validações comuns ao cadastro e à alteração; se tudo estiver certo, grava o médico.
     *
     * - o CPF não pode pertencer a outra pessoa física (paciente ou médico);
     * - o CRM não pode pertencer a outro médico;
     * - o UsuarioService valida o login/senha e monta a conta: um médico novo recebe
     *   o perfil inicial MEDICO e a senha codificada em BCrypt.
     */
    private String salvar(Medico medico, BindingResult result, RedirectAttributes redirectAttributes) {
        boolean novo = medico.getId() == null;

        if (!result.hasFieldErrors("cpf")) {
            boolean cpfDuplicado = novo
                    ? pessoaFisicaRepository.existsByCpf(medico.getCpf())
                    : pessoaFisicaRepository.existsByCpfAndIdNot(medico.getCpf(), medico.getId());
            if (cpfDuplicado) {
                result.rejectValue("cpf", "cpf.duplicado", "Já existe uma pessoa cadastrada com este CPF.");
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

        // Conta já gravada no banco (null para médico novo ou ainda sem conta)
        Usuario contaAtual = novo ? null
                : repository.findById(medico.getId()).map(Medico::getUsuario).orElse(null);
        usuarioService.validar(medico.getUsuario(), contaAtual, result);

        if (result.hasErrors()) {
            return "medico/form";
        }

        medico.setUsuario(usuarioService.prepararConta(medico.getUsuario(), contaAtual, "MEDICO"));
        repository.save(medico);
        redirectAttributes.addFlashAttribute("sucesso", "Médico salvo com sucesso!");
        return "redirect:/medicos";
    }
}
