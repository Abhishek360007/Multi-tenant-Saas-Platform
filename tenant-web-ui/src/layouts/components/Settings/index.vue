<script lang="ts" setup>
import { watchEffect } from "vue"
import { storeToRefs } from "pinia"
import { useSettingsStore } from "@/store/modules/settings"
import { resetConfigLayout } from "@/utils"
import SelectLayoutMode from "./SelectLayoutMode.vue"
import { Refresh } from "@element-plus/icons-vue"

const settingsStore = useSettingsStore()

/** storeToRefs keeps the extracted properties reactive */
const {
  layoutMode,
  showTagsView,
  showLogo,
  fixedHeader,
  showFooter,
  showNotify,
  showThemeSwitch,
  showScreenfull,
  showSearchMenu,
  cacheTagsView,
  showWatermark,
  showGreyMode,
  showColorWeakness,
  showSearchTenant,
  showControlSize
} = storeToRefs(settingsStore)

/** Switch settings */
const switchSettings = {
  "Show tab bar": showTagsView,
  "Show Logo": showLogo,
  "Fixed Header": fixedHeader,
  "Show footer": showFooter,
  "Show notifications": showNotify,
  "Show theme switch": showThemeSwitch,
  "Show full-screen button": showScreenfull,
  "Show search button": showSearchMenu,
  "Show project switcher": showSearchTenant,
  "Show component size": showControlSize,
  "Cache tab bar": cacheTagsView,
  "Enable watermark": showWatermark,
  "Grayscale mode": showGreyMode,
  "Color-blind mode": showColorWeakness
}

/** Outside left layout, the header is always fixed */
watchEffect(() => {
  layoutMode.value !== "left" && (fixedHeader.value = true)
})
</script>

<template>
  <div class="setting-container">
    <h4>Layout</h4>
    <SelectLayoutMode />
    <el-divider />
    <h4>Features</h4>
    <div class="setting-item" v-for="(settingValue, settingName, index) in switchSettings" :key="index">
      <span class="setting-name">{{ settingName }}</span>
      <el-switch v-model="settingValue.value" :disabled="layoutMode !== 'left' && settingName === 'Fixed Header'" />
    </div>
    <el-button type="danger" :icon="Refresh" @click="resetConfigLayout">Reset</el-button>
  </div>
</template>

<style lang="scss" scoped>
@import "@/styles/mixins.scss";

.setting-container {
  padding: 0px 20px;
  .setting-item {
    font-size: 14px;
    color: var(--el-text-color-regular);
    padding: 3px 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .setting-name {
      @extend %ellipsis;
    }
  }
  .el-button {
    margin-top: 40px;
    width: 100%;
  }
}
</style>
