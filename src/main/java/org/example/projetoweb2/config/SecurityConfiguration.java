package org.example.projetoweb2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuração do Spring Security.
 *
 * Define:
 * - as regras de autorização (quais perfis acessam quais URLs);
 * - o formulário de login personalizado, o logout e o "lembrar de mim";
 * - os usuários e perfis em memória (InMemoryUserDetailsManager);
 * - o codificador de senhas (BCrypt).
 *
 * Perfis do sistema:
 * - ADMIN:    acesso total (inclusive cadastro de médicos, exclusões e console H2);
 * - RECEPCAO: cadastra e edita pacientes e consultas; apenas visualiza médicos;
 * - MEDICO:   apenas visualiza pacientes, médicos e consultas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Cadeia de filtros de segurança aplicada a todas as requisições.
     *
     * As regras de authorizeHttpRequests são avaliadas NA ORDEM em que foram
     * declaradas: a primeira que casar com a URL é aplicada. Por isso as regras
     * mais específicas vêm primeiro e anyRequest() fica por último.
     *
     * @param http objeto do Spring Security usado para montar a configuração
     * @return cadeia de filtros configurada
     * @throws Exception em caso de erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // recursos públicos (página de login e arquivos estáticos)
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // console do banco: somente administrador
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")

                        // exclusões: somente administrador
                        .requestMatchers("/pacientes/excluir/**", "/medicos/excluir/**",
                                "/consultas/excluir/**").hasRole("ADMIN")

                        // cadastro e edição de médicos: somente administrador
                        .requestMatchers("/medicos/novo", "/medicos/editar/**", "/medicos/salvar").hasRole("ADMIN")

                        // cadastro e edição de pacientes e consultas: administrador e recepção
                        .requestMatchers("/pacientes/novo", "/pacientes/editar/**", "/pacientes/salvar",
                                "/consultas/nova", "/consultas/editar/**", "/consultas/salvar")
                        .hasAnyRole("ADMIN", "RECEPCAO")

                        // listagens: qualquer perfil do sistema
                        .requestMatchers("/pacientes", "/medicos", "/consultas")
                        .hasAnyRole("ADMIN", "RECEPCAO", "MEDICO")

                        // demais requisições exigem usuário autenticado
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                       // URL da página de login personalizada
                        .defaultSuccessUrl("/consultas", true)     // para onde ir após logar
                        .permitAll()
                )
                .httpBasic(withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")         // volta ao login com mensagem
                        .permitAll()
                )
                .rememberMe(withDefaults())                        // mantém o usuário autenticado após fechar o navegador
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acesso-negado")        // página amigável no lugar do erro 403
                )
                // o console H2 não envia o token CSRF e é exibido dentro de frames
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    /**
     * Usuários e perfis em memória.
     *
     * roles("ADMIN") grava a autoridade "ROLE_ADMIN"; nas regras usa-se
     * hasRole("ADMIN"), sem o prefixo. As senhas são armazenadas
     * já codificadas pelo PasswordEncoder (BCrypt), nunca em texto puro.
     *
     * @param passwordEncoder codificador de senhas definido em {@link #passwordEncoder()}
     * @return serviço que o Spring Security usa para buscar os usuários no login
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails recepcao = User.builder()
                .username("recepcao")
                .password(passwordEncoder.encode("123"))
                .roles("RECEPCAO")
                .build();

        UserDetails medico = User.builder()
                .username("medico")
                .password(passwordEncoder.encode("123"))
                .roles("MEDICO")
                .build();

        return new InMemoryUserDetailsManager(admin, recepcao, medico);
    }

    /**
     * Codificador de senhas BCrypt: gera um hash com "sal" aleatório,
     * por isso a mesma senha produz hashes diferentes a cada execução.
     *
     * @return codificador de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
