import store from "@/store"
import {defineStore} from "pinia"
import {ref} from "vue"
import {ISimpleTenant} from "@/types/base"
import {useUserStoreHook} from "./user"
import {getSimpleTenantApi} from "@/api/tenant"

export const useTenantStore = defineStore("tenant", () => {
  // Current tenant
  const tenant = ref<ISimpleTenant>()

  // Get current tenant
  const getTenantId = (): string => {
    return tenant.value!.tenantId
  }

  // Get current tenant
  const getTenant = (): ISimpleTenant => {
    return tenant.value!
  }

  /** Get the tenant of the signed-in user */
  const getLoginUserTenant = () => {
    return new Promise((resolve, reject) => {
      const tenantId = useUserStoreHook().userInfo?.tenantId
      if (tenantId) {
        getSimpleTenantApi(tenantId)
          .then((resp) => {
            if (resp.data) {
              tenant.value = resp.data
              resolve(resp)
            } else {
              reject(resp.message)
            }
          })
          .catch((error) => {
            reject(error)
          })
      }
    })
  }

  return {
    tenant,
    getTenantId,
    getTenant,
    getLoginUserTenant
  }
})

/** Use outside setup */
export function useTenantStoreHook() {
  return useTenantStore(store)
}
