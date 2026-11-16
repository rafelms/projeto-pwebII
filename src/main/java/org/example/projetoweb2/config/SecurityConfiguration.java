package org.example.projetoweb2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuração do Spring Security.
 *
 * Define:
 * - as regras de autorização (quais perfis acessam quais URLs);
 * - o formulário de login personalizado, o logout e o "lembrar de mim";
 * - o codificador de senhas (BCrypt).
 *
 * Os usuários e perfis vêm do BANCO DE DADOS: o Spring Security encontra
 * sozinho o único UserDetailsService da aplicação (UsuarioDetailsService)
 * e o PasswordEncoder abaixo, e passa a usá-los no login.
 *
 * Os perfis do sistema (ADMIN, RECEPCAO, MEDICO e PACIENTE) e o que cada um
 * pode fazer estão descritos no enum Papel.
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
     * Algumas regras também levam o verbo HTTP (HttpMethod.POST, PUT, DELETE):
     * a mesma URL /pacientes/5 pode ser consultada, alterada (PUT) ou excluída (DELETE),
     * e cada operação tem a sua permissão.
     *
     * @param http objeto do Spring Security usado para montar a configuração
     * @return cadeia de filtros configurada
     * @throws Exception em caso de erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // console do banco e lista de usuários: somente administrador.
                        // "/usuarios" casa só com a URL exata: "Meus dados" e a troca da própria senha
                        // (/usuarios/meus-dados e /usuarios/senha) caem em anyRequest(), abaixo.
                        // A redefinição da senha de outra conta (/usuarios/{id}/senha) é do ADMIN.
                        .requestMatchers("/h2-console/**", "/usuarios", "/usuarios/*/senha").hasRole("ADMIN")

                        // exclusões (DELETE): somente administrador
                        .requestMatchers(HttpMethod.DELETE, "/pacientes/*", "/medicos/*", "/consultas/*")
                        .hasRole("ADMIN")

                        // médicos: formulários, cadastro (POST) e alteração (PUT) somente para o administrador
                        .requestMatchers("/medicos/novo", "/medicos/*/editar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/medicos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/medicos/*").hasRole("ADMIN")

                        // pacientes e consultas: formulários, cadastro e alteração para administrador e recepção
                        .requestMatchers("/pacientes/novo", "/pacientes/*/editar",
                                "/consultas/nova", "/consultas/*/editar").hasAnyRole("ADMIN", "RECEPCAO")
                        .requestMatchers(HttpMethod.POST, "/pacientes", "/consultas").hasAnyRole("ADMIN", "RECEPCAO")
                        .requestMatchers(HttpMethod.PUT, "/pacientes/*", "/consultas/*").hasAnyRole("ADMIN", "RECEPCAO")

                        // listagens (GET): funcionários da clínica (o PACIENTE não acessa)
                        .requestMatchers(HttpMethod.GET, "/pacientes", "/medicos", "/consultas")
                        .hasAnyRole("ADMIN", "RECEPCAO", "MEDICO")

                        // demais requisições exigem usuário autenticado, de qualquer perfil
                        // (inclusive PACIENTE): página inicial, "Meus dados" e troca da própria senha
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                       // URL da página de login personalizada
                        .defaultSuccessUrl("/", true)              // após logar, "/" escolhe a tela pelo perfil
                        .permitAll()                               // libera a página de login para quem não está logado
                )
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
     * Codificador de senhas BCrypt: gera um hash com "sal" aleatório,
     * por isso a mesma senha produz hashes diferentes a cada execução.
     *
     * É usado em dois momentos:
     * - no cadastro: encode("123") gera o hash gravado na coluna senha;
     * - no login: o Spring Security chama matches(senhaDigitada, hashDoBanco).
     *
     * @return codificador de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
