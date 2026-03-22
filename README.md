<h1 align="center">🚗 Lata Velha</h1>

<h3 align="center">Pós-Tech FIAP — Arquitetura de Software</h3>

<p align="center">
  Sistema de gestão automotiva
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-green?logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Compose-blue?logo=docker&logoColor=white" alt="Docker"/>
</p>

---

## Arquitetura

O projeto segue **Domain-Driven Design (DDD)** com arquitetura em camadas, onde cada camada possui responsabilidades bem definidas e as dependências sempre apontam para o centro (domínio).

<p align="center">
  <img src="./documentation/arquitetura-ddd-lata-velha.svg" alt="Arquitetura DDD em camadas" width="700"/>
</p>

```
br.com.lata.velha
├── presentation/        → Entrada HTTP (controllers, exception handlers)
├── application/         → Casos de uso, DTOs e portas de saída
├── domain/              → Regras de negócio puras (zero frameworks)
└── infrastructure/      → JPA, JWT, configs do Spring
```

**Regra principal:** o `domain` não importa nenhuma outra camada. A `infrastructure` implementa as interfaces definidas pelo `domain` e `application`, conectadas via injeção de dependência do Spring.

---

## Pré-requisitos

- **Java 21**
- **Docker Desktop** instalado e rodando

---

## Como rodar

**1. Subir o banco de dados**

```bash
docker compose -f docker/docker-compose.yml up -d
```

**2. Verificar se o container está rodando**

```bash
docker ps
```

**3. Rodar a aplicação**

```bash
./mvnw spring-boot:run
```

**4. Acessar o Swagger**

```
http://localhost:8080/swagger-ui.html
```

---

## Banco de dados

| Propriedade | Valor        |
| ----------- | ------------ |
| Host        | `localhost`  |
| Porta       | `5432`       |
| Database    | `minha_base` |
| Usuário     | `admin`      |
| Senha       | `admin123`   |

---

## Autenticação

O sistema utiliza **JWT com chaves RSA** para autenticação. Após o login, o token deve ser informado no Swagger para acessar os endpoints protegidos.

**Usuários pré-cadastrados:**

| Usuário     | Senha    | Role     |
| ----------- | -------- | -------- |
| `admin`     | `123456` | ADMIN    |
| `atendente` | `123456` | USER     |
| `mecanico`  | `123456` | MECANICO |

**Como autenticar no Swagger:**

1. Faça `POST /auth/login` com username e senha
2. Copie o token retornado (sem aspas)
3. Clique em **Authorize** no Swagger
4. Cole o token e confirme

---

## Parar a aplicação

```bash
docker compose -f docker/docker-compose.yml down -v
```

O flag `-v` remove os volumes, limpando os dados do banco.
