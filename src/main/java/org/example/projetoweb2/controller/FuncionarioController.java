package org.example.projetoweb2.controller;

import org.example.projetoweb2.model.Funcionario;
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
        // Envia a lista de funcionários do banco de dados para a página.
        // No list.html, o Thymeleaf acessará essa lista através da variável ${funcionarios}
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
        // Cria um objeto Funcionario VAZIO e envia para a página form.html.
        // O Thymeleaf usará esse objeto (th:object) para preencher o formulário vazio.
        model.addAttribute("funcionario", new Funcionario());
        return "form";
    }

    /**
     * Método responsável por receber os dados do formulário submetido e salvar no banco de dados.
     * 
     * @param funcionario Objeto recebido da View. O Thymeleaf (através dos atributos th:field) 
     *                    preencheu automaticamente os dados digitados pelo usuário neste objeto.
     * @return Redireciona a requisição de volta para a rota "/funcionarios" (que carrega a lista atualizada)
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Funcionario funcionario) {
        if (funcionario.getId() == null) {
            repository.cadastrar(funcionario);
        } else {
            repository.editar(funcionario);
        }
        // O redirecionamento (redirect:) faz com que não tente abrir uma tela chamada 'funcionarios',
        // mas sim que realize uma nova chamada para a rota URL /funcionarios (chamando o método listar).
        return "redirect:/funcionarios";
    }

    /**
     * Carrega a página do formulário com os dados de um funcionário EXISTENTE preenchidos para edição.
     * 
     * @param id O ID do funcionário a ser editado, passado diretamente pela URL (ex: /funcionarios/editar/5)
     * @param model Objeto usado para transferir dados para a View.
     * @return Retorna o arquivo form.html
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        // Busca o funcionário correspondente ao ID no banco e o envia para a página.
        // O Thymeleaf pegará esses dados e preencherá os inputs (campos) no HTML.
        model.addAttribute("funcionario", repository.buscarPorId(id));
        return "form";
    }

    /**
     * Ação de deletar um funcionário do sistema.
     * Não há uma tela específica para exclusão, ela acontece por "baixo dos panos".
     * 
     * @param id O ID do funcionário a ser excluído, pego da URL que foi gerada pelo botão no Thymeleaf.
     * @return Redireciona de volta para a rota principal carregando a listagem atualizada.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        repository.excluir(id);
        return "redirect:/funcionarios";
    }
}
