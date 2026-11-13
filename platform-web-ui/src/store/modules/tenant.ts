import store from "@/store"
import { defineStore } from "pinia"
import { ref } from "vue"
import { ISimpleTenant } from "@/types/base"
import { setCurrentTenant as setTenant, getCurrentTenant as getTenant } from "@/utils/cache/local-storage"
import { defaultTenant } from "@/utils"

export const useTenantStore = defineStore("tenant", () => {
  // Current tenant
  const currentTenant = ref<ISimpleTenant>(getTenant())

  // Get current tenant
  const getCurrentTenantId = (): string => {
    return getCurrentTenant().tenantId
  }

  // Get the default tenant
  const getDefaultTenantId = (): string => {
    return defaultTenant.tenantId
  }

  // Get current tenant
  const getCurrentTenant = (): ISimpleTenant => {
    return currentTenant.value
  }

  // Set the current tenant
  const setCurrentTenant = (tenant: ISimpleTenant): void => {
    currentTenant.value = tenant
    setTenant(tenant)
  }

  // Reset the current tenant
  const resetCurrentTenant = (): void => {
    setCurrentTenant(defaultTenant)
  }

  // Get current tenant
  const isDefaultTenantId = (): boolean => {
    return getCurrentTenantId() === getDefaultTenantId()
  }

  return {
    getDefaultTenantId,
    isDefaultTenantId,
    getCurrentTenant,
    setCurrentTenant,
    resetCurrentTenant,
    getCurrentTenantId
  }
})

/** Use outside setup */
export function useTenantStoreHook() {
  return useTenantStore(store)
}
