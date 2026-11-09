package org.example.projetoweb2.service;

import org.example.projetoweb2.model.Perfil;
import org.example.projetoweb2.model.Usuario;
import org.example.projetoweb2.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Ponte entre o banco de dados e o Spring Security.
 *
 * No login, o Spring Security chama loadUserByUsername() com o usuário digitado.
 * Este serviço busca o Usuario no banco e o converte em um UserDetails
 * (o formato que o Spring Security entende), contendo login, hash da senha e perfis.
 *
 * Quem compara a senha digitada com o hash é o PasswordEncoder (BCrypt),
 * e não este serviço.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Carrega o usuário pelo login.
     *
     * @Transactional(readOnly = true) mantém a conexão aberta enquanto os perfis
     * (carregados sob demanda, LAZY) são lidos.
     *
     * @param login nome de usuário digitado na tela de login
     * @return dados do usuário no formato do Spring Security
     * @throws UsernameNotFoundException se o login não existir
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = repository.findByUsuario(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        // Cada Perfil vira uma autoridade com o prefixo ROLE_ (ex.: ADMIN -> ROLE_ADMIN)
        List<SimpleGrantedAuthority> autoridades = usuario.getPerfis().stream()
                .map(Perfil::getNome)
                .map(nome -> new SimpleGrantedAuthority("ROLE_" + nome))
                .toList();

        return User.withUsername(usuario.getUsuario())
                .password(usuario.getSenha())
                .authorities(autoridades)
                .build();
    }
}
