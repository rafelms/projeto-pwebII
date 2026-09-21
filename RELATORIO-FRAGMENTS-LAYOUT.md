# Relatório — Fragmentos e Layout com Thymeleaf + Bootstrap 5

## 1. Visão Geral

Este relatório documenta a implementação dos **fragmentos HTML** e do **padrão Layout Decorator** no projeto de Programação Web II, utilizando o **Thymeleaf Layout Dialect** em conjunto com o **Bootstrap 5** para estilização.

O objetivo principal foi reorganizar todas as páginas da aplicação para **eliminar a duplicação de código HTML**, centralizando a estrutura comum (cabeçalho, barra de navegação, rodapé, imports de CSS/JS) em um **layout único** que é compartilhado por todas as páginas.

---

## 2. O que são Fragmentos no Thymeleaf?

Fragmentos (fragments) são blocos reutilizáveis de HTML que podem ser definidos em um arquivo e incluídos em outros templates. No Thymeleaf, existem duas formas principais de trabalhar com fragmentos:

### 2.1. Abordagem Simples: `th:insert` e `th:replace`

A forma mais básica de reutilizar fragmentos é usando `th:insert` ou `th:replace` para incluir um trecho de HTML de outro arquivo:

```html
<!-- Definindo o fragment em fragments/footer.html -->
<footer th:fragment="rodape">
    <p>Rodapé aqui</p>
</footer>

<!-- Usando o fragment em outra página -->
<div th:replace="~{fragments/footer :: rodape}"></div>
```

- **`th:insert`**: insere o conteúdo do fragment **dentro** da tag host.
- **`th:replace`**: **substitui** a tag host pelo fragment inteiro.

### 2.2. Abordagem Avançada: Layout Decorator (`layout:decorate`)

Em vez de incluir fragments manualmente em cada página, a abordagem **Layout Decorator** inverte a lógica: define-se um **layout mestre** com a estrutura completa da página, e cada página "filha" apenas indica qual layout quer decorar e fornece seu conteúdo específico.

**Esta foi a abordagem adotada neste projeto**, conforme a orientação do professor.

---

## 3. Arquitetura dos Templates

### 3.1. Estrutura de Arquivos

```
src/main/resources/templates/
├── layout.html                 ← Layout mestre (NOVO)
├── fragments/
│   ├── header.html             ← Fragment da navbar (NOVO)
│   └── footer.html             ← Fragment do rodapé (ATUALIZADO)
├── paciente/
│   ├── list.html               ← Lista de pacientes (REFATORADO)
│   └── form.html               ← Formulário de paciente (REFATORADO)
├── medico/
│   ├── list.html               ← Lista de médicos (REFATORADO)
│   └── form.html               ← Formulário de médico (REFATORADO)
└── consulta/
    ├── list.html               ← Lista de consultas (REFATORADO)
    └── form.html               ← Formulário de consulta (REFATORADO)
```

### 3.2. Fluxo de Renderização

```
Usuário acessa /pacientes
         │
         ▼
PacienteController retorna "paciente/list"
         │
         ▼
Thymeleaf processa paciente/list.html
         │
         ▼
Encontra layout:decorate="~{layout}"
         │
         ▼
Carrega layout.html como base
         │
         ├── th:replace="~{fragments/header :: navbar}"  → Insere a navbar
         ├── layout:fragment="conteudo"                  → Substitui pelo conteúdo de paciente/list.html
         └── th:replace="~{fragments/footer :: rodape}"  → Insere o rodapé
         │
         ▼
HTML final completo é enviado ao navegador
```

---

## 4. Arquivos Criados e Modificados

### 4.1. pom.xml — Nova Dependência

**O que foi feito:** Adicionada a dependência do `thymeleaf-layout-dialect`.

**Por quê:** Essa biblioteca estende o Thymeleaf com os atributos `layout:decorate` e `layout:fragment`, permitindo o padrão Layout Decorator. Sem ela, seria necessário incluir os fragments manualmente em cada página com `th:insert`/`th:replace`.

**Como funciona:** O Spring Boot detecta a dependência automaticamente e registra o dialeto no processador Thymeleaf. Nenhuma configuração adicional é necessária.

