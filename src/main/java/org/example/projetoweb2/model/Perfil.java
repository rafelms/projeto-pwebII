package org.example.projetoweb2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um papel (role) de acesso ao sistema.
 *
 * O nome é um valor do enum Papel, gravado SEM o prefixo "ROLE_"
 * (ex.: ADMIN, RECEPCAO, MEDICO, PACIENTE). O prefixo é adicionado pelo
 * Spring Security no login (UsuarioDetailsService, método roles()),
 * pois é assim que ele reconhece um papel em hasRole("ADMIN").
 *
 * O que cada papel pode fazer está descrito no enum Papel.
 */
@Entity
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificação do papel: única, pois dois perfis não podem ter o mesmo nome.
     * É um valor do enum Papel, gravado pelo nome (EnumType.STRING). Não tem setter:
     * o papel de um perfil não muda depois de criado.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private Papel nome;

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
     * @param nome papel do perfil
     */
    public Perfil(Papel nome) {
        this.nome = nome;
    }

    public Papel getNome() {
        return nome;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
