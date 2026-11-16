package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Medico.
 * Fornece operações CRUD prontas herdadas do JpaRepository.
 * A verificação de CPF duplicado fica no PessoaFisicaRepository.
 */
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    /** Busca médicos cujo nome contém o trecho informado (filtro da listagem). */
    List<Medico> findByNomeContainingIgnoreCase(String nome);

    /**
     * Verifica se OUTRO médico já usa o CRM.
     * Na edição, o id do próprio médico é ignorado; no cadastro, id é null.
     */
    @Query("SELECT COUNT(m) > 0 FROM Medico m "
            + "WHERE m.crm = :crm AND (:id IS NULL OR m.id <> :id)")
    boolean crmEmUso(@Param("crm") String crm, @Param("id") Long id);

    /** Busca o médico pelo login da sua conta (medico.usuario.usuario), usado em "Meus dados". */
    Optional<Medico> findByUsuarioUsuario(String login);
}
