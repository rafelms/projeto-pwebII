package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.PessoaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade PessoaJuridica.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long> {

    /** Primeira pessoa jurídica cadastrada (a clínica exibida no rodapé). */
    Optional<PessoaJuridica> findFirstByOrderByIdAsc();
}
