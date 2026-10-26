package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório JPA para a entidade Paciente.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /** Busca pacientes cujo nome contém o trecho informado (filtro da listagem). */
    List<Paciente> findByNomeContainingIgnoreCase(String nome);

    /** Verifica se já existe paciente com o CPF (usado no cadastro). */
    boolean existsByCpf(String cpf);

    /** Verifica se outro paciente (id diferente) já usa o CPF (usado na edição). */
    boolean existsByCpfAndIdNot(String cpf, Long id);
}
