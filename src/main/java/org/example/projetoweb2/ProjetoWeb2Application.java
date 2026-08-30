package org.example.projetoweb2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Spring Boot.
 * Responsável por inicializar o contexto do Spring e subir o servidor embutido.
 */
@SpringBootApplication
public class ProjetoWeb2Application {

    /**
     * Método main que inicia a aplicação Spring Boot.
     *
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(ProjetoWeb2Application.class, args);
    }

}
