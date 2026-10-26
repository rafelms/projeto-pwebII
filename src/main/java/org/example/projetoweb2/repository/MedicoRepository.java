package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório JPA para a entidade Medico.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    /** Busca médicos cujo nome contém o trecho informado (filtro da listagem). */
    List<Medico> findByNomeContainingIgnoreCase(String nome);

    /** Verifica se já existe médico com o CPF (usado no cadastro). */
    boolean existsByCpf(String cpf);

    /** Verifica se outro médico (id diferente) já usa o CPF (usado na edição). */
    boolean existsByCpfAndIdNot(String cpf, Long id);

    /** Verifica se já existe médico com o CRM (usado no cadastro). */
    boolean existsByCrm(String crm);

    /** Verifica se outro médico (id diferente) já usa o CRM (usado na edição). */
    boolean existsByCrmAndIdNot(String crm, Long id);
}
