<script lang="ts" setup>
import { computed } from "vue"
import { useTheme } from "@/hooks/useTheme"
// Set the Element Plus locale to English
import en from "element-plus/es/locale/lang/en"
import { useSettingsStoreHook } from "@/store/modules/settings"

const settingsStore = useSettingsStoreHook()

const controlSize = computed(() => {
  return settingsStore.controlSize as any
})

const { initTheme } = useTheme()

/** Initialize the theme */
initTheme()
</script>

<template>
  <el-config-provider :locale="en" :size="controlSize">
    <router-view />
  </el-config-provider>
</template>
