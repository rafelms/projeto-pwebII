package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuario.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Busca o usuário pelo login (usado na autenticação e na verificação de login duplicado). */
    Optional<Usuario> findByUsuario(String usuario);
}
