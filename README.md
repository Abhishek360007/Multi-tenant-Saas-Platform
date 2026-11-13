<div align="center">
  <img src="docs/brand/tenanthub-wordmark.png" alt="TenantHub" width="360">

  <p>A multi-tenant, database-per-tenant SaaS foundation with a platform admin console and a tenant portal.</p>
</div>

## Overview

Every tenant gets its own MySQL database (`tenanthub_<tenantId>`). It is created and migrated
automatically by Liquibase when the tenant is registered. Requests are routed to the right
database through the `x-tenant-id` header, and each tenant can also hold several projects.

| Layer | Stack |
| --- | --- |
| Backend | Java 21+, Spring Boot 3.5, MyBatis-Plus, Liquibase, Redis, JWT (RSA-encrypted login) |
| API docs | springdoc OpenAPI 3 + knife4j UI (`/doc.html`) |
| Frontends | Vue 3, TypeScript, Element Plus, Pinia, Vite |
| Deployment | Docker Compose (MySQL 8, Redis 7, Nginx), systemd unit, shell script |

## Features

- **Tenant management**: create tenants, assign permission templates, auto-provision tenant databases
- **RBAC**: users, roles, menus/permissions, departments with data scopes
- **Projects**: multiple projects per tenant
- **India addresses**: state → district → sub-district picker with official LGD codes (7,938 areas)
- **Auth**: captcha + RSA-encrypted credentials, JWT access tokens, online-user tracking, forced logout
- **Monitoring**: login logs, operation logs, live server metrics (CPU, memory, disk)
- **System**: dictionaries, global config, file upload via x-file-storage (Qiniu and Aliyun OSS SDKs included)
- **Real-time**: WebSocket/STOMP notifications

## Repository layout

```
server/                     Spring Boot multi-module backend
  common/                   shared constants, utils, domain types
  internal-api/             interfaces between modules
  tenanthub-sys/            tenants, menus, config, dictionaries
  tenanthub-pms/            users, roles, departments (permission management)
  tenanthub-cms/            projects and devices
  tenanthub-monitor/        login/operation logs, server metrics
  tenanthub-service/        runnable application (TenantHubApplication)
  plugins/                  auth-jwt, mbp-tenant (tenant routing), redis-config, web-config
platform-web-ui/            admin console for platform operators
tenant-web-ui/              portal for tenant users
docker-compose.yml          MySQL, Redis, Nginx and the app
nginx.conf                  reverse proxy for both UIs and the API
```

## Getting started

### Prerequisites

- JDK 21 or newer (builds on 21 and 25)
- Maven 3.9+
- MySQL 8 and Redis 7 (or Docker)
- Node.js 18+ and pnpm for the frontends

### 1. Start MySQL and Redis

```bash
docker run -d --name tenanthub-mysql -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=tenanthub_master \
  -p 3306:3306 mysql:8.4 --lower_case_table_names=1 --character-set-server=utf8mb4
docker run -d --name tenanthub-redis -p 6379:6379 redis:7.4
```

The server needs a MySQL user that can create databases, because each new tenant gets its own schema.

### 2. Run the backend

```bash
cd server
mvn -DskipTests package
java -jar tenanthub-service/target/tenanthub-service-1.0.1.jar --spring.profiles.active=test
```

The `test` profile points at `127.0.0.1:3306` (root/123456) and `127.0.0.1:6379`. Liquibase creates
and seeds `tenanthub_master` on first start. Open the API docs at http://localhost:18891/doc.html.

### 3. Run the frontends

```bash
cd platform-web-ui && pnpm install && pnpm dev
cd tenant-web-ui   && pnpm install && pnpm dev
```

### Default login

| Tenant | Username | Password |
| --- | --- | --- |
| `master` | `admin` | `123456` |

You must choose a new password the first time you sign in. Until you do, the API only allows
changing the password or signing out.

To avoid the default entirely, set `TENANTHUB_BOOTSTRAP_ADMIN_PASSWORD` before the first start. The admin
then uses that password and is not asked to change it.

New tenants get a random 12-character admin password. It is shown once, in the response to the
create (or reset) request, and must be changed at first sign-in. The same applies to any user
whose password is set or reset by an administrator.

## Deployment

1. Copy `.env.example` to `.env` and set real passwords.
2. Build the jar and copy it, along with an `application-pro.yml`, to `/data/tenanthub/` on the host.
3. `docker compose up -d`

See [server/README.md](server/README.md) for the shell script (`server/shell/tenanthub.sh`) and
systemd unit (`server/systemd/tenanthub.service`).

## Configuration

Key settings in `server/tenanthub-service/src/main/resources/application.yml`:

| Property | Default | Meaning |
| --- | --- | --- |
| `multi.tenant.prefix` | `tenanthub_` | prefix for tenant database names |
| `multi.tenant.default-tenant-id` | `master` | the platform's own tenant |
| `multi.tenant.enable-project` | `true` | enable per-tenant projects |
| `jwt.token.keys-dir` | `./keys` | where the JWT secret and RSA key pair are generated on first start. **Keep private and persistent.** |

## Credits & license

This project is derived from [molly-multi-tenant](https://github.com/thousmile/molly-multi-tenant)
by Wang Chen Chen. The frontends are based on [v3-admin-vite](https://github.com/un-pany/v3-admin-vite).
Licensed under the [Apache License 2.0](LICENSE). See [NOTICE](NOTICE) for details of the changes.
