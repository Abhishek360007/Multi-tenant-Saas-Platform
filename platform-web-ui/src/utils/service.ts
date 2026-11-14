import axios, { AxiosResponse, type AxiosInstance, type AxiosRequestConfig } from "axios"
import { requestTimesToUtc, responseTimesToLocal } from "./utc-time"
import { useUserStoreHook } from "@/store/modules/user"
import { ElMessage, ElMessageBox } from "element-plus"
import { get, merge } from "lodash-es"
import { getToken } from "./cache/local-storage"
import { useTenantStoreHook } from "@/store/modules/tenant"
import { useProjectStoreHook } from "@/store/modules/project"
import { getEnvBaseURLPrefix } from "."
import { ISimpleProject, ISimpleTenant } from "@/types/base"
import { defaultTenant } from "@/utils"
import { v4 as uuidv4 } from "uuid"
import qs from "qs"

/** Create the request instance */
function createService() {
  // Create an axios instance named service
  const service = axios.create()
  // Request interceptor
  service.interceptors.request.use(
    (config) => {
      // The API works in UTC; send local date-times as UTC
      if (config.data) config.data = requestTimesToUtc(config.data)
      if (config.params) config.params = requestTimesToUtc(config.params)
      return config
    },
    // Request failed
    (error) => Promise.reject(error)
  )
  // Response interceptor (adjust to your business needs)
  service.interceptors.response.use(
    (response) => {
      // apiData is the data returned by the API
      const apiData = response.data
      // Return binary data directly
      const responseType = response.request?.responseType
      if (responseType === "blob" || responseType === "arraybuffer") return response
      // The API returns UTC date-times; show them in the browser's time zone
      if (apiData && typeof apiData === "object") apiData.data = responseTimesToLocal(apiData.data)
      // This code is the business code agreed with the backend code
      const code = apiData.status
      // No code means the response did not come from this backend api
      if (code === undefined) {
        ElMessage.error("Not an endpoint of this system")
        return Promise.reject(new Error("Not an endpoint of this system"))
      }
      const tenantStore = useTenantStoreHook()
      const projectStore = useProjectStoreHook()
      switch (code) {
        case 200:
          // code === 200 means no error
          return apiData
        case 400020:
          // Password change pending: the router guard sends the user to /change-password
          return Promise.reject(new Error(apiData.message))
        case 400004:
        case 400010:
        case 400011:
        case 400012:
          logout("Session expired", apiData.message)
          return Promise.reject(new Error(apiData.message))
        case 400444:
        case 400445:
          // If the tenant ID does not exist, reset to the default tenant
          ElMessage.error(apiData.message)
          // The tenant does not exist or was deleted
          tenantStore.resetCurrentTenant()
          return resendRequest(service, response)
        case 400446:
          // This platform user has no permission for the tenant
          if (apiData.data) {
            ElMessage.error(`You do not have permission for tenant ${tenantStore.getCurrentTenant().name}`)
            tenantStore.setCurrentTenant(apiData.data as ISimpleTenant)
            return resendRequest(service, response)
          } else {
            logout("You do not have permission for any tenant", "Access denied")
            return Promise.reject(new Error("You do not have permission for any tenant"))
          }
        case 400447:
          // This user has no permission for the project
          if (apiData.data) {
            ElMessage.error(`You do not have permission for project ${projectStore.getCurrentProject().projectName}`)
            projectStore.setCurrentProject(apiData.data as ISimpleProject)
            return resendRequest(service, response)
          } else {
            logout("You do not have permission for any project", "Access denied")
            return Promise.reject(new Error("You do not have permission for any project"))
          }
        default:
          // is not a valid Code
          ElMessage.error(apiData.message || "Error")
          return Promise.reject(new Error(apiData.message || "Error"))
      }
    },
    (error) => {
      // status is the HTTP status code
      const status = get(error, "response.status")
      if (!error.response && !axios.isCancel(error)) {
        // No answer at all: the server is down, unreachable, or (on free hosting) still waking up
        error.message =
          error.code === "ECONNABORTED"
            ? "The server is taking too long to answer. If it was idle, it may be waking up: try again in a minute."
            : "Can't reach the server. If it was idle, it may be waking up: try again in a minute."
      }
      switch (status) {
        case 400:
          error.message = "Bad request"
          break
        case 401:
          // Token when expired
          logout("Your session has expired. Please sign in again.", "Sign in again")
          break
        case 403:
          error.message = "Access denied"
          break
        case 404:
          error.message = "Request URL error"
          break
        case 408:
          error.message = "Request timed out"
          break
        case 500:
          error.message = "Internal server error"
          break
        case 501:
          error.message = "Not implemented"
          break
        case 502:
          error.message = "Bad gateway"
          break
        case 503:
          error.message = "Service unavailable"
          break
        case 504:
          error.message = "Gateway timeout"
          break
        case 505:
          error.message = "HTTP version not supported"
          break
        default:
          break
      }
      ElMessage.error(error.message)
      return Promise.reject(error)
    }
  )
  return service
}

