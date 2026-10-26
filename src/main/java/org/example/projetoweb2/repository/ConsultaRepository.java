package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para a entidade Consulta.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    /**
     * Filtro da listagem de consultas. Cada critério só é aplicado
     * quando o parâmetro correspondente não é null.
     *
     * @param inicio     data/hora mínima (inclusiva)
     * @param fim        data/hora máxima (exclusiva)
     * @param pacienteId id do paciente
     * @param medicoId   id do médico
     * @param valorMinimo valor mínimo da consulta
     * @return consultas que atendem aos critérios, ordenadas por data
     */
    @Query("SELECT c FROM Consulta c "
            + "WHERE (:inicio IS NULL OR c.data >= :inicio) "
            + "AND (:fim IS NULL OR c.data < :fim) "
            + "AND (:pacienteId IS NULL OR c.paciente.id = :pacienteId) "
            + "AND (:medicoId IS NULL OR c.medico.id = :medicoId) "
            + "AND (:valorMinimo IS NULL OR c.valor >= :valorMinimo) "
            + "ORDER BY c.data")
    List<Consulta> filtrar(@Param("inicio") LocalDateTime inicio,
                           @Param("fim") LocalDateTime fim,
                           @Param("pacienteId") Long pacienteId,
                           @Param("medicoId") Long medicoId,
                           @Param("valorMinimo") Double valorMinimo);

    /** Verifica se o paciente possui consultas (impede exclusão). */
    boolean existsByPacienteId(Long pacienteId);

    /** Verifica se o médico possui consultas (impede exclusão). */
    boolean existsByMedicoId(Long medicoId);

    /** Verifica se o médico já tem consulta nesse horário (usado no cadastro). */
    boolean existsByMedicoIdAndData(Long medicoId, LocalDateTime data);

    /** Verifica se o médico já tem OUTRA consulta nesse horário (usado na edição). */
    boolean existsByMedicoIdAndDataAndIdNot(Long medicoId, LocalDateTime data, Long id);

    /** Verifica se o paciente já tem consulta nesse horário (usado no cadastro). */
    boolean existsByPacienteIdAndData(Long pacienteId, LocalDateTime data);

    /** Verifica se o paciente já tem OUTRA consulta nesse horário (usado na edição). */
    boolean existsByPacienteIdAndDataAndIdNot(Long pacienteId, LocalDateTime data, Long id);
}
