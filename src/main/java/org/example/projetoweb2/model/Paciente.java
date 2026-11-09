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
 * Atributos herdados de Pessoa: id, email, telefone, usuario
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

    public List<Consulta> getConsultaList() {
        return consultaList;
    }

    public void setConsultaList(List<Consulta> consultaList) {
        this.consultaList = consultaList;
    }
}
