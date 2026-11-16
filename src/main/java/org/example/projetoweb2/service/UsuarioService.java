package org.example.projetoweb2.service;

import org.example.projetoweb2.dto.AlterarSenha;
import org.example.projetoweb2.model.Papel;
import org.example.projetoweb2.model.Perfil;
import org.example.projetoweb2.model.Usuario;
import org.example.projetoweb2.repository.PerfilRepository;
import org.example.projetoweb2.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

/**
 * Regras de criação e alteração das contas de acesso (Usuario).
 *
 * Usado pelos controllers de Paciente e Médico, pois ambos cadastram
 * a pessoa junto com o seu usuário e senha, e pelo UsuarioController,
 * quando o próprio usuário altera a sua senha.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PerfilRepository perfilRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Regras da conta que dependem do banco (o formato do login e o tamanho da senha
     * já foram validados pelas anotações do formulário).
     * Os erros são adicionados ao BindingResult e exibidos no próprio formulário.
     *
     * - login: não pode pertencer a outra conta;
     * - senha: obrigatória ao criar a conta; na edição, em branco significa "manter a atual".
     *   Senha em branco chega aqui como null (GlobalBindingAdvice).
     *
     * @param login      usuário digitado (campo "usuario" do formulário)
     * @param senha      senha digitada (campo "senha" do formulário)
     * @param contaAtual conta já gravada da pessoa (null se ainda não tiver conta)
     * @param result     resultado da validação do formulário
     */
    public void validar(String login, String senha, Usuario contaAtual, BindingResult result) {
        Long idDaConta = contaAtual != null ? contaAtual.getId() : null;
        if (!result.hasFieldErrors("usuario") && usuarioRepository.loginEmUso(login, idDaConta)) {
            result.rejectValue("usuario", "usuario.duplicado", "Este usuário já está em uso.");
        }
        if (contaAtual == null && senha == null) {
            result.rejectValue("senha", "senha.obrigatoria", "A senha é obrigatória.");
        }
    }

    /**
     * Monta a conta que será gravada junto com a pessoa.
     *
     * - Pessoa SEM conta: cria um Usuario novo, com a senha codificada (BCrypt)
     *   e o perfil inicial (PACIENTE ou MEDICO).
     * - Pessoa COM conta: atualiza o login e, se informada, a senha; os perfis são mantidos.
     *
     * A conta atual vem sempre da pessoa buscada no banco (e nunca de um id enviado
     * pelo formulário), para que ninguém altere a conta de outra pessoa manipulando o HTML.
     *
     * @param login         usuário digitado
     * @param senha         senha digitada (null = manter a atual)
     * @param contaAtual    conta já gravada da pessoa (null se ainda não tiver conta)
     * @param perfilInicial papel atribuído a uma conta nova
     * @return conta pronta para ser gravada
     */
    public Usuario prepararConta(String login, String senha, Usuario contaAtual, Papel perfilInicial) {
        if (contaAtual == null) {
            Usuario nova = new Usuario(login, passwordEncoder.encode(senha));
            Perfil perfil = perfilRepository.findByNome(perfilInicial)
                    .orElseThrow(() -> new IllegalStateException("Perfil não cadastrado: " + perfilInicial));
            nova.adicionarPerfil(perfil);
            return nova;
        }

        contaAtual.setUsuario(login);
        if (senha != null) {
            contaAtual.setSenha(passwordEncoder.encode(senha));
        }
        return contaAtual;
    }

    /**
     * Troca a senha do próprio usuário logado.
     *
     * - passwordEncoder.matches(digitada, hash) confere se a senha atual está certa:
     *   o hash do BCrypt não pode ser "desfeito", só comparado;
     * - a nova senha e a confirmação precisam ser iguais.
     * Se houver erro, ele é registrado no BindingResult e a senha não é alterada.
     *
     * Como o método é @Transactional, o usuário buscado está sendo gerenciado pelo JPA
     * e a nova senha é gravada ao final da transação, sem precisar chamar save().
     *
     * @param login  login do usuário autenticado
     * @param dados  senhas digitadas no formulário
     * @param result resultado da validação do formulário
     */
    @Transactional
    public void alterarSenha(String login, AlterarSenha dados, BindingResult result) {
        Usuario usuario = usuarioRepository.findByUsuario(login).orElseThrow();

        if (!result.hasFieldErrors("senhaAtual")
                && !passwordEncoder.matches(dados.getSenhaAtual(), usuario.getSenha())) {
            result.rejectValue("senhaAtual", "senha.incorreta", "A senha atual está incorreta.");
        }
        if (!result.hasFieldErrors("confirmacao") && !dados.getConfirmacao().equals(dados.getNovaSenha())) {
            result.rejectValue("confirmacao", "senha.diferente", "A confirmação não é igual à nova senha.");
        }
        if (result.hasErrors()) {
            return;
        }

        usuario.setSenha(passwordEncoder.encode(dados.getNovaSenha()));
    }
}
