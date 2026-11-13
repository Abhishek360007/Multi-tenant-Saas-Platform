<script lang="ts" setup>
import { computed, watchEffect } from "vue"
import { storeToRefs } from "pinia"
import { useAppStore } from "@/store/modules/app"
import useResize from "./hooks/useResize"
import { useWatermark } from "@/hooks/useWatermark"
import LeftMode from "./LeftMode.vue"
import TopMode from "./TopMode.vue"
import LeftTopMode from "./LeftTopMode.vue"
import { Settings, RightPanel } from "./components"
import { DeviceEnum } from "@/constants/app-key"
import { getCssVariableValue, setCssVariableValue } from "@/utils"
import { useSettingsStore } from "@/store/modules/settings"

/** Layout Responsive layout */
useResize()
const { setWatermark, clearWatermark } = useWatermark()

const appStore = useAppStore()
const settingsStore = useSettingsStore()

const { showSettings, layoutMode, showTagsView, showWatermark, showGreyMode, showColorWeakness } =
  storeToRefs(settingsStore)

const classes = computed(() => {
  return {
    showGreyMode: showGreyMode.value,
    showColorWeakness: showColorWeakness.value
  }
})

//#region When the tab bar is hidden its height is removed so the logo and header heights always match
const cssVariableName = "--v3-tagsview-height"
const v3TagsviewHeight = getCssVariableValue(cssVariableName)
watchEffect(() => {
  showTagsView.value
    ? setCssVariableValue(cssVariableName, v3TagsviewHeight)
    : setCssVariableValue(cssVariableName, "0px")
})
//#endregion

/** Turn the watermark on or off */
watchEffect(() => {
  showWatermark.value ? setWatermark(import.meta.env.VITE_APP_TITLE) : clearWatermark()
})
</script>

<template>
  <div :class="classes">
    <!-- Left layout -->
    <LeftMode v-if="layoutMode === 'left' || appStore.device === DeviceEnum.Mobile" />
    <!-- Top layout -->
    <TopMode v-else-if="layoutMode === 'top'" />
    <!-- Mixed layout -->
    <LeftTopMode v-else-if="layoutMode === 'left-top'" />
    <!-- Right settings panel -->
    <RightPanel v-if="showSettings">
      <Settings />
    </RightPanel>
  </div>
</template>

<style lang="scss" scoped>
.showGreyMode {
  filter: grayscale(1);
}

.showColorWeakness {
  filter: invert(0.8);
}
</style>
