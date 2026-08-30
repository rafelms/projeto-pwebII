package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um Paciente no sistema da clínica.
 * Possui relacionamento de um para muitos com a entidade Consulta.
 */
@Entity
public class Paciente {

    /** Identificador único do paciente (chave primária). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome completo do paciente. */
    private String nome;

    /** Telefone de contato do paciente. */
    private String telefone;

    /** Lista de consultas vinculadas a este paciente. */
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultaList = new ArrayList<>();

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Paciente() {
    }

    /**
     * Retorna o identificador do paciente.
     *
     * @return id do paciente
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do paciente.
     *
     * @param id identificador a ser definido
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome do paciente.
     *
     * @return nome do paciente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do paciente.
     *
     * @param nome nome a ser definido
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o telefone do paciente.
     *
     * @return telefone do paciente
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o telefone do paciente.
     *
     * @param telefone telefone a ser definido
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Retorna a lista de consultas do paciente.
     *
     * @return lista de consultas
     */
    public List<Consulta> getConsultaList() {
        return consultaList;
    }

    /**
     * Define a lista de consultas do paciente.
     *
     * @param consultaList lista de consultas a ser definida
     */
    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }

    /**
     * Retorna uma string formatada com os dados básicos do paciente.
     *
     * @return string com id, nome e telefone do paciente
     */
    public String dados() {
        return "Paciente [ID=" + id + ", Nome=" + nome + ", Telefone=" + telefone + "]";
    }

    /**
     * Retorna uma string resumindo as consultas vinculadas a este paciente.
     * Caso não possua consultas, informa que não há consultas cadastradas.
     *
     * @return string com o resumo das consultas do paciente
     */
    public String consultas() {
        if (consultaList == null || consultaList.isEmpty()) {
            return "Nenhuma consulta cadastrada para " + nome + ".";
        }
        StringBuilder sb = new StringBuilder("Consultas de " + nome + ": ");
        for (Consulta c : consultaList) {
            sb.append(c.dados()).append(" | ");
        }
        return sb.toString();
    }
}
