<template>
  <div class="app-container" v-loading="loading" v-has="['pre_role:view']">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by role name" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchTableData">Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" :icon="Plus" v-has="['pre_role:create']" @click="handleAdd()">Add</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table :data="tableData">
          <el-table-column prop="roleId" label="Role ID" />
          <el-table-column prop="roleName" label="Name" />
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
          <el-table-column prop="sort" label="Sort order" sortable />
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
              <el-link :icon="EditPen" type="info" v-has="['pre_role:update:permissions']"
                @click="handleEditMenu(scope.row)">Permissions</el-link>
              &nbsp;
              <el-link :icon="Edit" type="warning" v-has="['pre_role:update']"
                @click="handleEdit(scope.row)">Edit</el-link>
              &nbsp;
              <el-link :icon="Delete" type="danger" v-has="['pre_role:delete']"
                @click="handleDelete(scope.row)">Delete</el-link>
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
        label-width="80px" @keyup.enter="handleSaveAndFlush">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="roleName" label="Name">
              <el-input v-model.trim="entityForm.roleName" placeholder="Name" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="sort" label="Sort order">
              <el-input-number v-model="entityForm.sort" :min="1" :max="9999999" tabindex="2" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item prop="description" label="Description">
          <el-input v-model="entityForm.description" placeholder="Description" :rows="3" :maxlength="100" type="textarea"
            tabindex="3" />
        </el-form-item>
        <el-form-item prop="dataScope" label="Data permissions">
          <select-dict-data v-model:value="dataScope" dictTypeKey="sys_data_scope" />
        </el-form-item>
        <el-form-item v-if="entityForm.dataScope === 2" prop="deptIds" label="Custom departments">
          <custom-tree v-if="deptsData" :data="deptsData?.all" ref="deptsRef" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="handleSaveAndFlush">OK</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Edit permissions -->
    <el-dialog v-model="menusDialog.visible" :title="menusDialog.title" width="50%" :close-on-click-modal="false">
      <custom-tree v-if="menusDialog.data" :data="menusDialog.data?.all" ref="menusRef" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="menusDialog.visible = false">Cancel</el-button>
          <el-button type="primary" v-preventReClick @click="handleUpdateMenus">OK</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue"
import { Plus, Edit, Delete, Search, EditPen } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox, FormInstance, FormRules, ElTree } from "element-plus"
import {
  queryRoleApi,
  saveRoleApi,
  updateRoleApi,
  deleteRoleApi,
  getRoleMenusApi,
  updateRoleMenusApi,
  getRoleDeptsApi
} from "@/api/role"
import { cloneDeep } from "lodash-es"
import { IPmsRole } from "@/types/pms"
import { showStringOverflow } from "@/hooks/useIndex"
import { IUpdateMenus } from "@/types/base"
import CustomTree from "@/components/CustomTree/index.vue"

interface ITreeDialog {
  /** Menu ID */
  visible: boolean
  /** Menu name */
  title: string
  /** Parent */
  data?: IUpdateMenus
}

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<IPmsRole[]>()

const deptsRef = ref<any>()
const deptsData = ref<IUpdateMenus>()

const menusRef = ref<any>()
const menusDialog = reactive<ITreeDialog>({
  visible: false,
  title: "",
  data: undefined
})

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: "",
  includeCauu: true
})

/// Form data
const entityForm = ref<IPmsRole>({
  roleId: 0,
  roleName: "",
  sort: 0,
  description: "",
  dataScope: 4,
  deptIds: []
})

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  roleName: [{ required: true, message: "Enter a name", trigger: "blur" }],
  sort: [{ required: true, message: "Enter a sort order", trigger: "blur" }],
  description: [{ required: true, message: "Enter a description", trigger: "blur" }],
  dataScope: [{ required: true, message: "Select data permissions", trigger: "blur" }],
}

// Fetch data
const getTableData = () => {
  loading.value = true
  queryRoleApi(params)
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
    roleId: 0,
    roleName: "",
    sort: 0,
    description: "",
    dataScope: 4,
    deptIds: []
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
const handleEditMenu = (data: IPmsRole) => {
  loading.value = true
  entityForm.value = cloneDeep(data)
  menusDialog.title = "Edit"
  getRoleMenusApi(data.roleId)
    .then((resp) => {
      menusDialog.data = resp.data
      menusDialog.visible = true
    })
    .catch((err) => {
      console.log("err :>> ", err)
    })
    .finally(() => {
      // Call the child component method to re-render tree
      menusRef.value!.setCheckedKeys(menusDialog.data!.have)
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
  ElMessageBox.confirm(`Change the permissions of ${entityForm.value.roleName}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      const params = {
        id: entityForm.value.roleId,
        menus: menuIds
      }
      updateRoleMenusApi(params)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Updated permissions of ${entityForm.value.roleName}`,
              type: "success"
            })
          }
        })
        .catch((err) => {
          console.log("err :>> ", err)
        })
        .finally(() => {
          menusDialog.visible = false
        })
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
}


const dataScope = computed({
  get(): number {
    return entityForm.value.dataScope
  },
  set(v: number) {
    if (v === 2) {
      handleEditDept()
    }
    entityForm.value.dataScope = v
  }
})


// Edit departments
const handleEditDept = () => {
  loading.value = true
  getRoleDeptsApi(entityForm.value.roleId)
    .then((resp) => {
      deptsData.value = resp.data
    })
    .catch((err) => {
      console.log("err :>> ", err)
    })
    .finally(() => {
      // Call the child component method to re-render tree
      deptsRef.value!.setCheckedKeys(deptsData.value!.have)
      loading.value = false
    })
}


// Edit
const handleEdit = (data: IPmsRole) => {
  resetEntity()
  entityForm.value = cloneDeep(data)
  if (data.dataScope === 2) {
    handleEditDept()
  }
  dialogTitle.value = "Edit"
  saveFlag.value = false
  dialogVisible.value = true
}

// Add
const handleAdd = () => {
  resetEntity()
  entityForm.value.sort = params.pageTotal + 1
  dialogTitle.value = "Add"
  saveFlag.value = true
  dialogVisible.value = true
}

// Delete
const handleDelete = (data: IPmsRole) => {
  ElMessageBox.confirm(`Delete ${data.roleName}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteRoleApi(data.roleId)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.roleName}`,
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
  // Custom permissions
  if (entityForm.value.dataScope === 2) {
    const deptIds: number[] = deptsRef.value!.getCheckedKeys()
    if (deptIds && deptIds.length < 1) {
      ElMessage({
        message: "Select at least one department",
        type: "warning"
      })
      return
    }
    entityForm.value.deptIds = deptIds
  }
  entityFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      if (saveFlag.value) {
        saveRoleApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Role added",
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
        updateRoleApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Role updated",
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
