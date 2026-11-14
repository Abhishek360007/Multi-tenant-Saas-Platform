<script lang="ts" setup>
import { computed, onMounted, ref } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import {
  billingQuoteApi,
  billingSummaryApi,
  checkoutApi,
  listPlansApi,
  paymentsApi,
  verifyPaymentApi,
  type BillingCycle,
  type IBillingSummary,
  type IPayment,
  type IPlan,
  type IQuote
} from "@/api/billing"
import { payWithRazorpay } from "@/utils/razorpay"
import { formatPaise } from "@/utils/utc-time"

defineOptions({ name: "BillingSubscription" })

const loading = ref(false)
const summary = ref<IBillingSummary>()
const plans = ref<IPlan[]>([])
const payments = ref<IPayment[]>([])
const paying = ref(false)
const cycle = ref<BillingCycle>("MONTHLY")

const quoteVisible = ref(false)
const quoteLoading = ref(false)
const quote = ref<IQuote>()

const load = async () => {
  loading.value = true
  try {
    const [s, p, h] = await Promise.all([billingSummaryApi(), listPlansApi(), paymentsApi()])
    summary.value = s.data
    plans.value = p.data
    payments.value = h.data
  } finally {
    loading.value = false
  }
}
onMounted(load)

const limit = (n: number) => (n < 0 ? "Unlimited" : String(n))
/** "1 project", "5 projects", "Unlimited projects" */
const limitOf = (n: number, noun: string) => (n < 0 ? `Unlimited ${noun}s` : `${n} ${noun}${n === 1 ? "" : "s"}`)
const usagePercent = (used: number, max: number) => (max <= 0 ? 0 : Math.min(100, Math.round((used / max) * 100)))
const priceOf = (plan: IPlan) => (cycle.value === "YEARLY" ? plan.yearlyPricePaise : plan.monthlyPricePaise)
const isCurrent = (plan: IPlan) => summary.value?.plan.planCode === plan.planCode

/** Plan name for a plan code (falls back to the code for plans no longer offered) */
const planName = (code: string) => plans.value.find((p) => p.planCode === code)?.name ?? code
const pad2 = (n?: number) => String(n ?? "").padStart(2, "0")

/** Yearly price compared with 12 monthly payments */
const yearlySaving = computed(() => {
  const paid = plans.value.find((p) => p.monthlyPricePaise > 0)
  if (!paid) return ""
  const months = paid.yearlyPricePaise / paid.monthlyPricePaise
  return months < 12 ? `${Math.round(12 - months)} months free` : ""
})

const openQuote = async (plan: IPlan) => {
  quoteVisible.value = true
  quoteLoading.value = true
  quote.value = undefined
  try {
    quote.value = (await billingQuoteApi(plan.planCode, cycle.value)).data
  } catch {
    quoteVisible.value = false
  } finally {
    quoteLoading.value = false
  }
}

/** Razorpay Checkout, then server-side signature verification activates the plan */
const pay = async () => {
  if (!quote.value) return
  paying.value = true
  try {
    const order = (await checkoutApi(quote.value.planCode, quote.value.cycle)).data
    quoteVisible.value = false
    const result = await payWithRazorpay(order)
    const payment = (await verifyPaymentApi(result)).data
    await load()
    ElMessageBox.alert(
      `Payment received. Your ${summary.value?.plan.name} plan is active until ${payment.periodEnd}. Invoice ${payment.invoiceNo}.`,
      "Thank you!",
      { type: "success", confirmButtonText: "OK" }
    )
  } catch (e: any) {
    if (e?.message && e.message !== "Payment cancelled") ElMessage.error(e.message)
  } finally {
    paying.value = false
  }
}

const statusType = (s: string) => (s === "paid" ? "success" : s === "failed" ? "danger" : "info")
</script>

