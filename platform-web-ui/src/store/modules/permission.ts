import { ref } from "vue"
import store from "@/store"
import { defineStore } from "pinia"
import { type RouteRecordRaw } from "vue-router"
import { constantRoutes } from "@/router"
import { flatMultiLevelRoutes } from "@/router/helper"
import routeSettings from "@/config/route"
import { IPermsMenus } from "@/types/pms"
import { isExternal } from "@/utils/validate"

const Layout = () => import("@/layouts/index.vue")

// Convert the route strings from the backend into components
function flatTreeRoutes(arr: IPermsMenus[]): RouteRecordRaw[] {
  const result: RouteRecordRaw[] = []
  const deep = (arr1: IPermsMenus[], arr2: RouteRecordRaw[]) => {
    arr1.forEach((source: IPermsMenus) => {
      const { title, icon, hidden, keepAlive } = source.meta
      const target: any = {
        path: source.path,
        name: source.name,
        meta: {
          title: title,
          svgIcon: icon,
          hidden: hidden,
          keepAlive: keepAlive
        }
      }
      // Check whether the path is an external link.
      if (isExternal(source.path)) {
        target.component = () => {}
      } else {
        if (source.component === "Layout") {
          target.component = Layout
        } else {
          target.component = loadView(source.component)
        }
      }
      arr2.push(target)
      if (source.children && source.children.length > 0) {
        target.children = []
        target.redirect = source.children[0].path
        deep(source.children, target.children)
      }
    })
  }
  deep(arr, result)
  return result
}

// Match every .vue file under views
const modules = import.meta.glob("../../views/**/**.vue")

export const loadView = (view: string) => {
  let res
  for (const path in modules) {
    const dir = path.split("views/")[1].split(".vue")[0]
    if (dir === view) {
      res = () => modules[path]()
    }
  }
  return res
}

export const usePermissionStore = defineStore("permission", () => {
  const routes = ref<RouteRecordRaw[]>([])
  const dynamicRoutes = ref<RouteRecordRaw[]>([])

  const setRoutes = (data: IPermsMenus[]) => {
    const asyncRoutes = flatTreeRoutes(data)
    routes.value = constantRoutes.concat(asyncRoutes)
    dynamicRoutes.value = routeSettings.thirdLevelRouteCache ? flatMultiLevelRoutes(asyncRoutes) : asyncRoutes
  }

  return { routes, dynamicRoutes, setRoutes }
})

/** Use outside setup */
export function usePermissionStoreHook() {
  return usePermissionStore(store)
}
