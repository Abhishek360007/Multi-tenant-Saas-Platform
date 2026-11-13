import store from "@/store"
import { defineStore } from "pinia"
import { type Ref, ref, watch } from "vue"
import { type LayoutSettings, layoutSettings } from "@/config/layouts"
import { setConfigLayout, getControlSize, setControlSize } from "@/utils/cache/local-storage"

type SettingsStore = {
  // Mapped type over the keys of layoutSettings
  [Key in keyof LayoutSettings]: Ref<LayoutSettings[Key]>
}

type SettingsStoreKey = keyof SettingsStore

export const useSettingsStore = defineStore("settings", () => {
  /** State object */
  const state = {} as SettingsStore

  // Iterate over the layoutSettings entries
  for (const [key, value] of Object.entries(layoutSettings)) {
    // Assert the key type and wrap the value in ref to make it reactive
    const refValue = ref(value)
    state[key as SettingsStoreKey] = refValue
    // Watch each reactive value
    watch(refValue, () => {
      // Cache
      const settings = _getCacheData()
      setConfigLayout(settings)
    })
  }

  /** Data to cache: convert the state object into a settings object */
  const _getCacheData = () => {
    const settings = {} as LayoutSettings
    for (const [key, value] of Object.entries(state)) {
      // @ts-ignore
      settings[key as SettingsStoreKey] = value.value
    }
    return settings
  }

  const controlSize = ref<string>(getControlSize() || "default")

  // Update component size
  const updateControlSize = (value: string) => {
    controlSize.value = value
    setControlSize(value)
  }

  return {
    ...state,
    controlSize,
    updateControlSize
  }
})

/** Use outside setup */
export function useSettingsStoreHook() {
  return useSettingsStore(store)
}
