package org.example.projetoweb2.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração manual do console web do H2.
 *
 * No Spring Boot 4.x, o servlet do H2 Console pode não ser registrado
 * automaticamente pela autoconfiguração. Esta classe faz o registro
 * explícito do servlet para que o console fique acessível em /h2-console.
 */
@Configuration
public class H2ConsoleConfig {

    /**
     * Registra o servlet do H2 Console manualmente no contexto da aplicação.
     * O console ficará acessível na URL /h2-console/*.
     *
     * @return bean de registro do servlet do H2
     */
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<JakartaWebServlet> registrationBean =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }
}
