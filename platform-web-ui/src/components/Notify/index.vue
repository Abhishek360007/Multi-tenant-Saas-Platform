<script lang="ts" setup>
import { ref, computed } from "vue"
import { ElMessage } from "element-plus"
import { Bell } from "@element-plus/icons-vue"
import NotifyList from "./NotifyList.vue"
import { useNoticeStoreHook } from "@/store/modules/notice"
import { IPushMessage } from "@/types/base"

const { broadcast, pushNotices } = useNoticeStoreHook()

type TabName = "Notifications" | "Messages" | "To-dos"

interface DataItem {
  name: TabName
  type: "primary" | "success" | "warning" | "danger" | "info"
  list: IPushMessage[]
}

/** Current badge value */
const badgeValue = computed(() => {
  return data.value.reduce((sum, item) => sum + item.list.length, 0)
})
/** Maximum badge value */
const badgeMax = 99
/** Panel width */
const popoverWidth = 350
/** Current Tab */
const activeName = ref<TabName>("Notifications")
/** All data */
const data = ref<DataItem[]>([
  // Notifications
  {
    name: "Notifications",
    type: "primary",
    list: broadcast
  },
  // Messages
  {
    name: "Messages",
    type: "danger",
    list: pushNotices
  },
  // To-dos
  {
    name: "To-dos",
    type: "warning",
    list: []
  }
])

const handleHistory = () => {
  ElMessage.success(`Go to the ${activeName.value} history page`)
}
</script>

<template>
  <div class="notify">
    <el-popover placement="bottom" :width="popoverWidth" trigger="click">
      <template #reference>
        <el-badge :value="badgeValue" :max="badgeMax" :hidden="badgeValue === 0">
          <el-tooltip effect="dark" content="Notifications" placement="bottom">
            <el-icon :size="20">
              <Bell />
            </el-icon>
          </el-tooltip>
        </el-badge>
      </template>
      <template #default>
        <el-tabs v-model="activeName" class="demo-tabs" stretch>
          <el-tab-pane v-for="(item, index) in data" :name="item.name" :key="index">
            <template #label>
              {{ item.name }}
              <el-badge :value="item.list.length" :max="badgeMax" :type="item.type" />
            </template>
            <el-scrollbar height="400px">
              <NotifyList :list="item.list" />
            </el-scrollbar>
          </el-tab-pane>
        </el-tabs>
        <div class="notify-history">
          <el-button link @click="handleHistory">View {{ activeName }} history</el-button>
        </div>
      </template>
    </el-popover>
  </div>
</template>

<style lang="scss" scoped>
.notify {
  margin-right: 10px;
  color: var(--el-text-color-regular);
}
.notify-history {
  text-align: center;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color);
}
</style>
