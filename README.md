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

## O Sistema (branch `atividade-29-10-2026`)

Sistema de uma clínica médica: cadastro de pacientes e médicos, agenda de consultas e controle de acesso por perfil.

| Pacote | Conteúdo |
|---|---|
| `model` | Entidades JPA (`Pessoa` → `PessoaFisica` → `Paciente`/`Medico`, `PessoaJuridica`, `Consulta`, `Usuario`, `Perfil`) e enums (`Papel`, `StatusConsulta`, `TipoConsulta`) |
| `dto` | Objetos dos formulários e filtros (`PessoaFisicaForm`, `MedicoForm`, `ConsultaForm`, `AlterarSenha`, `RedefinirSenha`, `FiltroPessoa`, `FiltroConsulta`). Os formulários nunca são ligados direto às entidades |
| `controller` | Um controller por tela (`PacienteController`, `MedicoController`, `ConsultaController`, `UsuarioController`, `LoginController`) e os `@ControllerAdvice` (`ClinicaAdvice`, `GlobalBindingAdvice`) |
| `service` | Regras das contas de acesso (`UsuarioService`) e login pelo banco (`UsuarioDetailsService`) |
| `repository` | Repositórios Spring Data JPA |
| `config` | `SecurityConfiguration` (regras de acesso) e `DadosIniciais` (perfis e usuários de teste) |

## Executando o Projeto

O projeto utiliza o banco de dados H2 (em memória) para simplificar a configuração inicial.

Para executar localmente, utilize o comando na raiz do projeto:
```bash
.\mvnw spring-boot:run
```
A aplicação estará disponível em `http://localhost:8080/`.

### Usuários para teste

| Usuário | Senha | Perfil | O que acessa |
|---|---|---|---|
| `admin` | `admin` | ADMIN | Tudo, inclusive médicos, exclusões, usuários e o console H2 |
| `recepcao` | `123` | RECEPCAO | Cadastra e edita pacientes e consultas; visualiza médicos |
| `medico` | `123` | MEDICO | Visualiza pacientes, médicos e consultas |
| `paciente` | `123` | PACIENTE | Somente "Meus dados" |

O administrador pode redefinir a senha de qualquer conta pelo botão **Alterar senha** da tela `/usuarios` (modal por linha, sem pedir a senha atual). Cada usuário continua trocando a própria senha em "Meus dados", informando a senha atual.

---

## DB no H2

O console do H2 é restrito ao perfil ADMIN.

1. Entre no sistema como `admin` / `admin`.
2. Acesse: 👉 http://localhost:8080/h2-console/ (ou o link **H2** no menu)
3. Na tela de login do console, preencha os dados exatamente assim:
   • JDBC URL: jdbc:h2:mem:clinicadb
   • User Name: sa
   • Password: (deixe em branco)
4. Clique em Connect