```xml
<!--
    Thymeleaf Layout Dialect: permite o padrão "layout decorator".
    Com ele, definimos um layout mestre (layout.html) e cada página filha
    "decora" esse layout com layout:decorate, substituindo apenas a área de conteúdo.
    Isso evita repetição de código HTML (head, navbar, footer) em cada template.
-->
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
</dependency>
```

---

### 4.2. layout.html — Template Mestre (NOVO)

**O que é:** O "esqueleto" de todas as páginas da aplicação. Define a estrutura HTML completa que será compartilhada por todas as views.

**Por que foi criado:** Para centralizar em um único lugar tudo que é comum a todas as páginas: o `<head>` (com meta tags, CSS do Bootstrap, Bootstrap Icons), a navbar, o rodapé e o JavaScript do Bootstrap. Assim, cada página filha precisa definir apenas seu conteúdo específico.

**Como funciona:**

1. O `<head>` inclui o Bootstrap 5 CSS e Bootstrap Icons via CDN.
2. `layout:title-pattern="$CONTENT_TITLE - Clínica Saúde Total"` combina o título da página filha com o nome da aplicação. Exemplo: a página que define `<title>Pacientes</title>` gera "Pacientes - Clínica Saúde Total".
3. `th:replace="~{fragments/header :: navbar}"` insere a barra de navegação do arquivo `header.html`.
4. `layout:fragment="conteudo"` define o ponto de substituição — cada página filha fornece seu conteúdo para esta área.
5. `th:replace="~{fragments/footer :: rodape}"` insere o rodapé do arquivo `footer.html`.
6. O Bootstrap 5 JS é carregado no final do `<body>` para performance.

**Trecho principal:**
```html
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <title layout:title-pattern="$CONTENT_TITLE - Clínica Saúde Total">...</title>
    <!-- Bootstrap 5 CSS + Icons -->
</head>
<body>
    <div th:replace="~{fragments/header :: navbar}"></div>   <!-- Navbar -->

    <main class="container mt-4 mb-5">
        <div layout:fragment="conteudo">                     <!-- Área de conteúdo dinâmico -->
            <p>Será substituído pela página filha.</p>
        </div>
    </main>

    <div th:replace="~{fragments/footer :: rodape}"></div>   <!-- Rodapé -->
    <!-- Bootstrap 5 JS -->
</body>
</html>
```

---

### 4.3. fragments/header.html — Barra de Navegação (NOVO)

**O que é:** Fragment reutilizável contendo a navbar (barra de navegação) do Bootstrap 5.

**Por que foi criado:** A navbar aparece em todas as páginas, então foi criada como fragment para ser incluída pelo layout.html uma única vez, evitando repetição.

**Como funciona:**

- `th:fragment="navbar"` define o bloco como fragment, permitindo referenciá-lo via `~{fragments/header :: navbar}`.
- Classes Bootstrap utilizadas:
  - `navbar-expand-lg`: a navbar é horizontal em telas ≥ 992px e vira menu "hambúrguer" em telas menores.
  - `navbar-dark bg-primary`: texto claro sobre fundo azul (identidade visual da clínica).
  - `shadow-sm`: sombra sutil para profundidade visual.
- Links para as 3 seções do CRUD: Pacientes, Médicos e Consultas.
- `th:href="@{/...}"` gera URLs com o context path correto do Spring.
- Ícones do Bootstrap Icons (`bi bi-people`, `bi bi-person-badge`, `bi bi-calendar-check`).

**Personalização aplicada:** Cor azul (`bg-primary`), nome da clínica "Clínica Saúde Total" como brand, ícones temáticos para cada seção.

---

### 4.4. fragments/footer.html — Rodapé (ATUALIZADO)

**O que é:** Fragment reutilizável contendo o rodapé com dados de contato da clínica.

**Por que existe:** O rodapé exibe informações da Pessoa Jurídica (razão social, CNPJ, email, telefone) cadastrada no banco, servindo como assinatura visual em todas as páginas.

**Como funciona:**

- `th:fragment="rodape"` define o bloco como fragment.
- `th:if="${clinica != null}"` garante que o rodapé só aparece se houver uma PessoaJuridica cadastrada.
- A variável `${clinica}` é fornecida **globalmente** pelo `PessoaJuridicaController` (via `@ControllerAdvice` + `@ModelAttribute`), sem necessidade de adicioná-la manualmente em cada controller.
- `th:text="${clinica.razaoSocial}"`, `th:text="${clinica.cnpj}"`, etc., exibem os dados dinamicamente.

