import { computed } from "vue"
import { useTenantStoreHook } from "@/store/modules/tenant"
import { indiaAreaDeepQuery, expiredDateAgo, timeAgo } from "@/utils"

const tenantStore = useTenantStoreHook()

// Earlier time
const showTimeAgo = computed(() => {
  return (value: string) => timeAgo(value)
})

// Future time
const showExpiredDateAgo = computed(() => {
  return (value: string) => expiredDateAgo(value)
})

// Truncate a string
const showStringOverflow = computed(() => {
  return (value: string, len = 20) => {
    return value.length <= len ? value : value.substring(0, len)
  }
})

// Show the India area name
const showIndiaArea = computed(() => {
  return (value: number) => {
    const area = indiaAreaDeepQuery(value)
    if (area) {
      return area.mergerName
    }
    return ""
  }
})

// Whether the current tenant is the default tenant
const isDefaultTenantId = computed(() => {
  return (tenantId: string) => {
    return tenantStore.getDefaultTenantId() === tenantId
  }
})

export { showTimeAgo, showExpiredDateAgo, showStringOverflow, showIndiaArea, isDefaultTenantId }
