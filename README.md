# projetoA-api

API REST do **Projeto A** — aplicação integrada ao **Conecta**. Valida tokens JWT emitidos pelo `conecta-api`, provisiona usuários e expõe gestão de permissões, grupos e dados do domínio.

Pode ser acessado por **domínio próprio** ou via portal **Conecta** (`https://conecta.sps.ce.gov.br`).

---

## Tecnologias

| Tecnologia | Versão / uso |
|------------|----------------|
| Java | 23 |
| Spring Boot | 3.5.16 (Java 17–23) |
| Spring Security | OAuth2 Resource Server (JWT do Conecta) |
| Spring Data JPA | Persistência |
| PostgreSQL | Banco de dados |
| Flyway | Migrations |
| WebFlux | Cliente HTTP para Conecta |
| Maven | Build |

---

## Integração com Conecta

O Projeto A **não emite** JWT próprio. Ele:

1. Redireciona login para o Conecta ou aceita login direto via `ConectaClient`
2. Valida tokens emitidos pelo Conecta via **JWKS** (URI canônica do bootstrap, ou `{CONECTA_*_BASE_URL}/.well-known/jwks.json`)
3. Provisiona/sincroniza usuário local a partir das claims do JWT
4. Suporta **handoff** (ticket) para sessão via `/auth/conecta/complete`

Na subida, `ConectaIntegrationConfigLoader` busca no Conecta (`/internal/integracao/*`) as chaves de handoff **e** metadados (`issuer`, `jwksUri`, `urlBase`, TTL, etc.). Isso **não** fica no env.

---

## Perfis Spring

| Profile | Arquivo YAML | Variáveis |
|---------|--------------|-----------|
| `dev` | `application-dev.yml` | `config/projetoA-api.dev.env` |
| `homolog` | `application-homolog.yml` | `config/projetoA-api.homolog.env` |
| `prod` | `application-prod.yml` | `config/projetoA-api.prod.env` |

Profile padrão na IDE: `dev`.

---

## Variáveis de ambiente principais

| Variável | Descrição |
|----------|-----------|
| `DB_*` | Conexão PostgreSQL |
| `CONECTA_INTERNAL_BASE_URL` | URL do Conecta para bootstrap S2S (`http://conecta-api:8080`) |
| `CONECTA_SISTEMA_CODIGO` | Sigla do sistema no Conecta (`PROJETO_A`) |
| `CONECTA_CLIENT_SECRET` | Secret S2S em claro gerado no painel Conecta |
| `APP_API_INTERNAL_URL` | URL interna desta API (deve coincidir com `api_internal_url` no painel Conecta) |
| `CONECTA_BOOTSTRAP_OPTIONAL` | (dev) `true` — não aborta subida se bootstrap/handoff falhar |
| `APP_CORS_ALLOWED_ORIGINS` | Origens permitidas (domínio próprio + Conecta) |
| `STORAGE_TYPE` | `minio` ou `local` (local também se `MINIO_URL` vazio) |
| `MINIO_URL` | Endpoint MinIO |
| `MINIO_BUCKET` | Bucket (`spscloud`) |
| `MINIO_DEFAULT_FOLDER` | Pasta no bucket (`sistemas/projetoa/`) |
| `MINIO_ACCESS_NAME` / `MINIO_ACCESS_SECRET` | Credenciais MinIO |
| `PROJETOA_STORAGE_LOCAL_ANEXOS` | Pasta local quando storage em disco |
| `AUTH_COOKIE_SECURE` | `true` em HTTPS |
| `SPRINGDOC_SWAGGER_UI_ENABLED` | Swagger em `/docs` |

Issuer JWT, JWKS e metadados do sistema vêm do bootstrap Conecta (`GET /internal/integracao/config`). Não use `CONECTA_ISSUER_URI` / `CONECTA_BASE_URL` nos envs.


Arquivos por ambiente: `config/projetoA-api.dev.env`, `config/projetoA-api.homolog.env`, `config/projetoA-api.prod.env`.

### Domínio duplo (próprio + Conecta)

Configure `APP_CORS_ALLOWED_ORIGINS` com **ambas** as origens:

```properties
APP_CORS_ALLOWED_ORIGINS=https://meu-projeto.sps.ce.gov.br,https://conecta.sps.ce.gov.br
```

O callback `/auth/conecta-complete` do frontend deve corresponder à `url_base` cadastrada no sistema `PROJETO_A` no Conecta.

---

## Desenvolvimento local

```bash
cp config/projetoA-api.dev.env.example config/projetoA-api.dev.env
# Edite CREDENCIAIS em config/*.dev.env

mvn spring-boot:run
```

API em `http://localhost:8081`.

### Integração Conecta (dev)

Em desenvolvimento, as URLs do Conecta ficam no próprio `config/*-*.dev.env`, apontando para produção via o proxy BFF:

`CONECTA_INTERNAL_BASE_URL=https://conecta.sps.ce.gov.br/conecta-api`

Homologação usa `https://hconecta.sps.ce.gov.br/conecta-api`.

Pontos importantes no modo dev remoto (filho local → Conecta prod):

- O sufixo `/conecta-api` é obrigatório (é o proxy do `conecta-frontend`; sem ele a requisição cai no Next e vira 307 `/login`)
- `CONECTA_CLIENT_SECRET` = secret em claro do sistema no painel Conecta (mesmo valor na API e no frontend do filho)
- `/internal/integracao/**` na allowlist do proxy BFF (auth por client secret)
- `CONECTA_BOOTSTRAP_OPTIONAL=true` — rede de segurança se o proxy/API remoto falhar
- Em cluster/homolog/prod use o Service interno (`http://conecta-api:8080`), sem o sufixo `/conecta-api`


### Validação JWT (via Conecta)

O Projeto A **não possui** par de chaves JWT. O `conecta-api` emite os tokens (`JWT_PRIVATE_KEY` / `JWT_PUBLIC_KEY` ficam **somente no Conecta**).

O Projeto A valida assinatura e issuer com JWKS/issuer retornados no bootstrap (fallback: `{CONECTA_INTERNAL_BASE_URL}/.well-known/jwks.json`).

Não é necessário montar pasta `keys/` no container do Projeto A.


---

## Segurança (alinhada ao conecta-api)

- Cookies HttpOnly (`PROJETO_A_ACCESS_TOKEN`)
- CORS restrito por `APP_CORS_ALLOWED_ORIGINS` com credentials
- CSRF ignorado apenas em rotas públicas de auth
- Rate limit no login
- Handoff via ticket front-channel (`/auth/conecta/complete` e GET `/auth/conecta-complete`)
- Swagger desabilitável em produção

---

## Docker

```bash
docker compose up -d
```

Imagem: `hub.sps.ce.gov.br/conecta/projetoA-api`

Variáveis em `config/projetoA-api.prod.env`. O Projeto A não monta chaves JWT — valida tokens via JWKS do Conecta.

---

## Principais endpoints

| Rota | Descrição |
|------|-----------|
| `POST /auth/login` | Login via Conecta |
| `POST /auth/conecta/complete` | Conclusão handoff (BFF) |
| `GET /auth/conecta-complete` | Fallback handoff via GET (Ingress → API) |
| `GET /auth/session` | Sessão atual |
| `POST /auth/logout` | Logout |
| `GET /usuarios/**` | Gestão de usuários |
| `GET /grupos/**` | Grupos |
| `GET /permissoes/**` | Permissões |

O frontend acessa via proxy BFF em `/projetoA-api/*`.