---

### 4.5. PessoaJuridicaController.java — Controller Global (já existente)

**O que faz:** Disponibiliza a PessoaJuridica (clínica) como atributo global do model para todas as views.

**Como funciona:**

```java
@ControllerAdvice   // Aplica-se a TODOS os controllers da aplicação
public class PessoaJuridicaController {

    @ModelAttribute("clinica")  // Adiciona "clinica" ao model de TODAS as views
    public PessoaJuridica carregarClinica() {
        return repository.findAll()
                .stream()
                .findFirst()    // Busca a primeira PJ cadastrada
                .orElse(null);  // Retorna null se não houver nenhuma
    }
}
```

- `@ControllerAdvice`: faz com que os métodos `@ModelAttribute` se apliquem a todos os controllers, não apenas a um específico.
- `@ModelAttribute("clinica")`: o retorno do método fica disponível em todas as views Thymeleaf como `${clinica}`.
- Isso permite que o `footer.html` acesse `${clinica.razaoSocial}`, `${clinica.cnpj}`, etc., sem que cada controller precise adicionar esse atributo manualmente.

---

### 4.6. Páginas Refatoradas (6 arquivos)

Todas as 6 páginas foram refatoradas para usar o padrão Layout Decorator. Abaixo, a explicação comum e as particularidades de cada uma.

#### Estrutura comum de todas as páginas:

```html
<html layout:decorate="~{layout}">   <!-- Decora o layout mestre -->
<head>
    <title>Título da Página</title>    <!-- Combinado com o padrão do layout -->
</head>
<body>

<div layout:fragment="conteudo">       <!-- Substitui a área de conteúdo do layout -->
    <!-- Conteúdo específico da página -->
</div>

</body>
</html>
```

**O que mudou em relação à versão anterior:**
- **Removido:** todo o `<head>` (meta tags, CSS do Bootstrap), navbar duplicada, footer duplicado, `<script>` do Bootstrap.
- **Adicionado:** `layout:decorate="~{layout}"` no `<html>` e `layout:fragment="conteudo"` no bloco de conteúdo.
- **Resultado:** cada página contém APENAS seu conteúdo específico, eliminando 100% da duplicação.

---

#### 4.6.1. paciente/list.html — Lista de Pacientes

- **Dados recebidos:** `${pacientes}` — lista de objetos `Paciente` do `PacienteController`.
- **`th:each="p : ${pacientes}"`:** itera sobre a lista, gerando uma `<tr>` para cada paciente.
- **Modais:** para cada paciente, gera um modal Bootstrap com ID dinâmico (`th:id="'modal-' + ${p.id}"`) que exibe as consultas vinculadas.
- **`th:if` / `th:unless`:** controla a exibição da mensagem "Nenhuma consulta" vs a tabela de consultas.
- **`#numbers.formatDecimal(c.valor, 1, 2)`:** formata o valor como moeda (ex: R$ 150,00).

#### 4.6.2. paciente/form.html — Formulário de Paciente

- **Dados recebidos:** `${paciente}` — objeto `Paciente` (vazio ou preenchido).
- **`th:object="${paciente}"`:** vincula o formulário ao objeto Paciente.
- **`th:field="*{nome}"`:** binding bidirecional — preenche o input com o valor do atributo e envia o valor de volta ao controller no POST.
- **Campos refletem a herança:** `nome`, `cpf` (de PessoaFisica), `email`, `telefone` (de Pessoa).
- **Card Bootstrap:** formulário envolto em card com shadow para destaque visual.

#### 4.6.3. medico/list.html — Lista de Médicos

- Estrutura similar ao `paciente/list.html`, com coluna adicional para **CRM** (atributo específico de `Medico`).
- Modais para ver consultas de cada médico.

#### 4.6.4. medico/form.html — Formulário de Médico

- Similar ao `paciente/form.html`, com campo adicional para **CRM** (`th:field="*{crm}"`).
- Campos herdados: `nome`, `cpf` (de PessoaFisica), `email`, `telefone` (de Pessoa).

#### 4.6.5. consulta/list.html — Lista de Consultas

