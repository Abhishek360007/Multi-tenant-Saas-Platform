<div align="center">
  <img src="docs/brand/tenanthub-wordmark.png" alt="TenantHub" width="360">

  <p>A multi-tenant, database-per-tenant SaaS foundation with a platform admin console and a tenant portal.</p>

  <p>
    <a href="https://github.com/Abhishek360007/Multi-tenant-Saas-Platform/actions/workflows/build.yml"><img src="https://github.com/Abhishek360007/Multi-tenant-Saas-Platform/actions/workflows/build.yml/badge.svg?branch=main" alt="build"></a>
    <a href="https://tenanthub-admin.onrender.com"><img src="https://img.shields.io/website?url=https%3A%2F%2Ftenanthub-api.onrender.com%2Factuator%2Fhealth&label=live%20demo&up_message=online&down_message=sleeping" alt="live demo"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="license"></a>
    <br>
    <img src="https://img.shields.io/badge/Java-21%2B-orange?logo=openjdk&logoColor=white" alt="Java 21+">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 3.5">
    <img src="https://img.shields.io/badge/Vue-3-4FC08D?logo=vuedotjs&logoColor=white" alt="Vue 3">
    <img src="https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white" alt="TypeScript">
    <img src="https://img.shields.io/badge/MySQL-8%20%7C%20TiDB-4479A1?logo=mysql&logoColor=white" alt="MySQL 8 or TiDB">
    <img src="https://img.shields.io/badge/payments-Razorpay-0C2451?logo=razorpay&logoColor=white" alt="Razorpay">
  </p>
</div>

## Live demo

| | |
| --- | --- |
| Platform admin | https://tenanthub-admin.onrender.com |
| Tenant portal | https://tenanthub-portal.onrender.com |
| API health | https://tenanthub-api.onrender.com/actuator/health |

Hosted on free plans (Render, TiDB Cloud, Redis Cloud); payments run in Razorpay test mode.
If the server has been idle, the first request can take a minute while it wakes up.

## Screenshots

<table>
  <tr>
    <td width="50%"><img src="docs/screenshots/portal-billing.png" alt="Tenant portal: subscription plans in rupees with payment history"><br><sub><b>Tenant portal: Billing.</b> Plans in ₹ + 18% GST, usage against plan limits, paid invoices.</sub></td>
    <td width="50%"><img src="docs/screenshots/portal-billing-quote.png" alt="Order summary with CGST and SGST"><br><sub><b>Order summary.</b> CGST + SGST within the seller's state, IGST otherwise; paid through Razorpay.</sub></td>
  </tr>
  <tr>
    <td><img src="docs/screenshots/admin-tenants.png" alt="Platform admin: tenant list"><br><sub><b>Admin: Tenants.</b> Each tenant gets its own database, with Indian address areas, PIN and GSTIN.</sub></td>
    <td><img src="docs/screenshots/admin-tenant-create.png" alt="Add tenant form"><br><sub><b>Add tenant.</b> GSTIN is checked by format and checksum, PAN by format.</sub></td>
  </tr>
  <tr>
    <td><img src="docs/screenshots/admin-plans.png" alt="Platform admin: subscription plans"><br><sub><b>Admin: Plans.</b> Monthly and yearly prices, user and project limits.</sub></td>
    <td><img src="docs/screenshots/admin-dashboard.png" alt="Dashboard"><br><sub><b>Dashboard.</b> Sample charts.</sub></td>
  </tr>
  <tr>
    <td><img src="docs/screenshots/login.png" alt="Sign-in page"><br><sub><b>Sign in</b> with password + captcha, or a one-time code by email/SMS.</sub></td>
    <td></td>
  </tr>
</table>

The demo businesses, GSTINs and contacts in these screenshots are made up.

## Overview

Every tenant gets its own MySQL database (`tenanthub_<tenantId>`). It is created and migrated
automatically by Liquibase when the tenant is registered. Requests are routed to the right
database through the `x-tenant-id` header, and each tenant can also hold several projects.

| Layer | Stack |
| --- | --- |
| Backend | Java 21+, Spring Boot 3.5, MyBatis-Plus, Liquibase, Redis, JWT (RSA-encrypted login) |
| API docs | springdoc OpenAPI 3 + knife4j UI (`/doc.html`) |
| Frontends | Vue 3, TypeScript, Element Plus, Pinia, Vite |
| Deployment | Render Blueprint (free: Render + TiDB Cloud + Redis Cloud), Docker Compose (MySQL 8, Redis 7, Nginx), systemd unit, shell script |

