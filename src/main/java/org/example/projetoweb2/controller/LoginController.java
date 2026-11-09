package org.example.projetoweb2.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller das páginas ligadas à autenticação.
 *
 * Apenas EXIBE as páginas: o POST /login (verificação de usuário e senha)
 * e o POST /logout são processados automaticamente pelo Spring Security.
 */
@Controller
public class LoginController {

    /**
     * Exibe o formulário de login personalizado.
     *
     * @return nome da view de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Página inicial (para onde o usuário vai após o login).
     * O PACIENTE não acessa as listagens, então vai para "Meus dados";
     * os demais perfis vão para a listagem de consultas.
     *
     * @param request requisição atual, usada para consultar o perfil do usuário logado
     * @return redirecionamento conforme o perfil
     */
    @GetMapping("/")
    public String home(HttpServletRequest request) {
        if (request.isUserInRole("PACIENTE")) {
            return "redirect:/usuarios/meus-dados";
        }
        return "redirect:/consultas";
    }

    /**
     * Exibida quando um usuário autenticado tenta acessar uma URL
     * para a qual seu perfil não tem permissão (HTTP 403).
     *
     * Usa @RequestMapping (todos os métodos HTTP) porque o Spring Security
     * ENCAMINHA (forward) a requisição original para esta URL: se o acesso
     * negado ocorreu em um POST, PUT ou DELETE (ex.: DELETE /medicos/4), o forward mantém o mesmo verbo.
     *
     * @return nome da view de acesso negado
     */
    @RequestMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}
