package org.example.projetoweb2.controller;

import org.example.projetoweb2.model.Funcionario;
import org.example.projetoweb2.model.Departamento;
import org.example.projetoweb2.repository.FuncionarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository repository;

    public FuncionarioController(FuncionarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Carrega a página inicial de listagem de funcionários.
     * 
     * @param model Objeto usado para transferir dados do Controller para a View (HTML + Thymeleaf).
     * @return Retorna a string "list", indicando ao Spring para buscar o arquivo list.html
     */
    @GetMapping
    public String listar(Model model) {
        // Envia a lista de funcionários do banco de dados para a página usando nosso repositório customizado
        model.addAttribute("funcionarios", repository.listar());
        return "list";
    }

    /**
     * Carrega a página do formulário para o cadastro de um NOVO funcionário.
     * 
     * @param model Objeto usado para transferir dados para a View.
     * @return Retorna a string "form", indicando ao Spring para buscar o arquivo form.html
     */
    @GetMapping("/novo")
    public String formularioCadastrar(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        // Enviamos também os valores possíveis do Enum Departamento para popular o <select> no HTML
        model.addAttribute("departamentos", Departamento.values());
        return "form";
    }

    /**
     * Método responsável por receber os dados do formulário submetido e salvar no banco de dados.
     * 
     * @param funcionario Objeto recebido da View, preenchido automaticamente pelo Thymeleaf.
     * @return Redireciona a requisição de volta para a rota "/funcionarios"
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Funcionario funcionario) {
        if (funcionario.getId() == null) {
            repository.cadastrar(funcionario);
        } else {
            repository.editar(funcionario);
        }
        return "redirect:/funcionarios";
    }

    /**
     * Carrega a página do formulário com os dados de um funcionário EXISTENTE preenchidos para edição.
     * 
     * @param id O ID do funcionário a ser editado.
     * @param model Objeto usado para transferir dados para a View.
     * @return Retorna o arquivo form.html
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Funcionario funcionario = repository.buscarPorId(id);
        if (funcionario == null) {
            funcionario = new Funcionario(); // fallback caso não encontre
        }
        model.addAttribute("funcionario", funcionario);
        model.addAttribute("departamentos", Departamento.values());
        return "form";
    }

    /**
     * Ação de deletar um funcionário do sistema.
     * 
     * @param id O ID do funcionário a ser excluído.
     * @return Redireciona de volta para a rota principal.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        repository.excluir(id);
        return "redirect:/funcionarios";
    }
}
