import { ISearchQuery, ISimpleTenant } from "@/types/base"
import { ISysTenant, ICreateTenant, ICreateTenantAdmin } from "@/types/sys"
import { httpDelete, httpGet, httpPost, httpPut } from "@/utils/service"

// Simple query
export const simpleQueryTenantApi = (params: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ISimpleTenant>>("/sys/tenant/simple/query", params)
}

/** Query by ID */
export const getTenantApi = (id: number) => {
  return httpGet<number, IJsonResult<ISysTenant>>(`/sys/tenant/${id}`)
}

/** Paged query of all */
export const queryTenantApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ISysTenant>>("/sys/tenant/query", data)
}

/** Query all */
export const listTenantApi = () => {
  return httpGet<any, IJsonResult<ISysTenant[]>>("/sys/tenant/list")
}

/** Add */
export const saveTenantApi = (data: ICreateTenant) => {
  return httpPost<ICreateTenant, IJsonResult<ICreateTenantAdmin>>("/sys/tenant", data)
}

/** Edit */
export const updateTenantApi = (data: ISysTenant) => {
  return httpPut<any, IJsonResult<boolean>>("/sys/tenant", data)
}

/** Delete */
export const deleteTenantApi = (id: string) => {
  return httpDelete<string, IJsonResult<boolean>>(`/sys/tenant/${id}`)
}

/** Reset tenant data */
export const resetDataTenantApi = (id: string) => {
  return httpPost<string, IJsonResult<ICreateTenantAdmin>>(`/sys/tenant/reset/data/${id}`)
}
