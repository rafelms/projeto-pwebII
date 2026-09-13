# Projeto Web II - Spring Boot

**Estudante:** Rafael Menezes
**Professor:** Fagno Fonseca

---

## Sobre o Repositório

Este projeto tem como objetivo demonstrar a evolução do aprendizado no desenvolvimento web utilizando Java e Spring Boot ao longo da disciplina.

A proposta é que a aplicação comece utilizando os fundamentos básicos e, com o passar das aulas, seja aprimorada gradativamente. O projeto evoluirá para integrar novas tecnologias e abordagens à medida que exploramos as futuras dependências e funcionalidades oferecidas pelo ecossistema Spring (como Spring Data JPA, Spring Security, entre outras).

## Organização e Avaliação

Para facilitar o acompanhamento e a avaliação, o desenvolvimento do projeto foi organizado em branches baseadas nas atividades e nas datas das aulas.

Cada nova atividade ou versão do projeto será implementada em sua própria branch, utilizando o seguinte padrão de nomenclatura:
`atividade-DD-MM-YYYY`

Exemplo: `atividade-01-08-2026`

## Executando o Projeto

O projeto utiliza o banco de dados H2 (em memória) para simplificar a configuração inicial.

Para executar localmente, utilize o comando na raiz do projeto:
```bash
.\mvnw spring-boot:run
```
A aplicação estará disponível em `http://localhost:8080/`.

---

## DB no H2

1. Acesse: 👉 http://localhost:8080/h2-console/
2. Na tela de login, preencha os dados exatamente assim:                                                                                                                                                                    
   • JDBC URL: jdbc:h2:mem:clinicadb                                                                                                                                                                                       
   • User Name: sa                                                                                                                                                                                                         
   • Password: (deixe em branco)
3. Clique em Connect            
