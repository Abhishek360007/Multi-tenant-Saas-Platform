# Deploy TenantHub for free: Render + TiDB Cloud

| Part | Service | Free plan |
| --- | --- | --- |
| API server | Render web service (Docker) | 512 MB RAM, sleeps after 15 min idle |
| Redis | Render Key Value | 25 MB |
| Admin + tenant websites | Render static sites | free |
| Database | TiDB Cloud Serverless (MySQL-compatible) | 5 GB |

Everything is described in [`render.yaml`](../render.yaml), so Render creates all four services at once.

## 1. Create the database (TiDB Cloud)

1. Sign up at https://tidbcloud.com (free, no card needed).
2. **Create Cluster** > **Serverless** > region **AWS Mumbai (ap-south-1)** > Create.
3. Open the cluster > **Connect** > connection type **General**.
4. Click **Generate Password** and copy these three values somewhere safe:
   - **Host** (like `gateway01.ap-south-1.prod.aws.tidbcloud.com`)
   - **User** (like `2xYzAbCd.root`)
   - **Password**

You don't need to create any database: TenantHub creates `tenanthub_master` itself.

## 2. Create the services (Render)

1. Sign up at https://render.com with your GitHub account.
2. **New** > **Blueprint** > choose `Multi-tenant-Saas-Platform` > **Connect**.
3. Render asks for the secret values. Fill them in:

   | Key | Value |
   | --- | --- |
   | `DB_HOST` | TiDB host |
   | `DB_USERNAME` | TiDB user |
   | `DB_PASSWORD` | TiDB password |
   | `TENANTHUB_BOOTSTRAP_ADMIN_PASSWORD` | the password you want for the platform `admin` user |
   | `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET` | your (regenerated) Razorpay test keys |
   | `RAZORPAY_WEBHOOK_SECRET` | any long random text; you will paste the same text into Razorpay in step 4 |

4. Click **Apply**. The first deploy takes about 10-15 minutes (the server is built from source).

### If Render gives a service a different address

Render uses the service name in the address (`https://tenanthub-api.onrender.com`). If that name is taken, it adds
a suffix. Then, in the Render dashboard:

- **tenanthub-admin** > Environment: set `VITE_BASE_API` to the API's address and `VITE_TENANT_LINK` to the portal's
  address, then **Manual Deploy**.
- **tenanthub-portal** > Environment: set `VITE_BASE_API`, then **Manual Deploy**.
- **tenanthub-api** > Environment: set `STORAGE_LOCAL_DOMAIN` to `<API address>/files/`.

## 3. Keep the server awake (recommended)

On the free plan the server sleeps after 15 minutes without traffic, and waking it can take several minutes.
The free 750 hours a month are enough to keep one service running all month, so:

1. Sign up at https://uptimerobot.com (free).
2. **Add New Monitor** > HTTP(s) > URL `https://tenanthub-api.onrender.com/actuator/health` > interval **5 minutes**.

## 4. Razorpay webhook

1. Razorpay Dashboard > **Settings** > **Webhooks** > **Add New Webhook**.
2. URL: `https://tenanthub-api.onrender.com/razorpay/webhook`
3. Secret: the same text as `RAZORPAY_WEBHOOK_SECRET`.
4. Events: `payment.captured` and `order.paid`.

## 5. Sign in

- Admin: `https://tenanthub-admin.onrender.com`, user `admin`, the password from step 2.
- Create a tenant (System > Tenants), then sign in to `https://tenanthub-portal.onrender.com` with the tenant ID and
  the admin credentials it shows.

## Limits of the free setup

- **Uploaded files** (avatars, logos uploaded through the app) are stored on the server's disk, which Render wipes
  on every restart or deploy. To keep them, use S3-compatible storage (e.g. Cloudflare R2, free 10 GB): set
  `STORAGE_PLATFORM=s3-1`, `S3_ENABLED=true` and the `S3_*` values.
- **Redis** is not persistent: after a restart users sign in again.
- **Login locations** show "Unknown" unless you add a MaxMind GeoLite2 database.
