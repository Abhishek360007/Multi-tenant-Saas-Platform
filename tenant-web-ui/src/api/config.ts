import {ISearchQuery} from "@/types/base"
import {ISysConfig} from "@/types/sys"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

/** Query by ID */
export const getConfigApi = (id: number) => {
  return httpGet<number, IJsonResult<ISysConfig>>(`/sys/config/${id}`)
}

/** Paged query of all */
export const queryConfigApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ISysConfig>>("/sys/config/query", data)
}

/** Query all */
export const listConfigApi = () => {
  return httpGet<any, IJsonResult<ISysConfig[]>>("/sys/config/list")
}

/** Add */
export const saveConfigApi = (data: ISysConfig) => {
  return httpPost<ISysConfig, IJsonResult<ISysConfig>>("/sys/config", data)
}

/** Edit */
export const updateConfigApi = (data: ISysConfig) => {
  return httpPut<any, IJsonResult<boolean>>("/sys/config", data)
}

/** Delete */
export const deleteConfigApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/sys/config/${id}`)
}
