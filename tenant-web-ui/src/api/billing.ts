import { httpGet } from "@/utils/service"

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
