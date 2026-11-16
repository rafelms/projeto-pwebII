package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.FiltroPessoa;
import org.example.projetoweb2.dto.MedicoForm;
import org.example.projetoweb2.model.Medico;
import org.example.projetoweb2.model.Papel;
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
 *
 * O formulário é ligado a um DTO (MedicoForm), e não à entidade Medico:
 * o controller valida o DTO e só depois copia os dados para a entidade.
 * No model o DTO continua com o nome "medico", usado pelo template.
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
        model.addAttribute("medico", new MedicoForm());
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
        MedicoForm form = new MedicoForm();
        form.carregar(medico);
        model.addAttribute("medico", form);
        model.addAttribute("temConta", medico.getUsuario() != null);
        return "medico/form";
    }

    /**
     * Cadastra um novo médico (POST).
     */
    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("medico") MedicoForm form, BindingResult result,
                            Model model, RedirectAttributes redirectAttributes) {
        form.setId(null);
        return salvar(new Medico(), form, result, model, redirectAttributes);
    }

    /**
     * Altera um médico existente (PUT). O id vem da URL, e não do formulário,
     * e o médico é sempre buscado no banco: se não existir, volta à listagem com a mensagem de erro.
     */
    @PutMapping("/{id}")
    public String alterar(@PathVariable Long id, @Valid @ModelAttribute("medico") MedicoForm form,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        Medico medico = repository.findById(id).orElse(null);
        if (medico == null) {
            redirectAttributes.addFlashAttribute("erro", "Médico não encontrado: " + id);
            return "redirect:/medicos";
        }
        form.setId(id);
        return salvar(medico, form, result, model, redirectAttributes);
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
     * - @Valid (nos métodos acima) já aplicou as anotações do formulário;
     * - o CPF não pode pertencer a outra pessoa física (paciente ou médico);
     * - o CRM não pode pertencer a outro médico;
     * - o UsuarioService valida o login/senha e monta a conta: um médico novo recebe
     *   o perfil inicial MEDICO e a senha codificada em BCrypt.
     *
     * A entidade só é alterada depois que todas as validações passam.
     *
     * @param medico entidade nova (cadastro) ou buscada no banco (alteração)
     * @param form   dados digitados
     */
    private String salvar(Medico medico, MedicoForm form, BindingResult result,
                          Model model, RedirectAttributes redirectAttributes) {
        if (!result.hasFieldErrors("cpf") && pessoaFisicaRepository.cpfEmUso(form.getCpf(), medico.getId())) {
            result.rejectValue("cpf", "cpf.duplicado", "Já existe uma pessoa cadastrada com este CPF.");
        }
        if (!result.hasFieldErrors("crm") && repository.crmEmUso(form.getCrm(), medico.getId())) {
            result.rejectValue("crm", "crm.duplicado", "Já existe um médico cadastrado com este CRM.");
        }

        // Conta já gravada no banco (null para médico novo ou ainda sem conta)
        Usuario contaAtual = medico.getUsuario();
        usuarioService.validar(form.getUsuario(), form.getSenha(), contaAtual, result);

        if (result.hasErrors()) {
            model.addAttribute("temConta", contaAtual != null);
            return "medico/form";
        }

        form.aplicar(medico);
        medico.setUsuario(usuarioService.prepararConta(form.getUsuario(), form.getSenha(), contaAtual, Papel.MEDICO));
        repository.save(medico);
        redirectAttributes.addFlashAttribute("sucesso", "Médico salvo com sucesso!");
        return "redirect:/medicos";
    }
}
