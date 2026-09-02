# brmusics-api

API REST do **BRMusics** — gestão de músicos, celebrações, escalas, repertório e comunicação WhatsApp.

Autenticação **própria** (CPF/senha). A API emite JWT HS256 local e define o cookie HttpOnly `BRMUSICS_ACCESS_TOKEN`. Não há integração com Conecta.

---

## Tecnologias

| Tecnologia | Versão / uso |
|------------|----------------|
| Java | 23 |
| Spring Boot | 3.5.16 |
| Spring Security | Resource Server JWT local |
| Spring Data JPA | Persistência |
| PostgreSQL | Banco de dados |
| Flyway | Migrations |
| Maven | Build |

---

## Perfis Spring

| Profile | Arquivo YAML | Variáveis |
|---------|--------------|-----------|
| `dev` | `application-dev.yml` | `config/brmusics-api.dev.env` |
| `homolog` | `application-homolog.yml` | `config/brmusics-api.homolog.env` |
| `prod` | `application-prod.yml` | `config/brmusics-api.prod.env` |

Profile padrão na IDE: `dev`.

---

## Variáveis de ambiente principais

| Variável | Descrição |
|----------|-----------|
| `DB_*` | Conexão PostgreSQL |
| `APP_API_INTERNAL_URL` | URL interna desta API |
| `APP_CORS_ALLOWED_ORIGINS` | Origens permitidas (domínio do frontend) |
| `JWT_SECRET` | Segredo HMAC (mínimo 32 caracteres) |
| `JWT_ISSUER` | Emissor do token (`brmusics-api`) |
| `STORAGE_TYPE` | `minio` ou `local` |
| `MINIO_*` | Endpoint, bucket e credenciais MinIO |
| `BRMUSICS_STORAGE_LOCAL_ANEXOS` | Pasta local quando storage em disco |
| `AUTH_COOKIE_SECURE` | `true` em HTTPS |
| `SPRINGDOC_SWAGGER_UI_ENABLED` | Swagger em `/docs` |
| `WHATSAPP_*` | Integração WhatsApp Business |

Arquivos por ambiente: `config/brmusics-api.dev.env`, `config/brmusics-api.homolog.env`, `config/brmusics-api.prod.env`.

---

## Desenvolvimento local

```bash
cp config/brmusics-api.dev.env.example config/brmusics-api.dev.env
# Edite CREDENCIAIS em config/*.dev.env

mvn spring-boot:run
```

API em `http://localhost:8081`.

Em `dev`, se nenhum usuário tiver senha, é criado o administrador `00000000000` / `Admin@123`.

---

## Segurança

- Cookies HttpOnly (`BRMUSICS_ACCESS_TOKEN`)
- CORS restrito por `APP_CORS_ALLOWED_ORIGINS` com credentials
- CSRF ignorado apenas em rotas públicas de auth, webhook WhatsApp e health
- Rate limit no login (5 tentativas / 60s por IP)
- Swagger desabilitável em produção

---

## Docker

```bash
docker compose up -d
```

Imagem: `ernandopaiva/brmusics-api`. Variáveis em `config/brmusics-api.prod.env`.

---

## Principais endpoints

| Rota | Descrição |
|------|-----------|
| `POST /auth/login` | Login CPF/senha |
| `GET /auth/session` | Sessão atual |
| `POST /auth/logout` | Logout |
| `GET /musicos/**` | Músicos |
| `GET /escalas/**` | Escalas e publicação mensal |
| `GET /minha-escala/**` | Área do músico |
| `GET /repertorios/**` | Repertórios |
| `GET /whatsapp/**` | Comunicação WhatsApp |

O frontend acessa via proxy BFF em `/brmusics-api/*`.