// Resend the request
function resendRequest(service: AxiosInstance, response: AxiosResponse) {
  const config = response.config
  const tenantId = useTenantStoreHook().getCurrentTenantId()
  const projectId = useProjectStoreHook().getCurrentProjectId()
  config.headers["x-tenant-id"] = tenantId
  config.headers["x-project-id"] = projectId
  return service(config)
}

// Sign out
function logout(title: string, message: string) {
  ElMessageBox.confirm(message || "Your session has expired. Please sign in again.", title, {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      useUserStoreHook().fedLogout()
      // A hard browser refresh also works, but feels worse
      location.reload()
    })
    .catch(() => console.log("Sign-out cancelled..."))
}

/** Create the request method */
function createRequest(service: AxiosInstance) {
  return function <T>(config: AxiosRequestConfig): Promise<T> {
    const tokenValue = getToken()
    const projectId = useProjectStoreHook().getCurrentProjectId()
    // The login endpoint uses the default tenant ID
    // Sign-in requests (password and OTP) always target the platform tenant
    const isSignIn = config.url === "/auth/login" || (config.url ?? "").startsWith("/auth/otp/")
    const tenantId = isSignIn ? defaultTenant.tenantId : useTenantStoreHook().getCurrentTenantId()
    const defaultConfig: AxiosRequestConfig = {
      headers: {
        // Include Token
        Authorization: tokenValue ? tokenValue : "",
        "x-tenant-id": tenantId,
        "x-project-id": projectId,
        "Content-Type": "application/json"
      },
      // Request timeout: 30 seconds (a sleeping free-tier server can be slow to answer)
      timeout: 30000,
      baseURL: getEnvBaseURLPrefix()
    }
    // Merge the default config defaultConfig with the custom config into mergeConfig
    const mergeConfig = merge(defaultConfig, config)
    return service(mergeConfig)
  }
}

/** Instance used for network requests */
const axiosService = createService()

/** Method used for network requests */
const axiosRequest = createRequest(axiosService)

/** Generic request helper */
function httpRequest<T>(config: AxiosRequestConfig): Promise<T> {
  // Custom request/response callbacks handled separately
  return new Promise((resolve, reject) =>
    axiosRequest(config)
      .then((response) => {
        resolve(response as T)
      })
      .catch((error) => {
        reject(error)
      })
  )
}

/** Standalone GET helper */
function httpGet<T, P>(url: string, params?: T): Promise<P> {
  return httpRequest<P>({ method: "get", url, params, paramsSerializer: p => qs.stringify(p, { indices: false }) })
}

/** Standalone POST helper */
function httpPost<T, P>(url: string, data?: T): Promise<P> {
  return httpRequest<P>({ method: "post", url, data })
}

/** Standalone PUT helper */
function httpPut<T, P>(url: string, data?: T): Promise<P> {
  return httpRequest<P>({ method: "put", url, data })
}

/** Standalone DELETE helper */
function httpDelete<T, P>(url: string, params?: T): Promise<P> {
  return httpRequest<P>({ method: "delete", url, params, paramsSerializer: p => qs.stringify(p, { indices: false }) })
}

/** Standalone file download helper */
async function downloadFile(url: string, fileName?: string): Promise<void> {
  const { data, headers } = await axios.get(url, { responseType: "blob" })
  if (!fileName) {
    const fn1 = headers["content-disposition"]
    if (fn1) {
      fileName = fn1.replace(/\w+;filename=(.*)/, "$1")
    } else {
      fileName = uuidv4().replace(/-/g, "")
    }
  }
  const urlObject = window.URL || window.webkitURL
  const downloadUrl = urlObject.createObjectURL(data)
  const link = document.createElement("a")
  link.href = downloadUrl
  link.download = fileName!
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  urlObject.revokeObjectURL(downloadUrl)
}

/** Standalone file download helper */
function getFileBlob(url: string): Promise<Blob> {
  // Custom request/response callbacks handled separately
  return new Promise((resolve, reject) => {
    axios
      .get(url, { responseType: "blob" })
      .then((resp) => {
        const { data, headers } = resp
        const blob = new Blob([data], { type: headers["content-type"] })
        resolve(blob)
      })
      .catch((error) => reject(error))
  })
}

export { httpRequest, httpGet, httpPost, httpPut, httpDelete, axiosRequest, downloadFile, getFileBlob }
