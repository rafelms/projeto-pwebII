package org.example.projetoweb2.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.projetoweb2.model.Funcionario;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para a entidade Funcionario utilizando EntityManager nativo do JPA.
 * Ainda não estamos utilizando a interface JpaRepository do Spring Data, 
 * conforme orientado na aula base para aprendizado.
 */
@Repository
public class FuncionarioRepository {

    // O @PersistenceContext injeta automaticamente o EntityManager do JPA
    @PersistenceContext
    private EntityManager em;

    /**
     * Retorna a lista de todos os funcionários.
     * @return Lista de Funcionario
     */
    public List<Funcionario> listar() {
        // Utilizamos JPQL (Java Persistence Query Language) em vez de SQL puro.
        return em.createQuery("from Funcionario", Funcionario.class).getResultList();
    }

    /**
     * Busca um funcionário específico através de seu ID.
     * @param id ID numérico do funcionário.
     * @return Funcionario encontrado ou null.
     */
    public Funcionario buscarPorId(Long id) {
        return em.find(Funcionario.class, id);
    }

    /**
     * Cadastra um novo funcionário no banco de dados.
     * A anotação @Transactional garante que a transação no banco seja aberta e salva (commit) corretamente.
     * @param funcionario Objeto Funcionario com os dados.
     */
    @Transactional
    public void cadastrar(Funcionario funcionario) {
        em.persist(funcionario);
    }

    /**
     * Atualiza os dados de um funcionário que já existe.
     * @param funcionario Objeto Funcionario modificado.
     */
    @Transactional
    public void editar(Funcionario funcionario) {
        em.merge(funcionario);
    }

    /**
     * Remove o funcionário do banco de dados.
     * É preciso primeiro encontrá-lo (find) para depois removê-lo (remove).
     * @param id ID numérico do funcionário a ser apagado.
     */
    @Transactional
    public void excluir(Long id) {
        Funcionario funcionario = buscarPorId(id);
        if (funcionario != null) {
            em.remove(funcionario);
        }
    }
}
