package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuario.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Busca o usuário pelo login (usado na autenticação e em "Meus dados"). */
    Optional<Usuario> findByUsuario(String usuario);

    /**
     * Verifica se OUTRA conta já usa o login.
     * Na edição, o id da própria conta é ignorado; em uma conta nova, id é null.
     */
    @Query("SELECT COUNT(u) > 0 FROM Usuario u "
            + "WHERE u.usuario = :login AND (:id IS NULL OR u.id <> :id)")
    boolean loginEmUso(@Param("login") String login, @Param("id") Long id);
}
