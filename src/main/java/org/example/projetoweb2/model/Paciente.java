package org.example.projetoweb2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um paciente da clínica.
 * Herda de PessoaFisica, que por sua vez herda de Pessoa,
 * formando a hierarquia: Pessoa -> PessoaFisica -> Paciente.
 *
 * Com a estratégia JOINED, o JPA cria uma tabela separada para Paciente.
 * A anotação @PrimaryKeyJoinColumn indica que a coluna 'id' desta tabela
 * é a chave estrangeira que referencia a tabela 'pessoa_fisica'.
 *
 * Atributos herdados de Pessoa: id, email, telefone
 * Atributos herdados de PessoaFisica: nome, cpf
 * Atributos próprios: consultaList (relacionamento com Consulta)
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Paciente extends PessoaFisica {

    /** Lista de consultas vinculadas a este paciente. */
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultaList = new ArrayList<>();

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Paciente() {
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
     * @param consultaList nova lista de consultas
     */
    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }

    /**
     * Retorna uma string formatada com os dados básicos do paciente,
     * incluindo os atributos herdados das superclasses.
     *
     * @return string com id, nome, cpf, email e telefone do paciente
     */
    public String dados() {
        return "Paciente [ID=" + getId()
                + ", Nome=" + getNome()
                + ", CPF=" + getCpf()
                + ", Email=" + getEmail()
                + ", Telefone=" + getTelefone() + "]";
    }

    /**
     * Retorna uma string resumindo as consultas vinculadas a este paciente.
     * Caso não possua consultas, informa que não há consultas cadastradas.
     *
     * @return string com o resumo das consultas do paciente
     */
    public String consultas() {
        if (consultaList == null || consultaList.isEmpty()) {
            return "Nenhuma consulta cadastrada para " + getNome() + ".";
        }

        StringBuilder sb = new StringBuilder("Consultas de " + getNome() + ": ");
        for (Consulta c : consultaList) {
            sb.append(c.dados()).append(" | ");
        }
        return sb.toString();
    }
}
