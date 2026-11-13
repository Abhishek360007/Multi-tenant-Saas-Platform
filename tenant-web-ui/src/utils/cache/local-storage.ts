/** Handled centrally localStorage */

import CacheKey from "@/constants/cache-key"
import {type SidebarClosed, type SidebarOpened} from "@/constants/app-key"
import {type ThemeName} from "@/hooks/useTheme"
import {type TagView} from "@/store/modules/tags-view"
import {type LayoutSettings} from "@/config/layouts"
import {ILoginData} from "@/types/pms"
import {decode, encode} from "js-base64"
import {ISimpleProject} from "@/types/base"
import {defaultProject, hashCode} from ".."
import {isJSON} from "../validate"

//#region token
export const getToken = () => {
  return localStorage.getItem(CacheKey.TOKEN)
}

export const setToken = (token: string) => {
  localStorage.setItem(CacheKey.TOKEN, token)
}

export const removeToken = () => {
  localStorage.removeItem(CacheKey.TOKEN)
}
//#endregion

//#region Layout settings
export const getConfigLayout = () => {
  const json = localStorage.getItem(CacheKey.CONFIG_LAYOUT)
  return json ? (JSON.parse(json) as LayoutSettings) : null
}

export const setConfigLayout = (settings: LayoutSettings) => {
  localStorage.setItem(CacheKey.CONFIG_LAYOUT, JSON.stringify(settings))
}

export const removeConfigLayout = () => {
  localStorage.removeItem(CacheKey.CONFIG_LAYOUT)
}
//#endregion

//#region Sidebar state
export const getSidebarStatus = () => {
  return localStorage.getItem(CacheKey.SIDEBAR_STATUS)
}

export const setSidebarStatus = (sidebarStatus: SidebarOpened | SidebarClosed) => {
  localStorage.setItem(CacheKey.SIDEBAR_STATUS, sidebarStatus)
}
//#endregion

//#region Active theme name
export const getActiveThemeName = () => {
  return localStorage.getItem(CacheKey.ACTIVE_THEME_NAME) as ThemeName | null
}

export const setActiveThemeName = (themeName: ThemeName) => {
  localStorage.setItem(CacheKey.ACTIVE_THEME_NAME, themeName)
}
//#endregion

//#region Tab bar

export const getVisitedViews = () => {
  const json = localStorage.getItem(CacheKey.VISITED_VIEWS)
  return JSON.parse(json ?? "[]") as TagView[]
}

export const setVisitedViews = (views: TagView[]) => {
  views.forEach((view) => {
    // Remove unneeded properties so JSON.stringify does not hit circular references
    delete view.matched
    delete view.redirectedFrom
  })
  localStorage.setItem(CacheKey.VISITED_VIEWS, JSON.stringify(views))
}

export const getCachedViews = () => {
  const json = localStorage.getItem(CacheKey.CACHED_VIEWS)
  return JSON.parse(json ?? "[]") as string[]
}

export const setCachedViews = (views: string[]) => {
  localStorage.setItem(CacheKey.CACHED_VIEWS, JSON.stringify(views))
}

//#region Component size
/// Component size
export const getControlSize = () => {
  return localStorage.getItem(CacheKey.CONTROL_SIZE) as string
}

/// Component size
export const setControlSize = (size: string) => {
  localStorage.setItem(CacheKey.CONTROL_SIZE, size)
}
//#endregion

//#region Get the saved username and password
export const getUserAndPassword = () => {
  const base64Str = localStorage.getItem(CacheKey.USER_AND_PASSWORD)
  if (base64Str) {
    // First base64 decode
    const desVal1 = decode(base64Str)
    if (desVal1) {
      // Generated from the userAgent hashCode
      const hc = hashCode(navigator.userAgent)
      // hashCode . Base64 encoded
      const salt = encode(hc.toString())
      // Remove the salt
      const desVal2Str = desVal1.replaceAll(`${salt}=.`, "")
      const desVal3Json = decode(desVal2Str)
      if (isJSON(desVal3Json)) {
        const data = JSON.parse(desVal3Json) as ILoginData
        let saltPassword = decode(data.password)
        saltPassword = saltPassword.replaceAll(`${hc}`, "")
        const password = saltPassword.replaceAll(`${salt}`, "")
        data.password = password
        return data
      }
    }
    removeUserAndPassword()
  }
  return null
}

// Save the username and password
export function setUserAndPassword(data: ILoginData) {
  // Generated from the userAgent hashCode
  const hc = hashCode(navigator.userAgent)
  // hashCode . Base64 encoded
  const salt = encode(hc.toString())
  // Salt the password, then encode it
  data.password = encode(`${hc}${data.password}${salt}`)
  // Convert username and password to JSON. Base64 encode
  const encodeVal = encode(JSON.stringify(data))
  // Then append the salt
  const newVal = `${salt}=.${encodeVal}`
  // Base64 encode again
  const newVal2 = encode(newVal)
  return localStorage.setItem(CacheKey.USER_AND_PASSWORD, newVal2)
}

// Delete the saved username and password
export function removeUserAndPassword() {
  return localStorage.removeItem(CacheKey.USER_AND_PASSWORD)
}

//#endregion

//#region Project selection

// Get the current project
export const getCurrentProject = () => {
  let jsonStr = localStorage.getItem(CacheKey.PROJECT_ID)
  if (!jsonStr || !isJSON(jsonStr)) {
    setCurrentProject(defaultProject)
    jsonStr = localStorage.getItem(CacheKey.PROJECT_ID)
  }
  return JSON.parse(jsonStr!) as ISimpleProject
}

// Set the current project
export const setCurrentProject = (project: ISimpleProject) => {
  const jsonStr = JSON.stringify(project)
  localStorage.setItem(CacheKey.PROJECT_ID, jsonStr)
}

//#endregion

// Get the tenant used at the last sign-in Id
export const getLastTenantId = () => {
  return localStorage.getItem(CacheKey.LAST_TENANT_ID)
}

// Save the tenant used at this sign-in Id
export const setLastTenantId = (tenantId: string) => {
  localStorage.setItem(CacheKey.LAST_TENANT_ID, tenantId)
}

//#endregion
