import {createRouter, type RouteRecordRaw} from "vue-router"
import {flatMultiLevelRoutes, history} from "./helper"
import routeSettings from "@/config/route"
import {loginUrl} from "@/config/white-list"

const Layouts = () => import("@/layouts/index.vue")

/**
 * Constant routes
 * Except hidden pages such as redirect/403/404/login, pages should have a Name
 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/redirect",
    component: Layouts,
    meta: {
      hidden: true
    },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue")
      }
    ]
  },
  {
    path: "/403",
    component: () => import("@/views/error-page/403.vue"),
    meta: {
      hidden: true
    }
  },
  {
    path: "/404",
    component: () => import("@/views/error-page/404.vue"),
    meta: {
      hidden: true
    },
    alias: "/:pathMatch(.*)*"
  },
  {
    path: "/login",
    component: () => import("@/views/login/index.vue"),
    meta: {
      hidden: true
    }
  },
  {
    path: "/change-password",
    component: () => import("@/views/change-password/index.vue"),
    meta: {
      hidden: true,
      title: "Change password"
    }
  },
  {
    path: "/",
    component: Layouts,
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        component: () => import("@/views/dashboard/index.vue"),
        name: "Dashboard",
        meta: {
          title: "Home",
          svgIcon: "dashboard",
          affix: true
        }
      },
      {
        path: "personal",
        component: () => import("@/views/personal/index.vue"),
        name: "Personal",
        meta: {
          title: "Profile",
          affix: true,
          hidden: true
        }
      }
    ]
  },
  {
    path: "/unocss",
    component: Layouts,
    redirect: "/unocss/index",
    children: [
      {
        path: "index",
        component: () => import("@/views/unocss/index.vue"),
        name: "UnoCSS",
        meta: {
          title: "UnoCSS",
          svgIcon: "unocss"
        }
      }
    ]
  }
]

const router = createRouter({
  history,
  routes: routeSettings.thirdLevelRouteCache ? flatMultiLevelRoutes(constantRoutes) : constantRoutes
})

/** Reset routes */
export function resetRouter() {
  // Note: every dynamic route must have a Name or it may not be fully reset
  try {
    router.getRoutes().forEach((route) => {
      const { name, meta } = route
      if (name && meta.roles?.length) {
        router.hasRoute(name) && router.removeRoute(name)
      }
    })
  } catch {
    // A hard browser refresh also works, but feels worse
    window.location.reload()
  }
}

/** Go to the sign-in page */
export function jumpToLogin() {
  // Get the current page route
  const path = router.currentRoute.value.path
  // Get the current page parameters
  const query = router.currentRoute.value.query
  query.redirect = path
  router.push({ path: loginUrl, query: query })
}

export default router
