package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um médico da clínica.
 * Herda de PessoaFisica, que por sua vez herda de Pessoa,
 * formando a hierarquia: Pessoa -> PessoaFisica -> Medico.
 *
 * Com a estratégia JOINED, o JPA cria uma tabela separada para Medico.
 * A anotação @PrimaryKeyJoinColumn indica que a coluna 'id' desta tabela
 * é a chave estrangeira que referencia a tabela 'pessoa_fisica'.
 *
 * Atributos herdados de Pessoa: id, email, telefone
 * Atributos herdados de PessoaFisica: nome, cpf
 * Atributos próprios: crm, consultaList
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Medico extends PessoaFisica {

    /** Registro do médico no Conselho Regional de Medicina. */
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
     * @param crm novo CRM
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
     * @param consultaList nova lista de consultas
     */
    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }

    /**
     * Retorna uma string formatada com os dados do médico,
     * incluindo os atributos herdados das superclasses.
     *
     * @return string com id, nome, crm, cpf, email e telefone
     */
    public String dados() {
        return "Medico [ID=" + getId()
                + ", Nome=" + getNome()
                + ", CRM=" + crm
                + ", CPF=" + getCpf()
                + ", Email=" + getEmail()
                + ", Telefone=" + getTelefone() + "]";
    }

    /**
     * Retorna uma string resumindo as consultas vinculadas a este médico.
     * Caso não possua consultas, informa que não há consultas cadastradas.
     *
     * @return string com o resumo das consultas do médico
     */
    public String consultas() {
        if (consultaList == null || consultaList.isEmpty()) {
            return "Nenhuma consulta cadastrada para Dr(a). " + getNome() + ".";
        }

        StringBuilder sb = new StringBuilder("Consultas de Dr(a). " + getNome() + ": ");
        for (Consulta c : consultaList) {
            sb.append(c.dados()).append(" | ");
        }
        return sb.toString();
    }
}
