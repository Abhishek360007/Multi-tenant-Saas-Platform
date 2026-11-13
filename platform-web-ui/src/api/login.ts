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

export type OtpChannel = "EMAIL" | "SMS"

export interface IOtpSend {
  channel: OtpChannel
  target: string
  codeKey: string
  codeText: string
  tenantId?: string
}

export interface IOtpLogin {
  channel: OtpChannel
  target: string
  otp: string
  tenantId?: string
}

/** Send a sign-in code by email or SMS */
export const sendOtpApi = (data: IOtpSend) => {
  return httpPost<IOtpSend, IJsonResult<string>>("/auth/otp/send", data)
}

/** Sign in with a one-time code */
export const otpLoginApi = (data: IOtpLogin) => {
  return httpPost<IOtpLogin, IJsonResult<IOAuth2Token>>("/auth/otp/login", data)
}