<template>
  <div class="app-container" v-loading="loading">
    <el-card v-if="summary" shadow="never" class="summary">
      <template #header>
        <div class="summary-header">
          <span>Current plan</span>
          <el-tag size="large" type="success">{{ summary.plan.name }}</el-tag>
        </div>
      </template>
      <el-row :gutter="24">
        <el-col :xs="24" :sm="8">
          <div class="metric-label">Users</div>
          <div class="metric">{{ summary.users }} / {{ limit(summary.plan.maxUsers) }}</div>
          <el-progress
            v-if="summary.plan.maxUsers > 0"
            :percentage="usagePercent(summary.users, summary.plan.maxUsers)"
            :show-text="false"
          />
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="metric-label">Projects</div>
          <div class="metric">{{ summary.projects }} / {{ limit(summary.plan.maxProjects) }}</div>
          <el-progress
            v-if="summary.plan.maxProjects > 0"
            :percentage="usagePercent(summary.projects, summary.plan.maxProjects)"
            :show-text="false"
          />
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="metric-label">{{ summary.planExpires ? "Plan renews / ends" : "Account valid until" }}</div>
          <div class="metric">{{ summary.planExpires || summary.expires }}</div>
        </el-col>
      </el-row>
    </el-card>

    <div class="plans-header">
      <h3>Plans</h3>
      <el-radio-group v-model="cycle">
        <el-radio-button value="MONTHLY">Monthly</el-radio-button>
        <el-radio-button value="YEARLY">
          Yearly <span v-if="yearlySaving" class="saving">· {{ yearlySaving }}</span>
        </el-radio-button>
      </el-radio-group>
    </div>

    <el-row :gutter="16">
      <el-col v-for="plan in plans" :key="plan.planCode" :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="plan" :class="{ current: isCurrent(plan) }">
          <div class="plan-name">{{ plan.name }}</div>
          <div class="plan-desc">{{ plan.description }}</div>
          <div class="price">
            {{ formatPaise(priceOf(plan)) }}
            <span class="per">/ {{ cycle === "YEARLY" ? "year" : "month" }}</span>
          </div>
          <div class="gst-note">{{ priceOf(plan) > 0 ? "+ 18% GST" : "No charge" }}</div>
          <ul class="limits">
            <li>{{ limitOf(plan.maxUsers, "user") }}</li>
            <li>{{ limitOf(plan.maxProjects, "project") }}</li>
          </ul>
          <el-button v-if="isCurrent(plan)" disabled class="cta">Current plan</el-button>
          <el-button v-else-if="priceOf(plan) > 0" type="primary" class="cta" @click="openQuote(plan)">
            Choose {{ plan.name }}
          </el-button>
          <el-button v-else disabled class="cta">Free</el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-card v-if="payments.length" shadow="never" class="history">
      <template #header>Payment history</template>
      <el-table :data="payments">
        <el-table-column prop="invoiceNo" label="Invoice" min-width="160">
          <template #default="{ row }">{{ row.invoiceNo || "-" }}</template>
        </el-table-column>
        <el-table-column label="Plan" min-width="120">
          <template #default="{ row }">{{ planName(row.planCode) }} · {{ row.billingCycle === "YEARLY" ? "Yearly" : "Monthly" }}</template>
        </el-table-column>
        <el-table-column label="Amount (incl. GST)" min-width="140" align="right">
          <template #default="{ row }">{{ formatPaise(row.totalPaise) }}</template>
        </el-table-column>
        <el-table-column label="Status" width="100" align="center">
          <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="paidAt" label="Paid on" min-width="170" />
        <el-table-column label="Period" min-width="220">
          <template #default="{ row }">{{ row.periodStart ? `${row.periodStart.slice(0, 10)} to ${row.periodEnd.slice(0, 10)}` : "-" }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="quoteVisible" title="Order summary" width="420px" append-to-body>
      <div v-loading="quoteLoading">
        <template v-if="quote">
          <div class="quote-plan">{{ quote.planName }} · {{ quote.cycle === "YEARLY" ? "Yearly" : "Monthly" }}</div>
          <table class="quote">
            <tr>
              <td>Plan price</td>
              <td>{{ formatPaise(quote.amounts.basePaise) }}</td>
            </tr>
            <template v-if="quote.amounts.intraState">
              <tr>
                <td>CGST ({{ quote.amounts.ratePercent / 2 }}%)</td>
                <td>{{ formatPaise(quote.amounts.cgstPaise) }}</td>
              </tr>
              <tr>
                <td>SGST ({{ quote.amounts.ratePercent / 2 }}%)</td>
                <td>{{ formatPaise(quote.amounts.sgstPaise) }}</td>
              </tr>
            </template>
            <tr v-else>
              <td>IGST ({{ quote.amounts.ratePercent }}%)</td>
              <td>{{ formatPaise(quote.amounts.igstPaise) }}</td>
            </tr>
            <tr class="total">
              <td>Total</td>
              <td>{{ formatPaise(quote.amounts.totalPaise) }}</td>
            </tr>
          </table>
          <p class="supply-note">
            Place of supply: state code {{ pad2(summary?.buyerStateCode) }}
            ({{ quote.amounts.intraState ? "same state as seller: CGST + SGST" : "other state: IGST" }})
          </p>
        </template>
      </div>
      <template #footer>
        <el-button @click="quoteVisible = false">Cancel</el-button>
        <el-button type="primary" :disabled="!quote" :loading="paying" @click="pay">
          Pay {{ quote ? formatPaise(quote.amounts.totalPaise) : "" }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.summary {
  margin-bottom: 20px;
  .summary-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .metric-label {
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }
  .metric {
    font-size: 22px;
    font-weight: 600;
    margin: 4px 0 8px;
  }
}
.plans-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
  h3 {
    margin: 0;
  }
  .saving {
    color: var(--el-color-success);
  }
}
.plan {
  margin-bottom: 16px;
  &.current {
    border-color: var(--el-color-success);
  }
  .plan-name {
    font-size: 18px;
    font-weight: 600;
  }
  .plan-desc {
    color: var(--el-text-color-secondary);
    min-height: 40px;
    margin: 4px 0 12px;
  }
  .price {
    font-size: 28px;
    font-weight: 700;
    .per {
      font-size: 14px;
      font-weight: 400;
      color: var(--el-text-color-secondary);
    }
  }
  .gst-note {
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }
  .limits {
    padding-left: 18px;
    margin: 12px 0 16px;
    line-height: 1.8;
  }
  .cta {
    width: 100%;
  }
}
.history {
  margin-top: 4px;
}
.quote-plan {
  font-weight: 600;
  margin-bottom: 12px;
}
.quote {
  width: 100%;
  border-collapse: collapse;
  td {
    padding: 6px 0;
  }
  td:last-child {
    text-align: right;
    font-variant-numeric: tabular-nums;
  }
  .total td {
    border-top: 1px solid var(--el-border-color);
    font-weight: 700;
    padding-top: 10px;
  }
}
.supply-note {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  margin-top: 12px;
}
</style>
