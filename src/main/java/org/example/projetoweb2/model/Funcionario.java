package org.example.projetoweb2.model;

public class Funcionario {
    private Long id;
    private String nome;
    private String departamento;
    private Double salario;

    public Funcionario() {}

    public Funcionario(Long id, String nome, String departamento, Double salario) {
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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }
}
