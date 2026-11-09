package org.example.projetoweb2.config;

import org.example.projetoweb2.model.Pessoa;
import org.example.projetoweb2.model.Perfil;
import org.example.projetoweb2.model.Usuario;
import org.example.projetoweb2.repository.MedicoRepository;
import org.example.projetoweb2.repository.PacienteRepository;
import org.example.projetoweb2.repository.PerfilRepository;
import org.example.projetoweb2.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cadastra os perfis e os usuários iniciais ao subir a aplicação.
 *
 * Roda DEPOIS do import.sql (que cria pacientes, médicos e consultas).
 * Foi feito em Java, e não no import.sql, para que as senhas sejam
 * codificadas pelo PasswordEncoder (BCrypt) — nenhuma senha fica em texto puro no banco.
 *
 * Usuários criados:
 * - admin    / admin -> ADMIN    (sem pessoa vinculada)
 * - recepcao / 123   -> RECEPCAO (sem pessoa vinculada)
 * - medico   / 123   -> MEDICO   (vinculado à médica de id 4, Dra. Ana Costa)
 * - paciente / 123   -> PACIENTE (vinculado ao paciente de id 1, João Silva)
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final PerfilRepository perfilRepository;

    private final UsuarioRepository usuarioRepository;

    private final PacienteRepository pacienteRepository;

    private final MedicoRepository medicoRepository;

    private final PasswordEncoder passwordEncoder;

    public DadosIniciais(PerfilRepository perfilRepository, UsuarioRepository usuarioRepository,
                         PacienteRepository pacienteRepository, MedicoRepository medicoRepository,
                         PasswordEncoder passwordEncoder) {
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Perfis gravados SEM o prefixo ROLE_
        Perfil admin = perfilRepository.save(new Perfil("ADMIN"));
        Perfil recepcao = perfilRepository.save(new Perfil("RECEPCAO"));
        Perfil medico = perfilRepository.save(new Perfil("MEDICO"));
        Perfil paciente = perfilRepository.save(new Perfil("PACIENTE"));

        // Funcionários: contas sem pessoa vinculada
        usuarioRepository.save(criarUsuario("admin", "admin", admin));
        usuarioRepository.save(criarUsuario("recepcao", "123", recepcao));

        // Pessoas: a conta é salva junto com a pessoa (cascade = ALL em Pessoa.usuario)
        medicoRepository.findById(4L).ifPresent(m -> vincular(m, criarUsuario("medico", "123", medico)));
        pacienteRepository.findById(1L).ifPresent(p -> vincular(p, criarUsuario("paciente", "123", paciente)));
    }

    /**
     * Cria um usuário com a senha codificada e um perfil.
     */
    private Usuario criarUsuario(String login, String senha, Perfil perfil) {
        Usuario usuario = new Usuario(login, passwordEncoder.encode(senha));
        usuario.adicionarPerfil(perfil);
        return usuario;
    }

    /**
     * Associa a conta à pessoa. Como o método run() é @Transactional, a pessoa
     * está sendo gerenciada pelo JPA e a alteração é gravada ao final da transação.
     */
    private void vincular(Pessoa pessoa, Usuario usuario) {
        pessoa.setUsuario(usuario);
    }
}
