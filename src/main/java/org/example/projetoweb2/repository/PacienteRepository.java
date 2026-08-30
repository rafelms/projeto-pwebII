package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA para a entidade Paciente.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
