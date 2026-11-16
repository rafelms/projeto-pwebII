package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Papel;
import org.example.projetoweb2.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Perfil.
 */
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    /** Busca o perfil pelo papel (ex.: Papel.PACIENTE), usado para atribuir o papel inicial. */
    Optional<Perfil> findByNome(Papel nome);
}
