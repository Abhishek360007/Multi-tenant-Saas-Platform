import { ILoginData, ILoginUserInfo, IOAuth2Token, IUserPerms } from "@/types/pms"
import { httpGet, httpPost } from "@/utils/service"

/** Get public key */
export const getPublicKeyApi = () => {
  return httpGet<string, IJsonResult<string>>("/auth/public/key")
}

/** Sign in */
export const loginApi = (data?: ILoginData) => {
  return httpPost<ILoginData, IJsonResult<IOAuth2Token>>("/auth/login", data)
}

/** Sign out */
export const logoutApi = () => {
  return httpPost<string, IJsonResult<string>>("/auth/logout")
}

/** Get user details */
export const getUserInfoApi = () => {
  return httpGet<string, IJsonResult<ILoginUserInfo>>("/auth/login/user")
}


/** Get user permissions */
export const getUserPermsApi = () => {
  return httpGet<string, IJsonResult<IUserPerms>>("/pms/user/rights")
}
