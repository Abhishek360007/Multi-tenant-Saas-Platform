import { computed } from "vue"
import { useProjectStoreHook } from "@/store/modules/project"
import { useTenantStoreHook } from "@/store/modules/tenant"

const projectStore = useProjectStoreHook()
const tenantStore = useTenantStoreHook()

export function useTenantAndProject() {
  // Unique per tenant ID and project ID
  const currentOnlyId = computed(() => {
    return tenantStore.getCurrentTenantId() + projectStore.getCurrentProjectId()
  })

  return {
    currentOnlyId
  }
}