## Features

- **Tenant management**: create tenants, assign permission templates, auto-provision tenant databases
- **RBAC**: users, roles, menus/permissions, departments with data scopes
- **Projects**: multiple projects per tenant
- **Subscriptions**: Free / Basic / Pro plans in ₹ with user and project limits, GST (CGST + SGST or IGST), Razorpay payments (UPI, cards, netbanking, wallets) with signed webhooks and financial-year invoice numbers
- **India addresses**: state → district → sub-district picker with official LGD codes (7,938 areas)
- **Auth**: captcha + RSA-encrypted credentials, or one-time codes by email/SMS (MSG91); JWT access tokens, online-user tracking, forced logout
- **Monitoring**: login logs, operation logs, live server metrics (CPU, memory, disk)
- **System**: dictionaries, global config, file upload via x-file-storage (local disk or any S3-compatible store)
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
java -jar tenanthub-service/target/tenanthub-service-1.0.1.jar
```

By default it connects to MySQL at `127.0.0.1:3306` (root/123456) and Redis at `127.0.0.1:6379`;
override with the environment variables below. Liquibase creates and seeds `tenanthub_master` on
first start. Open the API docs at http://localhost:18891/doc.html.

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

**Free hosting:** [`render.yaml`](render.yaml) creates the API (Docker) and both websites (static sites) on Render.
The step-by-step guide is in [docs/deploy-render.md](docs/deploy-render.md):

| Part | Service (free plan) |
| --- | --- |
| API server | Render web service, Docker, Singapore |
| Admin and tenant websites | Render static sites |
| Database | TiDB Cloud Starter (MySQL-compatible, TLS on port 4000) |
| Redis | Redis Cloud, Upstash, or a Render Key Value you already have |
| Keep-awake | UptimeRobot pinging `/actuator/health` every 5 minutes |
| Payments | Razorpay, webhook at `https://<api>/razorpay/webhook` |

**Your own server (Docker Compose):**

1. Copy `.env.example` to `.env` and set real passwords.
2. Build the jar and copy it to `/data/tenanthub/tenanthub.jar` on the host.
   Optionally put `GeoLite2-City.mmdb` in `/data/tenanthub/geoip/` for login locations.
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

Environment variables (defaults suit local development; the `prod` profile requires the credentials):

