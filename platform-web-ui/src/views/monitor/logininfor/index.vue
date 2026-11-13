<template>
  <div class="app-container" v-loading="loading">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by nickname or username" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchTableData">Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" :icon="Delete" @click="handleBatchDelete">Delete selected</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table :data="tableData" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" />
          <el-table-column prop="grantType" label="Sign-in type" />
          <el-table-column prop="nickname" label="Nickname" />
          <el-table-column prop="username" label="Username">
            <template #default="scope">
              <el-tooltip effect="dark" :content="`User ID: ${scope.row.userId}`" placement="top">
                <el-link> {{ scope.row.username }}</el-link>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="avatar" label="Avatar">
            <template #default="scope">
              <el-avatar :size="40" :icon="UserFilled" :src="scope.row.avatar" />
            </template>
          </el-table-column>
          <el-table-column prop="requestUrl" label="Request URL" />
          <el-table-column prop="requestIp" label="Request IP" />
          <el-table-column prop="address" label="Request URL" />
          <el-table-column prop="osName" label="Operating system" />
          <el-table-column prop="browser" label="Browser" />
          <el-table-column prop="createTime" label="Created at">
            <template #default="scope">
              <el-tooltip v-if="scope.row.createTime" :content="scope.row.createTime" placement="top">
                <el-link> {{ showTimeAgo(scope.row.createTime) }}</el-link>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="80">
            <template #default="scope">
              <el-link :icon="Delete" type="danger" v-has="['pre_role:delete']" @click="handleDelete(scope.row)"
                >Delete</el-link
              >
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pager-wrapper">
        <el-pagination
          v-model:current-page="params.pageIndex"
          :page-size="params.pageSize"
          :background="true"
          layout="sizes, total, prev, pager, next, jumper"
          :total="params.pageTotal"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { Delete, Search, UserFilled } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { queryLoginLogApi, deleteLoginLogApi } from "@/api/loginLog"
import { ILoginLog } from "@/types/lms"
import { showTimeAgo } from "@/hooks/useIndex"

/** Load */
const loading = ref(false)

const tableData = ref<ILoginLog[]>()

const multipleSelection = ref<ILoginLog[]>([])

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: ""
})

// Fetch data
const getTableData = () => {
  loading.value = true
  queryLoginLogApi(params)
    .then((resp) => {
      tableData.value = resp.data.list
      params.pageTotal = resp.data.total
    })
    .catch((err) => {
      console.log("err :>> ", err)
    })
    .finally(() => {
      loading.value = false
    })
}

const searchTableData = () => {
  params.pageIndex = 1
  getTableData()
}

const handleSizeChange = (val: number) => {
  params.pageSize = val
  getTableData()
}

const handleCurrentChange = (val: number) => {
  params.pageIndex = val
  getTableData()
}

const handleSelectionChange = (data: ILoginLog[]) => {
  multipleSelection.value = data
}

// Delete selected
const handleBatchDelete = () => {
  if (multipleSelection.value.length < 1) {
    ElMessage({
      message: "Select at least one",
      type: "warning"
    })
    return
  }
  const items = multipleSelection.value.map((r) => r.id)
  ElMessageBox.confirm(`Delete the selected items?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteLoginLogApi(items)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted`,
              type: "success"
            })
          }
        })
        .catch((err) => {
          console.log("err :>> ", err)
        })
        .finally(() => {
          getTableData()
        })
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
}

// Delete
const handleDelete = (data: ILoginLog) => {
  ElMessageBox.confirm(`Delete ${data.nickname}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteLoginLogApi([data.id])
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.nickname}`,
              type: "success"
            })
          }
        })
        .catch((err) => {
          console.log("err :>> ", err)
        })
        .finally(() => {
          getTableData()
        })
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
}

onMounted(() => {
  getTableData()
})
</script>

<style lang="scss" scoped></style>
