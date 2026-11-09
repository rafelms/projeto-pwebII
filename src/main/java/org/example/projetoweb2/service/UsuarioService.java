package org.example.projetoweb2.service;

import org.example.projetoweb2.dto.AlterarSenha;
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

    /** Tamanho mínimo da senha digitada no formulário. */
    private static final int SENHA_MINIMA = 3;

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
     * Valida o login e a senha digitados no formulário.
     * Os erros são adicionados ao BindingResult e exibidos no próprio formulário.
     *
     * - login: não pode pertencer a outra conta;
     * - senha: obrigatória ao criar a conta; na edição, em branco significa "manter a atual".
     *
     * @param formulario usuário e senha digitados
     * @param contaAtual conta já gravada da pessoa (null se ainda não tiver conta)
     * @param result     resultado da validação do formulário
     */
    public void validar(Usuario formulario, Usuario contaAtual, BindingResult result) {
        if (!result.hasFieldErrors("usuario.usuario")) {
            boolean loginEmUso = usuarioRepository.findByUsuario(formulario.getUsuario())
                    .filter(outro -> contaAtual == null || !outro.getId().equals(contaAtual.getId()))
                    .isPresent();
            if (loginEmUso) {
                result.rejectValue("usuario.usuario", "usuario.duplicado", "Este usuário já está em uso.");
            }
        }

        String senha = formulario.getSenha();
        boolean senhaEmBranco = senha == null || senha.isBlank();

        if (contaAtual == null && senhaEmBranco) {
            result.rejectValue("usuario.senha", "senha.obrigatoria", "A senha é obrigatória.");
        } else if (!senhaEmBranco && senha.length() < SENHA_MINIMA) {
            result.rejectValue("usuario.senha", "senha.curta",
                    "A senha deve ter no mínimo " + SENHA_MINIMA + " caracteres.");
        }
    }

    /**
     * Monta a conta que será gravada junto com a pessoa.
     *
     * - Pessoa SEM conta: cria um Usuario novo, com a senha codificada (BCrypt)
     *   e o perfil inicial (ex.: PACIENTE ou MEDICO).
     * - Pessoa COM conta: atualiza o login e, se informada, a senha; os perfis são mantidos.
     *
     * A conta atual é sempre buscada no banco (e não pelo id vindo do formulário),
     * para que ninguém altere a conta de outra pessoa manipulando o HTML.
     *
     * @param formulario    usuário e senha digitados
     * @param contaAtual    conta já gravada da pessoa (null se ainda não tiver conta)
     * @param perfilInicial nome do perfil atribuído a uma conta nova
     * @return conta pronta para ser gravada
     */
    public Usuario prepararConta(Usuario formulario, Usuario contaAtual, String perfilInicial) {
        if (contaAtual == null) {
            Usuario nova = new Usuario(formulario.getUsuario(), passwordEncoder.encode(formulario.getSenha()));
            Perfil perfil = perfilRepository.findByNome(perfilInicial)
                    .orElseThrow(() -> new IllegalStateException("Perfil não cadastrado: " + perfilInicial));
            nova.adicionarPerfil(perfil);
            return nova;
        }

        contaAtual.setUsuario(formulario.getUsuario());
        if (formulario.getSenha() != null && !formulario.getSenha().isBlank()) {
            contaAtual.setSenha(passwordEncoder.encode(formulario.getSenha()));
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
        usuarioRepository.save(usuario);
    }
}
