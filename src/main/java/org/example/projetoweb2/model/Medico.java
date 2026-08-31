package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String crm;

    /** Lista de consultas vinculadas a este médico. */
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultaList = new ArrayList<>();

    public Medico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCrm() {
        return crm;
    }

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

    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }


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
