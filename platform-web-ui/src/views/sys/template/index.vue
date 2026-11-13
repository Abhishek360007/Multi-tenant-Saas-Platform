<template>
  <div class="app-container" v-loading="loading">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by template name" />
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
          <el-table-column prop="id" label="Template ID" />
          <el-table-column prop="name" label="Name" />
          <el-table-column prop="description" label="Description">
            <template #default="scope">
              <el-popover placement="top-start" :width="300" trigger="hover" v-if="scope.row.description"
                :content="scope.row.description">
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
          <el-table-column label="Actions" width="200">
            <template #default="scope">
              <el-link :icon="EditPen" type="info" @click="handleEditMenu(scope.row)">Permissions</el-link>
              &nbsp;
              <el-link :icon="Edit" type="warning" @click="handleEdit(scope.row)">Edit</el-link>
              &nbsp;
              <el-link v-admin :icon="Delete" type="danger" @click="handleDelete(scope.row)">Delete</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pager-wrapper">
        <el-pagination v-model:current-page="params.pageIndex" :page-size="params.pageSize" :background="true"
          layout="sizes, total, prev, pager, next, jumper" :total="params.pageTotal" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- Add/edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%" :close-on-click-modal="false">
      <el-form ref="entityFormRef" :model="entityForm" :rules="entityFormRules" label-position="right"
        label-width="80px">
        <el-form-item prop="name" label="Name">
          <el-input v-model.trim="entityForm.name" placeholder="Name" type="text" tabindex="1" />
        </el-form-item>
        <el-form-item prop="description" label="Description">
          <el-input v-model="entityForm.description" placeholder="Description" :rows="5" :maxlength="100" type="textarea"
            tabindex="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" v-preventReClick @click="handleSaveAndFlush">OK</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Edit permissions -->
    <el-dialog v-model="menusDialogVisible" :title="menusDialogTitle" width="50%" :close-on-click-modal="false">
      <custom-tree v-if="menusData" :data="menusData.all" ref="menusRef" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="menusDialogVisible = false">Cancel</el-button>
          <el-button type="primary" v-preventReClick @click="handleUpdateMenus">OK</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { Plus, Edit, Delete, Search, EditPen } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox, FormInstance, FormRules, ElTree } from "element-plus"
import {
  queryTemplateApi,
  saveTemplateApi,
  updateTemplateApi,
  deleteTemplateApi,
  getTemplateMenusApi,
  updateTemplateMenusApi
} from "@/api/template"
import { cloneDeep } from "lodash-es"
import { ISysTemplate } from "@/types/sys"
import { IUpdateMenus } from "@/types/base"
import { showStringOverflow } from "@/hooks/useIndex"
import CustomTree from "@/components/CustomTree/index.vue"

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<ISysTemplate[]>()

const menusDialogVisible = ref(false)

const menusDialogTitle = ref("")

const menusRef = ref<any>()
const menusData = ref<IUpdateMenus>()

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: "",
  includeCauu: true
})

/// Form data
const entityForm = ref<ISysTemplate>({
  id: 0,
  name: "",
  description: ""
})

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  name: [{ required: true, message: "Enter a name", trigger: "blur" }],
  description: [{ required: true, message: "Enter a description", trigger: "blur" }]
}

// Fetch data
const getTableData = () => {
  loading.value = true
  queryTemplateApi(params)
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
    id: 0,
    name: "",
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

// Edit permissions
const handleEditMenu = (data: ISysTemplate) => {
  loading.value = true
  entityForm.value = cloneDeep(data)
  menusDialogTitle.value = "Edit"
  getTemplateMenusApi(data.id)
    .then((resp) => {
      menusData.value = resp.data
      menusDialogVisible.value = true
    })
    .catch((err) => {
      console.log("err :>> ", err)
    })
    .finally(() => {
      // Call the child component method to re-render tree
      menusRef.value!.setCheckedKeys(menusData.value!.have)
      loading.value = false
    })
}

// Edit permissions
const handleUpdateMenus = () => {
  const menuIds: number[] = menusRef.value!.getCheckedKeys()
  if (menuIds && menuIds.length < 1) {
    ElMessage({
      message: "Select at least one permission",
      type: "warning"
    })
    return
  }
  ElMessageBox.confirm(`Change the permissions of ${entityForm.value.name}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      const params = {
        id: entityForm.value.id,
        menus: menuIds
      }
      updateTemplateMenusApi(params)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Updated permissions of ${entityForm.value.name}`,
              type: "success"
            })
          }
        })
        .catch((err) => {
          console.log("err :>> ", err)
        })
        .finally(() => {
          menusDialogVisible.value = false
        })
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
}

// Edit
const handleEdit = (data: ISysTemplate) => {
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
const handleDelete = (data: ISysTemplate) => {
  ElMessageBox.confirm(`Delete ${data.name}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteTemplateApi(data.id)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.name}`,
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
        saveTemplateApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Template added",
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
        updateTemplateApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Template updated",
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
