import {ISearchQuery} from "@/types/base"
import {IPmsDept} from "@/types/pms"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

/** Query by ID */
export const getDeptApi = (id: number) => {
  return httpGet<number, IJsonResult<IPmsDept>>(`/pms/dept/${id}`)
}

/** Paged query of all */
export const queryDeptApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<IPmsDept[]>>("/pms/dept/query", data)
}

/** Query all */
export const listDeptApi = () => {
  return httpGet<any, IJsonResult<IPmsDept[]>>("/pms/dept/list")
}

/** Query tree nodes */
export const treeDeptApi = () => {
  return httpGet<any, IJsonResult<IPmsDept[]>>("/pms/dept/tree")
}

/** Add */
export const saveDeptApi = (data: IPmsDept) => {
  return httpPost<IPmsDept, IJsonResult<IPmsDept>>("/pms/dept", data)
}

/** Edit */
export const updateDeptApi = (data: IPmsDept) => {
  return httpPut<any, IJsonResult<boolean>>("/pms/dept", data)
}

/** Delete */
export const deleteDeptApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/pms/dept/${id}`)
}
