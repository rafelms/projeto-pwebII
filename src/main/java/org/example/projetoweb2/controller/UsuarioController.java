package org.example.projetoweb2.controller;

import jakarta.validation.Valid;
import org.example.projetoweb2.dto.AlterarSenha;
import org.example.projetoweb2.repository.MedicoRepository;
import org.example.projetoweb2.repository.PacienteRepository;
import org.example.projetoweb2.repository.UsuarioRepository;
import org.example.projetoweb2.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Páginas ligadas às contas de usuário.
 *
 * - GET /usuarios:            lista todas as contas e seus perfis (somente ADMIN);
 * - GET /usuarios/meus-dados: dados do usuário logado (qualquer perfil);
 * - PUT /usuarios/senha:      o usuário logado altera a própria senha.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    private final PacienteRepository pacienteRepository;

    private final MedicoRepository medicoRepository;

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository,
                             MedicoRepository medicoRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista as contas cadastradas no banco com os seus perfis.
     *
     * @param model objeto do Spring MVC para enviar dados à view
     * @return nome da view de listagem de usuários
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuario/list";
    }

    /**
     * Exibe os dados do usuário logado.
     *
     * @AuthenticationPrincipal injeta o usuário autenticado (o UserDetails criado
     * pelo UsuarioDetailsService no login). Com o login dele, buscamos a conta
     * no banco e, se existir, o paciente ou médico vinculado a ela.
     *
     * @param usuarioLogado usuário autenticado na sessão
     * @param model         objeto do Spring MVC para enviar dados à view
     * @return nome da view "meus dados"
     */
    @GetMapping("/meus-dados")
    public String meusDados(@AuthenticationPrincipal UserDetails usuarioLogado, Model model) {
        model.addAttribute("alterarSenha", new AlterarSenha());
        return carregarMeusDados(usuarioLogado.getUsername(), model);
    }

    /**
     * Altera a senha do usuário logado (PUT, pois altera um dado já existente).
     *
     * Só altera a conta de quem está logado: o login vem do @AuthenticationPrincipal,
     * nunca do formulário. Se houver erro, a página "Meus dados" é exibida novamente
     * com as mensagens; se der certo, volta para ela com a mensagem de sucesso.
     *
     * @param usuarioLogado      usuário autenticado na sessão
     * @param alterarSenha       senhas digitadas no formulário
     * @param result             resultado da validação do formulário
     * @param model              objeto do Spring MVC para enviar dados à view
     * @param redirectAttributes atributos "flash" para a mensagem de sucesso
     * @return página "Meus dados" (com erros) ou redirecionamento para ela
     */
    @PutMapping("/senha")
    public String alterarSenha(@AuthenticationPrincipal UserDetails usuarioLogado,
                               @Valid @ModelAttribute("alterarSenha") AlterarSenha alterarSenha, BindingResult result,
                               Model model, RedirectAttributes redirectAttributes) {
        usuarioService.alterarSenha(usuarioLogado.getUsername(), alterarSenha, result);
        if (result.hasErrors()) {
            return carregarMeusDados(usuarioLogado.getUsername(), model);
        }
        redirectAttributes.addFlashAttribute("sucesso", "Senha alterada com sucesso!");
        return "redirect:/usuarios/meus-dados";
    }

    /**
     * Coloca no model a conta do usuário e, se existir, o paciente ou médico vinculado a ela.
     */
    private String carregarMeusDados(String login, Model model) {
        model.addAttribute("usuario", usuarioRepository.findByUsuario(login).orElseThrow());
        pacienteRepository.findByUsuarioUsuario(login).ifPresent(p -> model.addAttribute("paciente", p));
        medicoRepository.findByUsuarioUsuario(login).ifPresent(m -> model.addAttribute("medico", m));
        return "usuario/meus-dados";
    }
}
