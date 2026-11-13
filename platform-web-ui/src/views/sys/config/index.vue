<template>
  <div class="app-container" v-loading="loading">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by parameter name or key" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchTableData">Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" :icon="Plus" @click="handleAdd()">Add</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table :data="tableData">
          <el-table-column prop="configId" label="Settings ID" />
          <el-table-column prop="configName" label="Parameter name" />
          <el-table-column prop="configKey" label="Parameter key" />
          <el-table-column prop="configValue" label="Parameter value">
            <template #default="scope">
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                v-if="scope.row.configValue"
                :content="scope.row.configValue"
              >
                <template #reference>
                  <el-link> {{ showStringOverflow(scope.row.configValue) }}</el-link>
                </template>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="configType" label="Built in">
            <template #default="scope">
              {{ dictStore.getYesNo(scope.row.configType) }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="Created at">
            <template #default="scope">
              <operateUser :dateTime="scope.row.createTime" :entity="scope.row.createUserEntity" />
            </template>
          </el-table-column>
          <el-table-column prop="lastUpdateTime" label="Updated at">
            <template #default="scope">
              <operateUser :dateTime="scope.row.lastUpdateTime" :entity="scope.row.lastUpdateUserEntity" />
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="130">
            <template #default="scope">
              <div v-admin>
                <el-link :icon="Edit" type="warning" @click="handleEdit(scope.row)">Edit</el-link>
                &nbsp;
                <el-link v-if="scope.row.configType !== 1" :icon="Delete" type="danger" @click="handleDelete(scope.row)"
                  >Delete</el-link
                >
              </div>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%" :close-on-click-modal="false">
      <el-form
        ref="entityFormRef"
        :model="entityForm"
        :rules="entityFormRules"
        label-position="right"
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="configName" label="Parameter name">
              <el-input v-model.trim="entityForm.configName" placeholder="Parameter name" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="configType" label="Built in">
              <select-dict-data v-model:value="entityForm.configType" dictTypeKey="sys_yes_no" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="configKey" label="Parameter key">
          <el-input v-model.trim="entityForm.configKey" placeholder="Parameter key" type="text" tabindex="2" />
        </el-form-item>
        <el-form-item prop="configValue" label="Parameter value">
          <el-input
            v-model="entityForm.configValue"
            placeholder="Parameter value"
            show-word-limit
            type="textarea"
            :rows="5"
            tabindex="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" v-preventReClick @click="handleSaveAndFlush">OK</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { Plus, Edit, Delete, Search } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus"
import { queryConfigApi, saveConfigApi, updateConfigApi, deleteConfigApi } from "@/api/config"
import { cloneDeep } from "lodash-es"
import { ISysConfig } from "@/types/sys"
import { useDictStoreHook } from "@/store/modules/dict"
import { showStringOverflow } from "@/hooks/useIndex"

const dictStore = useDictStoreHook()

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<ISysConfig[]>()

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: "",
  includeCauu: true
})

/// Form data
const entityForm = ref<ISysConfig>({
  configId: 0,
  configName: "",
  configKey: "",
  configValue: "",
  configType: 0
})

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  configName: [{ required: true, message: "Enter a parameter name", trigger: "blur" }],
  configKey: [{ required: true, message: "Enter a parameter key", trigger: "blur" }],
  configValue: [{ required: true, message: "Enter a parameter value", trigger: "blur" }],
  configType: [{ required: true, message: "Choose built-in", trigger: "blur" }]
}

// Fetch data
const getTableData = () => {
  loading.value = true
  queryConfigApi(params)
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

// Reset entity fields
const resetEntity = () => {
  entityForm.value = {
    configId: 0,
    configName: "",
    configKey: "",
    configValue: "",
    configType: 0
  }
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

// Edit
const handleEdit = (data: ISysConfig) => {
  resetEntity()
  entityForm.value = cloneDeep(data)
  dialogTitle.value = "Edit"
  saveFlag.value = false
  dialogVisible.value = true
}

// Add
const handleAdd = () => {
  resetEntity()
  dialogTitle.value = "Add"
  saveFlag.value = true
  dialogVisible.value = true
}

// Delete
const handleDelete = (data: ISysConfig) => {
  ElMessageBox.confirm(`Delete ${data.configName}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteConfigApi(data.configId)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.configName}`,
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

// Add and edit
const handleSaveAndFlush = () => {
  entityFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      if (saveFlag.value) {
        saveConfigApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Setting added",
                type: "success"
              })
            }
          })
          .catch((err) => {
            console.log("err :>> ", err)
          })
          .finally(() => {
            loading.value = false
            dialogVisible.value = false
            getTableData()
          })
      } else {
        updateConfigApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Setting updated",
                type: "success"
              })
            }
          })
          .catch((err) => {
            console.log("err :>> ", err)
          })
          .finally(() => {
            loading.value = false
            dialogVisible.value = false
            getTableData()
          })
      }
    } else {
      loading.value = false
      return false
    }
  })
}

onMounted(() => {
  getTableData()
})
</script>

<style lang="scss" scoped></style>
