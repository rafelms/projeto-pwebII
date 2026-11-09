package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repositório JPA para a entidade Consulta.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    /**
     * Filtro da listagem. Cada condição só é aplicada se o parâmetro foi informado
     * (":param IS NULL OR ..."), então todos os campos do filtro são opcionais.
     *
     * @param inicio      data inicial (inclusiva)
     * @param fim         data final (inclusiva)
     * @param pacienteId  id do paciente
     * @param medicoId    id do médico
     * @param valorMinimo valor mínimo da consulta
     * @return consultas encontradas, ordenadas por data e horário
     */
    @Query("SELECT c FROM Consulta c "
            + "WHERE (:inicio IS NULL OR c.data >= :inicio) "
            + "AND (:fim IS NULL OR c.data <= :fim) "
            + "AND (:pacienteId IS NULL OR c.paciente.id = :pacienteId) "
            + "AND (:medicoId IS NULL OR c.medico.id = :medicoId) "
            + "AND (:valorMinimo IS NULL OR c.valor >= :valorMinimo) "
            + "ORDER BY c.data, c.horario")
    List<Consulta> filtrar(@Param("inicio") LocalDate inicio,
                           @Param("fim") LocalDate fim,
                           @Param("pacienteId") Long pacienteId,
                           @Param("medicoId") Long medicoId,
                           @Param("valorMinimo") BigDecimal valorMinimo);

    /** Verifica se o paciente possui consultas (impede a exclusão do paciente). */
    boolean existsByPacienteId(Long pacienteId);

    /** Verifica se o médico possui consultas (impede a exclusão do médico). */
    boolean existsByMedicoId(Long medicoId);

    /**
     * Verifica se o médico já tem outra consulta (não cancelada) no mesmo dia e horário.
     * Na edição, o id da própria consulta é ignorado; em uma consulta nova, id é null.
     */
    @Query("SELECT COUNT(c) > 0 FROM Consulta c "
            + "WHERE c.medico.id = :medicoId AND c.data = :data AND c.horario = :horario "
            + "AND c.status <> org.example.projetoweb2.model.StatusConsulta.CANCELADA "
            + "AND (:id IS NULL OR c.id <> :id)")
    boolean medicoOcupado(@Param("medicoId") Long medicoId, @Param("data") LocalDate data,
                          @Param("horario") LocalTime horario, @Param("id") Long id);

    /** Mesma verificação do método acima, para o paciente. */
    @Query("SELECT COUNT(c) > 0 FROM Consulta c "
            + "WHERE c.paciente.id = :pacienteId AND c.data = :data AND c.horario = :horario "
            + "AND c.status <> org.example.projetoweb2.model.StatusConsulta.CANCELADA "
            + "AND (:id IS NULL OR c.id <> :id)")
    boolean pacienteOcupado(@Param("pacienteId") Long pacienteId, @Param("data") LocalDate data,
                            @Param("horario") LocalTime horario, @Param("id") Long id);
}
