package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.FiltroPessoa;
import org.example.projetoweb2.dto.PessoaFisicaForm;
import org.example.projetoweb2.model.Paciente;
import org.example.projetoweb2.model.Papel;
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
 *
 * O formulário é ligado a um DTO (PessoaFisicaForm), e não à entidade Paciente:
 * o controller valida o DTO e só depois copia os dados para a entidade.
 * No model o DTO continua com o nome "paciente", usado pelo template.
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
        model.addAttribute("paciente", new PessoaFisicaForm());
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
        PessoaFisicaForm form = new PessoaFisicaForm();
        form.carregar(paciente);
        model.addAttribute("paciente", form);
        model.addAttribute("temConta", paciente.getUsuario() != null);
        return "paciente/form";
    }

    /**
     * Cadastra um novo paciente (POST).
     */
    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("paciente") PessoaFisicaForm form, BindingResult result,
                            Model model, RedirectAttributes redirectAttributes) {
        form.setId(null);
        return salvar(new Paciente(), form, result, model, redirectAttributes);
    }

    /**
     * Altera um paciente existente (PUT). O id vem da URL, e não do formulário,
     * e o paciente é sempre buscado no banco: se não existir, volta à listagem com a mensagem de erro.
     */
    @PutMapping("/{id}")
    public String alterar(@PathVariable Long id, @Valid @ModelAttribute("paciente") PessoaFisicaForm form,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("erro", "Paciente não encontrado: " + id);
            return "redirect:/pacientes";
        }
        form.setId(id);
        return salvar(paciente, form, result, model, redirectAttributes);
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
     * - @Valid (nos métodos acima) já aplicou as anotações do formulário;
     * - o CPF não pode pertencer a outra pessoa física (paciente ou médico);
     * - o UsuarioService valida o login/senha e monta a conta: um paciente novo recebe
     *   o perfil inicial PACIENTE e a senha codificada em BCrypt.
     *
     * A entidade só é alterada depois que todas as validações passam.
     *
     * @param paciente entidade nova (cadastro) ou buscada no banco (alteração)
     * @param form     dados digitados
     */
    private String salvar(Paciente paciente, PessoaFisicaForm form, BindingResult result,
                          Model model, RedirectAttributes redirectAttributes) {
        if (!result.hasFieldErrors("cpf") && pessoaFisicaRepository.cpfEmUso(form.getCpf(), paciente.getId())) {
            result.rejectValue("cpf", "cpf.duplicado", "Já existe uma pessoa cadastrada com este CPF.");
        }

        // Conta já gravada no banco (null para paciente novo ou ainda sem conta)
        Usuario contaAtual = paciente.getUsuario();
        usuarioService.validar(form.getUsuario(), form.getSenha(), contaAtual, result);

        if (result.hasErrors()) {
            model.addAttribute("temConta", contaAtual != null);
            return "paciente/form";
        }

        form.aplicar(paciente);
        paciente.setUsuario(usuarioService.prepararConta(form.getUsuario(), form.getSenha(), contaAtual, Papel.PACIENTE));
        repository.save(paciente);
        redirectAttributes.addFlashAttribute("sucesso", "Paciente salvo com sucesso!");
        return "redirect:/pacientes";
    }
}
