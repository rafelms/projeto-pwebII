package org.example.projetoweb2.repository;

import org.example.projetoweb2.model.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA para PessoaFisica (superclasse de Paciente e Medico).
 *
 * Como busca na tabela pessoa_fisica, as consultas abaixo enxergam
 * pacientes E médicos: assim o CPF é único entre todas as pessoas físicas.
 */
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {

    /** Verifica se alguma pessoa física já usa o CPF (usado no cadastro). */
    boolean existsByCpf(String cpf);

    /** Verifica se outra pessoa física (id diferente) já usa o CPF (usado na edição). */
    boolean existsByCpfAndIdNot(String cpf, Long id);
}
