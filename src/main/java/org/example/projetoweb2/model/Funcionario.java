package org.example.projetoweb2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade Funcionario mapeada para o banco de dados usando JPA.
 */
@Entity
public class Funcionario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    
    @Enumerated(EnumType.STRING)
    private Departamento departamento;
    
    private BigDecimal salario;

    public Funcionario() {}

    public Funcionario(Long id, String nome, Departamento departamento, BigDecimal salario) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
        this.salario = salario;
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

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }
}
