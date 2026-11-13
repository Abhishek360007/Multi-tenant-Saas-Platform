import {computed} from "vue"
import {useProjectStoreHook} from "@/store/modules/project"

const projectStore = useProjectStoreHook()

export function useTenantAndProject() {
  // Unique per tenant ID and project ID
  const currentOnlyId = computed(() => {
    return projectStore.getCurrentProjectId()
  })

  return {
    currentOnlyId
  }
}
