import {ISearchQuery} from "@/types/base"
import {IPmsUser, IUserListTenant} from "@/types/pms"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

/** Change password */
export const updatePasswordApi = (data: any) => {
  return httpPost<any, IJsonResult<boolean>>("/pms/user/update/password", data)
}

/** Reset password */
export const resetPasswordApi = (data: any) => {
  return httpPost<any, IJsonResult<boolean>>("/pms/user/reset/password", data)
}

/** Query users */
export const queryUserApi = (params: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<IPmsUser>>("/pms/user/query", params)
}

/** Query all */
export const listUserApi = () => {
  return httpGet<any, IJsonResult<IPmsUser[]>>("/pms/user/list")
}

/** Add */
export const saveUserApi = (data: IPmsUser) => {
  return httpPost<IPmsUser, IJsonResult<IPmsUser>>("/pms/user", data)
}

/** Update user details */
export const updateUserApi = (data: any) => {
  return httpPut<any, IJsonResult<boolean>>("/pms/user", data)
}

/** Update the signed-in user details */
export const updateUserInfoApi = (data: any) => {
  return httpPut<any, IJsonResult<boolean>>("/pms/user/info", data)
}

/** Delete */
export const deleteUserApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/pms/user/${id}`)
}

/** Query tenants linked to a platform user Id */
export const listSysUserTenantIdApi = (userId: number) => {
  return httpGet<any, IJsonResult<IUserListTenant>>(`/sys/user/list/tenant?userId=${userId}`)
}

/** Tenants linked to a platform user Id */
export const updateSysUserTenantIdApi = (data: any) => {
  return httpPost<any, IJsonResult<string>>("/sys/user/update/tenant", data)
}
