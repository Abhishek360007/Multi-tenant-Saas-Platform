<template>
  <div class="app-container" v-loading="loading">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by dictionary name" />
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
          <el-table-column prop="typeId" label="Dictionary ID" />
          <el-table-column prop="typeName" label="Dictionary name" />
          <el-table-column prop="typeKey" label="Dictionary key">
            <template #default="scope">
              <router-link :to="{ path: '/sys/dict/data', query: { typeKey: scope.row.typeKey } }">
                <el-link type="primary">{{ scope.row.typeKey }}</el-link>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="Dictionary description">
            <template #default="scope">
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                v-if="scope.row.description"
                :content="scope.row.description"
              >
                <template #reference>
                  <el-link> {{ showStringOverflow(scope.row.description) }}</el-link>
                </template>
              </el-popover>
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
            <el-form-item prop="typeName" label="Dictionary name">
              <el-input v-model.trim="entityForm.typeName" placeholder="Dictionary name" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="typeKey" label="Dictionary KEY">
              <el-input v-model.trim="entityForm.typeKey" placeholder="Dictionary KEY" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item prop="description" label="Dictionary description">
          <el-input
            v-model="entityForm.description"
            placeholder="Dictionary description"
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
import { queryDictTypeApi, saveDictTypeApi, updateDictTypeApi, deleteDictTypeApi } from "@/api/dict"
import { cloneDeep } from "lodash-es"
import { IDictType } from "@/types/dict"
import { showStringOverflow } from "@/hooks/useIndex"
import { useDictStoreHook } from "@/store/modules/dict"

const dictStore = useDictStoreHook()

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<IDictType[]>()

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: "",
  includeCauu: true
})

/// Form data
const entityForm = ref<IDictType>({
  typeId: 0,
  typeName: "",
  typeKey: "",
  description: ""
})

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  typeName: [{ required: true, message: "Enter a dictionary name", trigger: "blur" }],
  typeKey: [{ required: true, message: "Enter a dictionary key", trigger: "blur" }],
  description: [{ required: true, message: "Enter a dictionary description", trigger: "blur" }]
}

// Fetch data
const getTableData = () => {
  loading.value = true
  queryDictTypeApi(params)
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
    typeId: 0,
    typeName: "",
    typeKey: "",
    description: ""
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
const handleEdit = (data: IDictType) => {
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
const handleDelete = (data: IDictType) => {
  ElMessageBox.confirm(`Delete ${data.typeName}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteDictTypeApi(data.typeId)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.typeName}`,
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
        saveDictTypeApi(entityForm.value)
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
        updateDictTypeApi(entityForm.value)
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
