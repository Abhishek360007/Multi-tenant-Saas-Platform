import store from "@/store"
import { defineStore } from "pinia"
import { ref } from "vue"
import { ISimpleProject } from "@/types/base"
import { defaultProject } from "@/utils"

export const useProjectStore = defineStore("project", () => {
  // Current project
  const currentProject = ref<ISimpleProject>(defaultProject)

  // Get current project
  const getCurrentProjectId = (): number => {
    return getCurrentProject().projectId
  }

  // Get the default project
  const getDefaultProjectId = (): number => {
    return defaultProject.projectId
  }

  // Get current project
  const getCurrentProject = (): ISimpleProject => {
    return currentProject.value
  }

  // Set the current project
  const setCurrentProject = (tenant: ISimpleProject): void => {
    currentProject.value = tenant
  }

  // Reset the current project
  const resetCurrentProject = (): void => {
    currentProject.value = defaultProject
  }

  // Get current project
  const isDefaultProjectId = (): boolean => {
    return getCurrentProjectId() === getDefaultProjectId()
  }

  return {
    getCurrentProjectId,
    getDefaultProjectId,
    getCurrentProject,
    setCurrentProject,
    resetCurrentProject,
    isDefaultProjectId
  }
})

/** Use outside setup */
export function useProjectStoreHook() {
  return useProjectStore(store)
}
