package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.FiltroPessoa;
import org.example.projetoweb2.model.Paciente;
import org.example.projetoweb2.model.Usuario;
import org.example.projetoweb2.repository.ConsultaRepository;
import org.example.projetoweb2.repository.PacienteRepository;
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
 * Controller dos pacientes.
 *
 * Rotas (cada operação usa o verbo HTTP correspondente):
 * - GET    /pacientes              lista (com filtro por nome)
 * - GET    /pacientes/novo         formulário de cadastro
 * - POST   /pacientes              cadastra
 * - GET    /pacientes/{id}/editar  formulário de edição
 * - PUT    /pacientes/{id}         altera
 * - DELETE /pacientes/{id}         exclui
 */
@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    private final PessoaFisicaRepository pessoaFisicaRepository;

    private final ConsultaRepository consultaRepository;

    private final UsuarioService usuarioService;

    public PacienteController(PacienteRepository repository, PessoaFisicaRepository pessoaFisicaRepository,
                              ConsultaRepository consultaRepository, UsuarioService usuarioService) {
        this.repository = repository;
        this.pessoaFisicaRepository = pessoaFisicaRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista os pacientes cadastrados, aplicando o filtro por nome se informado.
     * Se o filtro for inválido, a mensagem aparece no formulário de busca e a lista completa é mostrada.
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
        Paciente paciente = new Paciente();
        paciente.setUsuario(new Usuario()); // conta vazia para os campos usuário/senha do formulário
        model.addAttribute("paciente", paciente);
        return "paciente/form";
    }

    /**
     * Exibe o formulário preenchido para editar um paciente existente.
     *
     * @param id                 identificador do paciente
     * @param model              objeto do Spring MVC para enviar dados à view
     * @param redirectAttributes atributos "flash" que sobrevivem ao redirect
     * @return nome da view do formulário ou redirecionamento para a listagem
     */
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("erro", "Paciente não encontrado: " + id);
            return "redirect:/pacientes";
        }
        if (paciente.getUsuario() == null) {
            paciente.setUsuario(new Usuario()); // paciente ainda sem conta: será criada ao salvar
        }
        model.addAttribute("paciente", paciente);
        return "paciente/form";
    }

    /**
     * Cadastra um novo paciente (POST).
     */
    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("paciente") Paciente paciente, BindingResult result,
                            RedirectAttributes redirectAttributes) {
        paciente.setId(null);
        return salvar(paciente, result, redirectAttributes);
    }

    /**
     * Altera um paciente existente (PUT). O id vem da URL, e não do formulário.
     */
    @PutMapping("/{id}")
    public String alterar(@PathVariable Long id, @Valid @ModelAttribute("paciente") Paciente paciente,
                          BindingResult result, RedirectAttributes redirectAttributes) {
        paciente.setId(id);
        return salvar(paciente, result, redirectAttributes);
    }

    /**
     * Exclui um paciente (DELETE). Não permite excluir quem possui consultas.
     *
     * @param id                 identificador do paciente
     * @param redirectAttributes atributos "flash" para mensagens de sucesso/erro
     * @return redirecionamento para a listagem de pacientes
     */
    @DeleteMapping("/{id}")
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

    /**
     * Validações comuns ao cadastro e à alteração; se tudo estiver certo, grava o paciente.
     *
     * - @Valid (nos métodos acima) já aplicou as anotações da entidade e da conta (Usuario);
     * - o CPF não pode pertencer a outra pessoa física (paciente ou médico);
     * - o UsuarioService valida o login/senha e monta a conta: um paciente novo recebe
     *   o perfil inicial PACIENTE e a senha codificada em BCrypt.
     */
    private String salvar(Paciente paciente, BindingResult result, RedirectAttributes redirectAttributes) {
        if (!result.hasFieldErrors("cpf")) {
            boolean cpfDuplicado = paciente.getId() == null
                    ? pessoaFisicaRepository.existsByCpf(paciente.getCpf())
                    : pessoaFisicaRepository.existsByCpfAndIdNot(paciente.getCpf(), paciente.getId());
            if (cpfDuplicado) {
                result.rejectValue("cpf", "cpf.duplicado", "Já existe uma pessoa cadastrada com este CPF.");
            }
        }

        // Conta já gravada no banco (null para paciente novo ou ainda sem conta)
        Usuario contaAtual = paciente.getId() == null ? null
                : repository.findById(paciente.getId()).map(Paciente::getUsuario).orElse(null);
        usuarioService.validar(paciente.getUsuario(), contaAtual, result);

        if (result.hasErrors()) {
            return "paciente/form";
        }

        paciente.setUsuario(usuarioService.prepararConta(paciente.getUsuario(), contaAtual, "PACIENTE"));
        repository.save(paciente);
        redirectAttributes.addFlashAttribute("sucesso", "Paciente salvo com sucesso!");
        return "redirect:/pacientes";
    }
}
