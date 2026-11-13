import {
  createRouter,
  createWebHashHistory,
  createWebHistory,
  type Router,
  type RouteRecordNormalized,
  type RouteRecordRaw
} from "vue-router"
import {cloneDeep, omit} from "lodash-es"

/** Router mode */
export const history =
  import.meta.env.VITE_ROUTER_HISTORY === "hash"
    ? createWebHashHistory(import.meta.env.VITE_PUBLIC_PATH)
    : createWebHistory(import.meta.env.VITE_PUBLIC_PATH)

/** Route flattening (third-level and deeper routes become second-level) */
export const flatMultiLevelRoutes = (routes: RouteRecordRaw[]) => {
  const routesMirror = cloneDeep(routes)
  routesMirror.forEach((route) => {
    // Flatten routes that are three or more levels deep
    isMultipleRoute(route) && promoteRouteLevel(route)
  })
  return routesMirror
}

/** Check whether the route depth is greater than 2 */
const isMultipleRoute = (route: RouteRecordRaw) => {
  const children = route.children
  if (children?.length) {
    // If any child route has children, the route is three or more levels deep
    return children.some((child) => child.children?.length)
  }
  return false
}

/** Generate second-level routes */
const promoteRouteLevel = (route: RouteRecordRaw) => {
  // A router instance is created to get all route records of the given route
  let router: Router | null = createRouter({
    history,
    routes: [route]
  })
  const routes = router.getRoutes()
  // addToChildren uses those route records to update the route children
  addToChildren(routes, route.children || [], route)
  router = null
  // After flattening, remove from all child routes the children
  route.children = route.children?.map((item) => omit(item, "children") as RouteRecordRaw)
}

/** Add the given child routes to the route module */
const addToChildren = (routes: RouteRecordNormalized[], children: RouteRecordRaw[], routeModule: RouteRecordRaw) => {
  children.forEach((child) => {
    const route = routes.find((item) => item.name === child.name)
    if (route) {
      // Initialize the routeModule children
      routeModule.children = routeModule.children || []
      // Add the route to routeModule.children if it is not already there
      if (!routeModule.children.includes(route)) {
        routeModule.children.push(route)
      }
      // If this child route has its own children, recurse to add them too
      if (child.children?.length) {
        addToChildren(routes, child.children, routeModule)
      }
    }
  })
}
