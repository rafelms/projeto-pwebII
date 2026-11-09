package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Paciente.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 * A verificação de CPF duplicado fica no PessoaFisicaRepository.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /** Busca pacientes cujo nome contém o trecho informado (filtro da listagem). */
    List<Paciente> findByNomeContainingIgnoreCase(String nome);

    /** Busca o paciente pelo login da sua conta (paciente.usuario.usuario), usado em "Meus dados". */
    Optional<Paciente> findByUsuarioUsuario(String login);
}
