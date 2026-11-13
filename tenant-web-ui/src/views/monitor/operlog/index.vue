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
          <el-table-column prop="title" label="Title" />
          <el-table-column prop="description" label="Description" />
          <el-table-column prop="serviceName" label="Service name" />
          <el-table-column prop="requestMethod" label="Request method" />
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
          <el-table-column prop="status" label="Status" />
          <el-table-column prop="timeCost" label="Duration (ms)" />
          <el-table-column prop="createTime" label="Created at">
            <template #default="scope">
              <el-tooltip v-if="scope.row.createTime" :content="scope.row.createTime" placement="top">
                <el-link> {{ showTimeAgo(scope.row.createTime) }}</el-link>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="120">
            <template #default="scope">
              <el-link :icon="View" type="primary" v-has="['pre_role:delete']" @click="handleInfo(scope.row)"
                >Details</el-link
              >
              &nbsp;
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

    <!-- Add/edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
      <el-form
        ref="entityFormRef"
        :model="entityForm"
        label-position="right"
        label-width="50px"
        @keyup.enter="dialogVisible = false"
      >
        <el-form-item label="Description">
          {{ entityForm.description }}
        </el-form-item>
        <el-form-item label="Address">
          {{ entityForm.requestUrl }}
        </el-form-item>
        <el-form-item label="Type">
          {{ entityForm.requestMethod }}
        </el-form-item>
        <el-form-item label="Parameters">
          <el-input v-model="entityForm.methodArgs" rows="5" type="textarea" />
        </el-form-item>
        <el-form-item label="Result">
          <el-input v-model="entityForm.responseResult" rows="10" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="dialogVisible = false">OK</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { Delete, Search, UserFilled, View } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { queryOperLogApi, deleteOperLogApi } from "@/api/loginLog"
import { IOperLog } from "@/types/lms"
import { showTimeAgo } from "@/hooks/useIndex"

/** Load */
const loading = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<IOperLog[]>()

/// Form data
const entityForm = ref<IOperLog>({
  /**
   * ID
   */
  id: "",
  /**
   * Title
   */
  title: "",
  /**
   * Description
   */
  description: "",
  /**
   * Service name
   */
  serviceName: "",
  /**
   * User ID
   */
  userId: 0,
  /**
   * User nickname
   */
  nickname: "",
  /**
   * Username
   */
  username: "",
  /**
   * Avatar
   */
  avatar: "",
  /**
   * Method
   */
  method: "",
  /**
   * Method arguments
   */
  methodArgs: "",
  /**
   * Request method
   */
  requestMethod: "",
  /**
   * Request URL
   */
  requestUrl: "",
  /**
   * Request IP
   */
  requestIp: "",
  /**
   * Request URL
   */
  address: "",
  /**
   * Response
   */
  responseResult: "",
  /**
   * Status
   */
  status: 0,
  /**
   * Error log
   */
  errorLog: "",
  /**
   * Duration (ms)
   */
  timeCost: 0,
  /**
   * Created at
   */
  createTime: ""
})

const multipleSelection = ref<IOperLog[]>([])

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: "",
  includeCauu: true
})

// Fetch data
const getTableData = () => {
  loading.value = true
  queryOperLogApi(params)
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

const handleSelectionChange = (data: IOperLog[]) => {
  multipleSelection.value = data
}

// Details
const handleInfo = (data: IOperLog) => {
  data.methodArgs = JSON.stringify(JSON.parse(data.methodArgs), null, "\t")
  data.responseResult = JSON.stringify(JSON.parse(data.responseResult), null, "\t")
  entityForm.value = data
  dialogVisible.value = true
  dialogTitle.value = data.title
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
      deleteOperLogApi(items)
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
const handleDelete = (data: IOperLog) => {
  ElMessageBox.confirm(`Delete ${data.title}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteOperLogApi([data.id])
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.title}`,
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
