<template>
  <el-container v-loading="loading">
    <el-header>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-input v-model="params.keywords" placeholder="Search by tenant name" clearable :prefix-icon="Search" />
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="searchTenantList">Search</el-button>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-table :data="simpleTenants" style="width: 100%">
        <el-table-column prop="tenantId" label="Tenant ID" width="120" />
        <el-table-column prop="logo" label="Logo" width="100">
          <template #default="scope">
            <el-avatar :size="50" :icon="UserFilled" :src="scope.row.logo" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Tenant name">
          <template #default="scope">
            <strong style="color: #409eff" v-if="!currentTenantId(scope.row.tenantId)">
              {{ scope.row.name }}
            </strong>
            <span v-else> {{ scope.row.name }} </span>
          </template>
        </el-table-column>
        <el-table-column prop="linkman" label="Contact person" />
        <el-table-column fixed="right" label="Actions" width="100">
          <template #default="scope">
            <el-button
              v-if="currentTenantId(scope.row.tenantId)"
              text
              type="primary"
              @click="handleSwitchClick(scope.row)"
              >Switch</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-main>
    <el-footer>
      <el-pagination
        v-model:current-page="params.pageIndex"
        :page-size="params.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :background="true"
        layout="sizes, total, prev, pager, next, jumper"
        :total="params.pageTotal"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { computed, reactive, ref, onMounted } from "vue"
import { Search, UserFilled } from "@element-plus/icons-vue"
import { ISearchQuery, ISimpleTenant } from "@/types/base"
import { simpleQueryTenantApi } from "@/api/tenant"
import { useTenantStoreHook } from "@/store/modules/tenant"
import { useProjectStoreHook } from "@/store/modules/project"

const tenantStore = useTenantStoreHook()
const projectStore = useProjectStoreHook()

const simpleTenants = ref<ISimpleTenant[]>()

/** Load */
const loading = ref(false)

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 5,
  keywords: ""
})

const emit = defineEmits<{
  (e: "handleSwitch", id: string): void
}>()

// Switch tenant
const handleSwitchClick = (t: ISimpleTenant) => {
  const tenant: ISimpleTenant = {
    tenantId: t.tenantId,
    logo: t.logo,
    name: t.name,
    linkman: t.linkman
  }
  tenantStore.setCurrentTenant(tenant)
  // Reset the current project to the default
  projectStore.resetCurrentProject()
  emit("handleSwitch", t.name)
}

const handleSizeChange = (val: number) => {
  params.pageSize = val
  searchTenantList()
}

const handleCurrentChange = (val: number) => {
  params.pageIndex = val
  searchTenantList()
}

const currentTenantId = computed(() => {
  return (tenantId: string) => {
    return tenantStore.getCurrentTenantId() !== tenantId
  }
})

const searchTenantList = () => {
  loading.value = true
  const p: ISearchQuery = {
    pageIndex: params.pageIndex,
    pageSize: params.pageSize,
    keywords: params.keywords
  }
  simpleQueryTenantApi(p)
    .then((resp) => {
      simpleTenants.value = resp.data.list
      params.pageTotal = resp.data.total
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
    .finally(() => {
      loading.value = false
    })
}

onMounted(() => {
  searchTenantList()
})

const searchList = () => searchTenantList()

// Expose component methods to parent components
defineExpose({
  searchList
})
</script>

<style lang="scss" scoped>
.el-header {
  --el-header-padding: 0px;
}
.el-main {
  --el-main-padding: 20px 0px;
}
.el-footer {
  --el-main-padding: 0px;
}
</style>
