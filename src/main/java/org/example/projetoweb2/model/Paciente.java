package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String telefone;

    /** Lista de consultas vinculadas a este paciente. */
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultaList = new ArrayList<>();

    public Paciente() {
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

    public String getTelefone() {
        return telefone;
    }

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