| Variable | Default | Meaning |
| --- | --- | --- |
| `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD` | `127.0.0.1`, `3306`, `root`, `123456` | MySQL; the user must be allowed to `CREATE DATABASE` |
| `DB_SSL_MODE`, `DB_POOL_SIZE` | `DISABLED`, `10` | MySQL TLS mode (`VERIFY_IDENTITY` for TiDB Cloud) and connection pool size |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` | `127.0.0.1`, `6379`, empty | Redis |
| `REDIS_DATABASE`, `REDIS_SSL` | `1` (`0` in prod), `false` | Redis database number (hosted Redis usually only has `0`) and TLS (`true` for Upstash) |
| `JWT_TOKEN_SECRET` | generated in `keys-dir` | JWT signing secret; set it where the disk is not persistent so sign-ins survive restarts |
| `STORAGE_PLATFORM` | `local-1` | `local-1` (disk, served at `/files/**`) or `s3-1` |
| `STORAGE_LOCAL_PATH`, `STORAGE_LOCAL_DOMAIN` | `<working dir>/uploads/`, `http://127.0.0.1:18891/files/` | where local files are stored (absolute path) and their public URL |
| `S3_ENABLED`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, `S3_REGION`, `S3_ENDPOINT`, `S3_BUCKET`, `S3_PUBLIC_URL` | region `ap-south-1` | any S3-compatible store: AWS (Mumbai), DigitalOcean Spaces (blr1), MinIO, Cloudflare R2 |
| `GEOIP_CITY_DB` | `./data/GeoLite2-City.mmdb` | [MaxMind GeoLite2 City](https://dev.maxmind.com/geoip/geolite2-free-geolocation-data) database (free account) for login locations; without it they show "Unknown" |
| `TENANTHUB_BOOTSTRAP_ADMIN_PASSWORD` | empty | initial password for the platform admin |
| `CORS_ALLOWED_ORIGINS` | `*` | websites allowed to call the API from a browser, comma-separated (e.g. `https://admin.example.com,https://portal.example.com`) |
| `API_DOCS_ENABLED` | `true` (`false` in prod) | serve the OpenAPI spec and Swagger UI |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` | empty, `587` | SMTP for email sign-in codes (e.g. Amazon SES Mumbai, Zoho Mail) |
| `MSG91_AUTH_KEY`, `MSG91_TEMPLATE_ID`, `MSG91_OTP_VARIABLE` | empty, empty, `otp` | SMS sign-in codes via MSG91 (needs a DLT-approved template) |
| `GST_SELLER_STATE_CODE` | `29` (Karnataka) | GST state code of your business: CGST + SGST for tenants in that state, IGST otherwise |
| `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET` | empty | Razorpay API keys (test keys start with `rzp_test_`); without them online payment is disabled |
| `RAZORPAY_WEBHOOK_SECRET` | empty | secret for the webhook at `https://your-api-domain/razorpay/webhook` (events `payment.captured`, `order.paid`) |
| `INVOICE_PREFIX` | `TH` | invoice numbers look like `TH/2026-27/000001` (per financial year) |

Personal overrides can go in `application-local.yml` (git-ignored); run with `--spring.profiles.active=dev,local`.

## Changelog

Newest first. Full history: `git log`.

**Tenant isolation hardening**
- The tenant context (tenant, project, custom-tenant flag) is cleared at the start and end of every request by a servlet filter, so a value can no longer carry over to the next request on a reused server thread
- The tenant database is selected with `Connection.setCatalog` instead of a `USE` statement, and the pool resets each connection to the master database when it is returned (`spring.datasource.hikari.catalog`), so a pooled connection never keeps another tenant's database
- The `x-tenant-id` header now takes precedence over the URL parameter of the same name

**Production polish**
- API docs (`/v3/api-docs`, Swagger UI) are off in production; `API_DOCS_ENABLED=true` turns them back on
- Only the TenantHub websites may call the API from a browser (`CORS_ALLOWED_ORIGINS`)
- A sleeping or unreachable server shows a clear "may be waking up" message; requests wait 30 seconds
- Payment history shows plan names; tenant list and add-tenant form no longer squash their text
- README screenshots and badges

**Free deployment (Render + TiDB Cloud + Redis Cloud)**
- Every tenant gets the Billing menu, whatever permission template it was created with
- Dashboard sample data uses Indian apps (WhatsApp, Paytm, Flipkart, Meesho, Myntra, Nykaa…)
- Fixed CORS: browser preflights were rejected by the JWT filter, blocking sign-in from websites on another domain
- Committed the pnpm lockfiles and the websites' shared `.env`; fixed the static site build on Render
- Redis connection is configurable (host, port, password, database, TLS) so an external free Redis works
- Render Blueprint, Dockerfile tuned for 512 MB / 0.1 CPU, TiDB Cloud TLS support
- Fixed the new-tenant result dialog layout and the documented webhook path

**Indian business features**
- Subscription plans in ₹ with 18% GST (CGST + SGST or IGST) and Razorpay payments with signed webhooks
- One-time code sign-in by email and SMS (MSG91)
- GSTIN, PAN and PIN code for tenants; Indian phone number validation
- Indian date (DD-MM-YYYY) and money (lakh/crore) formats
- India states, districts and sub-districts (LGD codes) instead of China regions
- China-specific services replaced with Indian/global equivalents

**Rebrand and hardening**
- Renamed to TenantHub with a new logo; the whole project translated to English
- Per-deployment JWT secret and RSA keys instead of shipped ones
- Random initial passwords with a forced change at first sign-in
- Runs in UTC with the en_US locale; times shown in the viewer's time zone
- Removed the template's demo menus and the demo notification job
- Test suites pass and run in CI

## Credits & license

This project is derived from [molly-multi-tenant](https://github.com/thousmile/molly-multi-tenant)
by Wang Chen Chen. The frontends are based on [v3-admin-vite](https://github.com/un-pany/v3-admin-vite).
Licensed under the [Apache License 2.0](LICENSE). See [NOTICE](NOTICE) for details of the changes.
