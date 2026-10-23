# DoeSaúde

Trabalho A03 — Desenvolvimento Web (Usabilidade, Desenvolvimento Web, Mobile e Jogos).

Sistema web que conecta **doadores de sangue e medicamentos** a **instituições de saúde**
(hospitais, hemocentros, ONGs) que precisam desses itens, associado ao
**ODS 3 — Saúde e Bem-Estar**.

**Integrantes do Grupo:**
João Lucas Torquato
Matheus Rocha Nogueira 
André Luiz Gusmão Carneiro De Toledo 
Julia Andrade Silva 
Pedro Ribeiro Linhares
Rosália Pereira Santos

## Como funciona

- Instituições cadastram **necessidades** (de sangue, com tipo sanguíneo específico, ou de
  medicamento), com quantidade e nível de urgência.
- Qualquer pessoa pode ver a lista pública de necessidades em aberto, com filtros.
- Doadores cadastrados agendam uma doação para uma necessidade compatível.
- A instituição confirma o agendamento e marca como concluída depois que a doação acontece;
  quando a quantidade necessária é atingida, a necessidade é encerrada automaticamente.

## Stack

- **Frontend:** Angular 20 (standalone components, signals, novo control flow `@if`/`@for`).
- **Backend:** Java 21 + Spring Boot 4 (Web, Data JPA, Security, Validation), autenticação via JWT.
- **Banco de dados:** PostgreSQL.

## Estrutura

```
backend/   API REST em Spring Boot (Maven)
frontend/  SPA em Angular
docker-compose.yml  Postgres para desenvolvimento local
```

## Rodando localmente

1. Suba o Postgres de desenvolvimento:
   ```
   docker compose up -d
   ```
2. Backend (porta 8080):
   ```
   cd backend
   ./mvnw spring-boot:run
   ```
3. Frontend (porta 4200):
   ```
   cd frontend
   npm install
   npm start
   ```

A API fica em `http://localhost:8080/api`, configurada no frontend em
`frontend/src/environments/environment.ts`.

## Deploy no EasyPanel

Cada pasta (`backend/`, `frontend/`) tem seu próprio `Dockerfile`, pensado para ser publicado
como um app separado no EasyPanel (um para a API, um para o frontend, mais o serviço de
Postgres que já existe no seu servidor).

Variáveis de ambiente que o backend espera em produção:

| Variável          | Descrição                                         |
|-------------------|----------------------------------------------------|
| `DB_HOST`         | host do Postgres (serviço do EasyPanel)            |
| `DB_PORT`         | porta do Postgres (padrão 5432)                    |
| `DB_NAME`         | nome do banco                                       |
| `DB_USER`         | usuário do banco                                    |
| `DB_PASSWORD`     | senha do banco                                      |
| `JWT_SECRET`      | chave secreta para assinar os tokens (troque a de dev!) |
| `PORT`            | porta HTTP da API (o EasyPanel costuma injetar)    |

No frontend, antes de buildar a imagem de produção, ajuste a URL real da API em
`frontend/src/environments/environment.production.ts`.

## Principais endpoints da API

- `POST /api/auth/registrar`, `POST /api/auth/login`
- `GET /api/necessidades` (público, filtros `tipo`, `tipoSanguineo`, `status`)
- `POST /api/necessidades` (instituição autenticada)
- `PATCH /api/necessidades/{id}/cancelar` (instituição autenticada)
- `POST /api/doacoes` (doador autenticado)
- `GET /api/doacoes/minhas` (doador), `GET /api/doacoes/recebidas` (instituição)
- `PATCH /api/doacoes/{id}/status` (instituição autenticada)
