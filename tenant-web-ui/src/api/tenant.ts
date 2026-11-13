import {ISearchQuery, ISimpleTenant} from "@/types/base"
import {ICreateTenant, ICreateTenantAdmin, ISysTenant} from "@/types/sys"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

// Simple query
export const simpleQueryTenantApi = (params: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ISimpleTenant>>("/sys/tenant/simple/query", params)
}

/** Query by ID */
export const getTenantApi = (id: string) => {
  return httpGet<string, IJsonResult<ISysTenant>>(`/sys/tenant/${id}`)
}

/** Check existence by ID */
export const existTenantApi = (id: string) => {
  return httpGet<string, IJsonResult<boolean>>(`/sys/tenant/exist/${id}`)
}

/** Simple get by ID */
export const getSimpleTenantApi = (id: string) => {
  const params = { id: id }
  return httpGet<any, IJsonResult<ISimpleTenant>>("/sys/tenant/simple", params)
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
