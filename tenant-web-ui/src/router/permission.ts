import router from "@/router"
import {useUserStoreHook} from "@/store/modules/user"
import {useDictStoreHook} from "@/store/modules/dict"
import {usePermissionStoreHook} from "@/store/modules/permission"
import {useNoticeStoreHook} from "@/store/modules/notice"

import {ElMessage} from "element-plus"
import {useTitle} from "@/hooks/useTitle"
import {getToken} from "@/utils/cache/local-storage"
import {fixBlankPage} from "@/utils/fix-blank-page"
import {setRouteChange} from "@/hooks/useRouteListener"
import {isWhiteList, loginUrl} from "@/config/white-list"
import NProgress from "nprogress"
import "nprogress/nprogress.css"
import {useTenantStoreHook} from "@/store/modules/tenant"

const { setTitle } = useTitle()
NProgress.configure({ showSpinner: false })

const CHANGE_PASSWORD_URL = "/change-password"

const userStore = useUserStoreHook()
const dictStore = useDictStoreHook()
const noticeStore = useNoticeStoreHook()
const tenantStore = useTenantStoreHook()
const permissionStore = usePermissionStoreHook()

router.beforeEach(async (to, _from, next) => {
  fixBlankPage()
  NProgress.start()

  const token = getToken()
  // Check whether the user is signed in
  if (!token) {
    // Pages on the sign-in allowlist are opened directly
    if (isWhiteList(to)) {
      next()
    } else {
      // Any other page redirects to sign-in
      NProgress.done()
      next(loginUrl)
    }
    return
  }

  // Already signed in and opening the sign-in page: redirect home
  if (to.path === loginUrl) {
    NProgress.done()
    return next({ path: "/" })
  }

  // Accounts whose password was set by an administrator must change it before anything else
  try {
    if (!userStore.userInfo) await userStore.getUserInfo()
  } catch (err: any) {
    userStore.fedLogout()
    NProgress.done()
    return next(loginUrl)
  }
  if (userStore.userInfo?.passwordChangeRequired === 1) {
    if (to.path === CHANGE_PASSWORD_URL) return next()
    NProgress.done()
    return next(CHANGE_PASSWORD_URL)
  }
  if (to.path === CHANGE_PASSWORD_URL) {
    NProgress.done()
    return next({ path: "/" })
  }

  // If the user already has their permissions
  if (userStore.menus.length !== 0) {
    return next()
  } else {
    // Otherwise fetch the permission menus again
    try {
      // Initialize data
      await initBasicData()
      // Check whether the user has their permission menus
      if (userStore.menus.length === 0) {
        await userStore.getUserPerms()
        // Generate accessible routes from the permission menus (accessible = constant + permitted dynamic routes)
        permissionStore.setRoutes(userStore.menus)
        // Add the 'permitted dynamic routes' to the router
        permissionStore.dynamicRoutes.forEach((route) => router.addRoute(route))
        // Make sure the routes have been added
        // replace: true so the navigation leaves no history entry
        next({ ...to, replace: true })
      } else {
        next()
      }
    } catch (err: any) {
      // On any error, reset the token and redirect to sign-in
      userStore.fedLogout()
      ElMessage.error(err.message || "Route guard error")
      NProgress.done()
      next(loginUrl)
    }
  }
})

router.afterEach((to) => {
  setRouteChange(to)
  setTitle(to.meta.title)
  NProgress.done()
})

// Initialize data such as user details, dictionaries and global config
async function initBasicData() {
  // Get user details
  if (userStore.userInfo === undefined || !userStore.userInfo) {
    await userStore.getUserInfo()
    await tenantStore.getLoginUserTenant()
  }

  // Fetch dictionary data
  if (dictStore.dictMapKeys === undefined || !dictStore.dictMapKeys) {
    await dictStore.getDictMapKeys()
  }

  // Start WebSocket
  if (!noticeStore.getStompClientActive()) {
    noticeStore.startWebSocket()
  }
}
