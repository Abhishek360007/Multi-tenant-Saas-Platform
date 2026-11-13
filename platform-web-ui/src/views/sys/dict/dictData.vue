<template>
  <div class="app-container" v-loading="loading">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by name" />
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
          <el-table-column prop="dictCode" label="ID" />
          <el-table-column prop="dictLabel" label="Name" />
          <el-table-column prop="dictValue" label="Key" />
          <el-table-column prop="isDefault" label="Default">
            <template #default="scope">
              {{ dictStore.getYesNo(scope.row.isDefault) }}
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
            <el-form-item prop="dictLabel" label="Name">
              <el-input v-model.trim="entityForm.dictLabel" placeholder="Name" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="isDefault" label="Default">
              <select-dict-data v-model:value="entityForm.isDefault" dictTypeKey="sys_yes_no" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item prop="dictValue" label="Key">
          <el-input
            v-model="entityForm.dictValue"
            placeholder="Key"
            show-word-limit
            type="textarea"
            :rows="5"
            tabindex="2"
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
import { useRoute } from "vue-router"
import { Plus, Edit, Delete, Search } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus"
import { queryDictDataApi, saveDictDataApi, updateDictDataApi, deleteDictDataApi } from "@/api/dict"
import { cloneDeep } from "lodash-es"
import { IDictData } from "@/types/dict"
import { useDictStoreHook } from "@/store/modules/dict"

const dictStore = useDictStoreHook()

const route = useRoute()

const typeKey = route.query.typeKey as string

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<IDictData[]>()

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: "",
  dictTypeKey: typeKey,
  includeCauu: true
})

/// Form data
const entityForm = ref<IDictData>({
  dictCode: 0,
  dictSort: 0,
  dictLabel: "",
  dictValue: "",
  typeKey: typeKey,
  isDefault: 0
})

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  dictLabel: [{ required: true, message: "Enter a name", trigger: "blur" }],
  dictValue: [{ required: true, message: "Enter a value", trigger: "blur" }],
  isDefault: [{ required: true, message: "Choose default", trigger: "blur" }]
}

// Fetch data
const getTableData = () => {
  loading.value = true
  queryDictDataApi(params)
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
    dictCode: 0,
    dictSort: 0,
    dictLabel: "",
    dictValue: "",
    typeKey: typeKey,
    isDefault: 0
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
const handleEdit = (data: IDictData) => {
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
const handleDelete = (data: IDictData) => {
  ElMessageBox.confirm(`Delete ${data.dictLabel}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteDictDataApi(data.dictCode)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.dictLabel}`,
              type: "success"
            })
          }
        })
        .catch((err) => {
          console.log("err :>> ", err)
        })
        .finally(() => {
          getTableData()
          dictStore.getDictMapKeys()
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
        saveDictDataApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Dictionary added",
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
            dictStore.getDictMapKeys()
          })
      } else {
        updateDictDataApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Dictionary updated",
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
            dictStore.getDictMapKeys()
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
