import {ISearchQuery} from "@/types/base"
import {ILoginLog, IOperLog} from "@/types/lms"
import {httpGet, httpPost} from "@/utils/service"

/** Query by ID */
export const getLoginLogApi = (id: String) => {
  return httpGet<number, IJsonResult<ILoginLog>>(`/lms/lgoin/${id}`)
}

/** Paged query of login logs */
export const queryLoginLogApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ILoginLog>>("/lms/lgoin/query", data)
}

/** Delete login logs */
export const deleteLoginLogApi = (ids: string[]) => {
  return httpPost<string[], IJsonResult<boolean>>("/lms/lgoin", ids)
}

/** Paged query of operation logs */
export const queryOperLogApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<IOperLog>>("/lms/oper/query", data)
}

/** Delete operation logs */
export const deleteOperLogApi = (ids: string[]) => {
  return httpPost<string[], IJsonResult<boolean>>("/lms/oper", ids)
}