- **`${c.dataFormatada}`:** chama o método `getDataFormatada()` da entidade `Consulta`, que formata o `LocalDateTime` como "dd/MM/yyyy HH:mm".
- **Operadores ternários:** `${c.paciente != null ? c.paciente.nome : 'N/A'}` evita erros se a relação for nula.

#### 4.6.6. consulta/form.html — Formulário de Consulta

- **`type="datetime-local"`:** input HTML5 nativo para seleção de data/hora, vinculado ao `LocalDateTime` da entidade.
- **`type="number" step="0.01"`:** input para valor monetário com precisão de centavos.
- **Selects com `@ManyToOne`:** `th:field="*{paciente.id}"` e `th:field="*{medico.id}"` vinculam os dropdowns às relações de banco de dados.
- **`th:each="p : ${pacientes}"`:** popula os dropdowns com os registros cadastrados.

---

## 5. Bootstrap 5 — Recursos Utilizados

| Recurso | Onde é usado | Descrição |
|---------|-------------|-----------|
| `navbar` | header.html | Barra de navegação responsiva |
| `container` | layout.html | Centraliza e limita a largura do conteúdo |
| `table table-striped table-hover` | Listas | Tabela com listras alternadas e hover |
| `table-primary` | Cabeçalhos de tabela | Fundo azul no cabeçalho |
| `btn btn-success/warning/danger/info` | Botões de ação | Botões coloridos por contexto |
| `card shadow-sm` | Formulários | Container com borda e sombra |
| `form-control / form-select / form-label` | Formulários | Estilização de inputs |
| `modal fade` | Listas paciente/médico | Janelas modais para ver consultas |
| `row / col-md-8 / justify-content-center` | Formulários | Grid system para centralizar |
| `d-flex gap-2` | Botões de formulário | Flexbox para espaçamento |
| `mb-3 / mt-4 / mt-5 / py-3` | Diversos | Margens e paddings utilitários |
| Bootstrap Icons | Navbar, botões | Ícones vetoriais (bi bi-*) |

---

## 6. Comparativo: Antes vs Depois

### ANTES (sem layout):
Cada página repetia **100% da estrutura HTML** — `<head>`, imports CSS, navbar, footer, imports JS.

```html
<!-- CADA PÁGINA tinha tudo isso repetido: -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>...</title>
    <link href="bootstrap.min.css" rel="stylesheet">    <!-- Repetido em 6 arquivos -->
</head>
<body>
    <nav>...</nav>                                       <!-- Navbar repetida em 6 arquivos -->
    <!-- conteúdo -->
    <div th:insert="~{fragments/footer :: rodape}"></div> <!-- Footer repetido em 6 arquivos -->
    <script src="bootstrap.bundle.min.js"></script>       <!-- JS repetido em 6 arquivos -->
</body>
</html>
```

### DEPOIS (com layout decorator):
Cada página define **apenas seu conteúdo específico**. Zero repetição.

```html
<!-- Cada página agora tem APENAS isso: -->
<html layout:decorate="~{layout}">
<head><title>Pacientes</title></head>
<body>
<div layout:fragment="conteudo">
    <!-- Apenas o conteúdo específico desta página -->
</div>
</body>
</html>
```

**A navbar, o footer, os imports de CSS e JS existem em um único lugar: `layout.html`.**

---

## 7. Conceitos-Chave para Explicação ao Professor

1. **`th:fragment`** — Define um bloco de HTML como reutilizável. Exemplo: `th:fragment="navbar"` no header.html.

2. **`th:replace`** — Substitui a tag host pelo fragment referenciado. Usado no layout.html para inserir navbar e footer.

3. **`layout:decorate`** — Indica que a página "filha" quer decorar um layout mestre. O layout fornece toda a estrutura e a página filha fornece apenas o conteúdo.

4. **`layout:fragment`** — Define o ponto de substituição no layout e o bloco de conteúdo na página filha. O nome deve coincidir (ex: `"conteudo"` em ambos).

5. **`layout:title-pattern`** — Combina o `<title>` da página filha com um padrão do layout, gerando títulos como "Pacientes - Clínica Saúde Total".

6. **`@ControllerAdvice` + `@ModelAttribute`** — Disponibiliza dados globalmente para todas as views sem duplicar código nos controllers.

7. **Bootstrap 5 via CDN** — Carregado no layout.html uma única vez, disponível em todas as páginas automaticamente.
