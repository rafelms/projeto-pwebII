package org.example.projetoweb2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um papel (role) de acesso ao sistema.
 *
 * O nome é gravado SEM o prefixo "ROLE_" (ex.: ADMIN, RECEPCAO, MEDICO, PACIENTE).
 * O prefixo é adicionado no UsuarioDetailsService, no momento do login,
 * pois é assim que o Spring Security reconhece um papel em hasRole("ADMIN").
 *
 * Perfis do sistema:
 * - ADMIN:    acesso total (inclusive médicos, exclusões, usuários e console H2);
 * - RECEPCAO: cadastra e edita pacientes e consultas; apenas visualiza médicos;
 * - MEDICO:   apenas visualiza pacientes, médicos e consultas;
 * - PACIENTE: usuário comum, acessa somente os próprios dados ("Meus dados").
 */
@Entity
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificação do papel: única, pois dois perfis não podem ter o mesmo nome. */
    @Column(nullable = false, unique = true, length = 30)
    private String nome;

    /**
     * Lado inverso do relacionamento muitos-para-muitos.
     * mappedBy = "perfis" indica que quem controla a tabela usuario_perfil é a classe Usuario.
     */
    @ManyToMany(mappedBy = "perfis")
    private List<Usuario> usuarios = new ArrayList<>();

    /**
     * Construtor padrão exigido pelo JPA.
     */
    public Perfil() {
    }

    /**
     * Cria um perfil com o nome informado.
     *
     * @param nome nome do papel, sem o prefixo ROLE_
     */
    public Perfil(String nome) {
        this.nome = nome;
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

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
