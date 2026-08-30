package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA para a entidade Medico.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
