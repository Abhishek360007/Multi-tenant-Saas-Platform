import { httpGet, httpPost } from "@/utils/service"

export type BillingCycle = "MONTHLY" | "YEARLY"

export interface IPlan {
  planCode: string
  name: string
  description: string
  /** Price before GST, in paise */
  monthlyPricePaise: number
  yearlyPricePaise: number
  /** -1 = unlimited */
  maxUsers: number
  maxProjects: number
  sort: number
  status: number
}

export interface IBillingSummary {
  tenantId: string
  tenantName: string
  plan: IPlan
  users: number
  projects: number
  expires: string
  /** End of the paid plan period (empty for Free) */
  planExpires?: string
  buyerStateCode: number
  sellerStateCode: number
}

export interface IGstAmounts {
  basePaise: number
  ratePercent: number
  intraState: boolean
  cgstPaise: number
  sgstPaise: number
  igstPaise: number
  totalPaise: number
}

export interface IQuote {
  planCode: string
  planName: string
  cycle: BillingCycle
  amounts: IGstAmounts
}

/** Offered subscription plans */
export const listPlansApi = () => httpGet<any, IJsonResult<IPlan[]>>("/sys/plan")

/** Current plan, usage and subscription end date */
export const billingSummaryApi = () => httpGet<any, IJsonResult<IBillingSummary>>("/billing/summary")

/** Price of a plan with GST for this tenant */
export const billingQuoteApi = (plan: string, cycle: BillingCycle) =>
  httpGet<any, IJsonResult<IQuote>>("/billing/quote", { plan, cycle })

export interface ICheckout {
  keyId: string
  orderId: string
  /** In paise */
  amount: number
  currency: string
  name: string
  description: string
  prefillName: string
  prefillEmail: string
  prefillContact: string
}

export interface IPayment {
  id: string
  planCode: string
  billingCycle: BillingCycle
  basePaise: number
  cgstPaise: number
  sgstPaise: number
  igstPaise: number
  totalPaise: number
  razorpayOrderId: string
  razorpayPaymentId: string
  status: "created" | "paid" | "failed"
  invoiceNo: string
  periodStart?: string
  periodEnd?: string
  createdAt: string
  paidAt?: string
}

export interface IRazorpaySuccess {
  razorpay_order_id: string
  razorpay_payment_id: string
  razorpay_signature: string
}

/** Create a Razorpay order for a plan */
export const checkoutApi = (plan: string, cycle: BillingCycle) =>
  httpPost<any, IJsonResult<ICheckout>>("/billing/checkout", { plan, cycle })

/** Verify a completed Razorpay payment and activate the plan */
export const verifyPaymentApi = (data: IRazorpaySuccess) =>
  httpPost<IRazorpaySuccess, IJsonResult<IPayment>>("/billing/verify", data)

/** Payment history */
export const paymentsApi = () => httpGet<any, IJsonResult<IPayment[]>>("/billing/payments")
