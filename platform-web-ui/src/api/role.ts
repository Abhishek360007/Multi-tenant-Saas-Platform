import {ISearchQuery, IUpdateMenus} from "@/types/base"
import {IPmsRole} from "@/types/pms"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

/** Query by ID */
export const getRoleApi = (id: number) => {
  return httpGet<number, IJsonResult<IPmsRole>>(`/pms/role/${id}`)
}

/** Paged query of all */
export const queryRoleApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<IPmsRole>>("/pms/role/query", data)
}

/** Query all */
export const listRoleApi = () => {
  return httpGet<any, IJsonResult<IPmsRole[]>>("/pms/role/list")
}

/** Add */
export const saveRoleApi = (data: IPmsRole) => {
  return httpPost<IPmsRole, IJsonResult<IPmsRole>>("/pms/role", data)
}

/** Edit */
export const updateRoleApi = (data: IPmsRole) => {
  return httpPut<any, IJsonResult<boolean>>("/pms/role", data)
}

/** Delete */
export const deleteRoleApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/pms/role/${id}`)
}

/** Granted permissions */
export const getRoleMenusApi = (data: number) => {
  return httpGet<ISearchQuery, IJsonResult<IUpdateMenus>>(`/pms/role/menus/${data}`)
}

/** Update granted permissions */
export const updateRoleMenusApi = (data: any) => {
  return httpPost<any, IJsonResult<Boolean>>("/pms/role/menus", data)
}

/** Assigned departments */
export const getRoleDeptsApi = (data: number) => {
  return httpGet<ISearchQuery, IJsonResult<IUpdateMenus>>(`/pms/role/depts/${data}`)
}
