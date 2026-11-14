# Deploy TenantHub for free: Render + TiDB Cloud

| Part | Service | Free plan |
| --- | --- | --- |
| API server | Render web service (Docker) | 512 MB RAM, sleeps after 15 min idle |
| Redis | Upstash, Redis Cloud, or a Render Key Value you already have | 256 MB / 30 MB / 25 MB |
| Admin + tenant websites | Render static sites | free |
| Database | TiDB Cloud Serverless (MySQL-compatible) | 5 GB |

The three Render services are described in [`render.yaml`](../render.yaml), so Render creates them at once.
Render allows only one free Key Value (Redis) per account, so the Blueprint doesn't create one; you give it the
details of a Redis instead (step 2).

## 1. Create the database (TiDB Cloud)

1. Sign up at https://tidbcloud.com (free, no card needed).
2. **Create Cluster** > **Serverless** > region **AWS Mumbai (ap-south-1)** > Create.
3. Open the cluster > **Connect** > connection type **General**.
4. Click **Generate Password** and copy these three values somewhere safe:
   - **Host** (like `gateway01.ap-south-1.prod.aws.tidbcloud.com`)
   - **User** (like `2xYzAbCd.root`)
   - **Password**

You don't need to create any database: TenantHub creates `tenanthub_master` itself.

## 2. Get a Redis

Pick one:

- **Upstash (simplest):** sign up at https://upstash.com > **Create Database** > Redis, region **ap-south-1 (Mumbai)**
  or **ap-southeast-1 (Singapore)**, free plan. On the database page note the **Endpoint** (host), and the
  **Password** (the token). Use `REDIS_PORT` = `6379`, `REDIS_SSL` = `true` and `REDIS_DATABASE` = `0`.
- **Redis Cloud:** sign up at https://redis.io/try-free > create a **free** database (30 MB) on AWS, region
  **ap-south-1 (Mumbai)** or **ap-southeast-1 (Singapore)**. From the database's **Connect** details take the
  host and port (the public endpoint is `host:port`) and the password of the `default` user. Use
  `REDIS_SSL` = `false` and `REDIS_DATABASE` = `0`.
- **A Render Key Value you already have:** it must be in the **Singapore** region. Open it > **Connect** >
  **Internal** and take the host from the URL `redis://<host>:6379`. Leave `REDIS_PASSWORD` empty and use `REDIS_PORT` = `6379`,
  `REDIS_SSL` = `false` and `REDIS_DATABASE` = `3`, so TenantHub doesn't mix with your other app's data.

## 3. Create the services (Render)

1. Sign up at https://render.com with your GitHub account.
2. **New** > **Blueprint** > choose `Multi-tenant-Saas-Platform` > **Connect**.
3. Render asks for the secret values. Fill them in:

   | Key | Value |
   | --- | --- |
   | `DB_HOST` | TiDB host |
   | `DB_USERNAME` | TiDB user |
   | `DB_PASSWORD` | TiDB password |
   | `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL`, `REDIS_DATABASE` | from step 2 |
   | `TENANTHUB_BOOTSTRAP_ADMIN_PASSWORD` | the password you want for the platform `admin` user |
   | `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET` | your (regenerated) Razorpay test keys |
   | `RAZORPAY_WEBHOOK_SECRET` | any long random text; you will paste the same text into Razorpay in step 5 |

4. Click **Apply**. The first deploy takes about 10-15 minutes (the server is built from source).

### If Render gives a service a different address

Render uses the service name in the address (`https://tenanthub-api.onrender.com`). If that name is taken, it adds
a suffix. Then, in the Render dashboard:

- **tenanthub-admin** > Environment: set `VITE_BASE_API` to the API's address and `VITE_TENANT_LINK` to the portal's
  address, then **Manual Deploy**.
- **tenanthub-portal** > Environment: set `VITE_BASE_API`, then **Manual Deploy**.
- **tenanthub-api** > Environment: set `STORAGE_LOCAL_DOMAIN` to `<API address>/files/`.

## 4. Keep the server awake (recommended)

On the free plan the server sleeps after 15 minutes without traffic, and waking it can take several minutes.
The free 750 hours a month are enough to keep one service running all month, so:

1. Sign up at https://uptimerobot.com (free).
2. **Add New Monitor** > HTTP(s) > URL `https://tenanthub-api.onrender.com/actuator/health` > interval **5 minutes**.

## 5. Razorpay webhook

1. Razorpay Dashboard > **Settings** > **Webhooks** > **Add New Webhook**.
2. URL: `https://tenanthub-api.onrender.com/razorpay/webhook`
3. Secret: the same text as `RAZORPAY_WEBHOOK_SECRET`.
4. Events: `payment.captured` and `order.paid`.

## 6. Sign in

- Admin: `https://tenanthub-admin.onrender.com`, user `admin`, the password from step 3.
- Create a tenant (System > Tenants), then sign in to `https://tenanthub-portal.onrender.com` with the tenant ID and
  the admin credentials it shows.

## Limits of the free setup

- **Uploaded files** (avatars, logos uploaded through the app) are stored on the server's disk, which Render wipes
  on every restart or deploy. To keep them, use S3-compatible storage (e.g. Cloudflare R2, free 10 GB): set
  `STORAGE_PLATFORM=s3-1`, `S3_ENABLED=true` and the `S3_*` values.
- **Redis** is not persistent: after a restart users sign in again.
- **Login locations** show "Unknown" unless you add a MaxMind GeoLite2 database.
