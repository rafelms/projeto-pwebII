package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.PessoaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA para a entidade PessoaJuridica.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long> {
}
