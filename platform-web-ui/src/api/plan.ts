import { httpGet, httpPost } from "@/utils/service"

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
  /** 1 = offered, 0 = hidden */
  status: number
}

/** All plans (platform admins also see hidden ones) */
export const listPlansApi = () => httpGet<any, IJsonResult<IPlan[]>>("/sys/plan")

/** Add or update a plan */
export const savePlanApi = (data: IPlan) => httpPost<IPlan, IJsonResult<boolean>>("/sys/plan", data)
