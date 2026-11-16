package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositório JPA para PessoaFisica (superclasse de Paciente e Medico).
 *
 * Como busca na tabela pessoa_fisica, a consulta abaixo enxerga
 * pacientes E médicos: assim o CPF é único entre todas as pessoas físicas.
 */
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {

    /**
     * Verifica se OUTRA pessoa física já usa o CPF.
     * Na edição, o id da própria pessoa é ignorado; no cadastro, id é null.
     */
    @Query("SELECT COUNT(p) > 0 FROM PessoaFisica p "
            + "WHERE p.cpf = :cpf AND (:id IS NULL OR p.id <> :id)")
    boolean cpfEmUso(@Param("cpf") String cpf, @Param("id") Long id);
}
