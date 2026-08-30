package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um Médico no sistema da clínica.
 * Possui relacionamento de um para muitos com a entidade Consulta.
 */
@Entity
public class Medico {

    /** Identificador único do médico (chave primária). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome completo do médico. */
    private String nome;

    /** Número do CRM (Conselho Regional de Medicina) do médico. */
    private String crm;

    /** Lista de consultas vinculadas a este médico. */
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultaList = new ArrayList<>();

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Medico() {
    }

    /**
     * Retorna o identificador do médico.
     *
     * @return id do médico
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o identificador do médico.
     *
     * @param id identificador a ser definido
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome do médico.
     *
     * @return nome do médico
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do médico.
     *
     * @param nome nome a ser definido
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna o CRM do médico.
     *
     * @return CRM do médico
     */
    public String getCrm() {
        return crm;
    }

    /**
     * Define o CRM do médico.
     *
     * @param crm CRM a ser definido
     */
    public void setCrm(String crm) {
        this.crm = crm;
    }

    /**
     * Retorna a lista de consultas do médico.
     *
     * @return lista de consultas
     */
    public List<Consulta> getConsultaList() {
        return consultaList;
    }

    /**
     * Define a lista de consultas do médico.
     *
     * @param consultaList lista de consultas a ser definida
     */
    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }

    /**
     * Retorna uma string formatada com os dados básicos do médico.
     *
     * @return string com id, nome e CRM do médico
     */
    public String dados() {
        return "Medico [ID=" + id + ", Nome=" + nome + ", CRM=" + crm + "]";
    }

    /**
     * Retorna uma string resumindo as consultas vinculadas a este médico.
     * Caso não possua consultas, informa que não há consultas cadastradas.
     *
     * @return string com o resumo das consultas do médico
     */
    public String consultas() {
        if (consultaList == null || consultaList.isEmpty()) {
            return "Nenhuma consulta cadastrada para Dr(a). " + nome + ".";
        }
        StringBuilder sb = new StringBuilder("Consultas de Dr(a). " + nome + ": ");
        for (Consulta c : consultaList) {
            sb.append(c.dados()).append(" | ");
        }
        return sb.toString();
    }
}
