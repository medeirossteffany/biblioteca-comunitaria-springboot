# 📚 Projeto Biblioteca Online - CP05 (FIAP)

![Tela do Sistema](/Users/steffanymedeiros/Downloads/biblioteca-comunitaria-springboot/src/main/resources/static/css/capturadetela.png)

> **CP05 - DDD - Projeto Biblioteca Online - Spring Boot**  

Aplicação web simples para gerenciamento de livros e empréstimos de uma **biblioteca comunitária**, desenvolvida em **Java Spring Boot** com **MVC, JPA e Thymeleaf**.

---

## ✅ Requisitos Funcionais

### 1. Cadastro de Livros
- Cada livro possui: **título, autor, ano de publicação e status** (disponível ou emprestado).  
- Possível **inserir, editar e excluir** livros.  
- Novo livro inicia como **disponível**.  

### 2. Cadastro de Usuários
- Cada usuário possui: **nome e e-mail válido**.  
- Possível **inserir e listar** usuários.  

### 3. Registro de Empréstimos
- Um empréstimo deve estar vinculado a:  
  - **Livro** (passa a “emprestado”).  
  - **Usuário** (quem pegou o livro).  
  - **Data de retirada**.  
  - **Data de devolução prevista**.  
- Ao devolver, o livro volta para **disponível**.  

### 4. Listagem
- **Todos os livros cadastrados**.  
- **Somente livros disponíveis**.  
- **Empréstimos ativos** (quem pegou, qual livro e até quando).  

---

## 🔧 Requisitos Técnicos

- **Spring Boot** (Web, JPA, Thymeleaf).  
- Banco de dados **H2** (padrão).  

# ▶️ Como Executar

Basta rodar a classe principal `BibliotecaApplication.java`.  
A aplicação será iniciada em:
http://localhost:8080
