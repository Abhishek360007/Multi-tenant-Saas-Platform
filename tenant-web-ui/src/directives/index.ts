import {type App, type Directive} from "vue"
import {useUserStoreHook} from "@/store/modules/user"

/** Register custom directives */
export function loadDirectives(app: App) {
  // example: v-has='pre_dept:view'
  app.directive("has", permission)
  // example: v-admin
  app.directive("admin", adminFlag)
  // example: v-preventReClick
  app.directive("preventReClick", preventReClick)
}

/** Permission directive, similar to checkPermission */
const permission: Directive = {
  mounted(el, binding) {
    const { value } = binding
    const buttons = useUserStoreHook().buttons
    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = buttons.some((button) => {
        return value.includes(button.perms)
      })
      if (!hasPermission) {
        el.style.display = "none"
      }
    } else {
      throw new Error(`need permissions! Like v-has="['update:data','add:data']"`)
    }
  }
}

/** Permission directive: checks whether the signed-in user is an admin */
const adminFlag: Directive = {
  mounted(el) {
    const hasPermission = useUserStoreHook().userInfo?.adminFlag === 1
    if (!hasPermission) {
      el.style.display = "none"
    }
  }
}

/** Directive that prevents duplicate requests */
const preventReClick: Directive = {
  mounted(el, binding) {
    const { value } = binding
    el.addEventListener("click", () => {
      el.disabled = true
      el.loading = true
      setTimeout(() => {
        el.disabled = false
        el.loading = false
      }, value || 3000)
    })
  }
}
