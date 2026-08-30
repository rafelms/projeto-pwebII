package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA para a entidade Consulta.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}
