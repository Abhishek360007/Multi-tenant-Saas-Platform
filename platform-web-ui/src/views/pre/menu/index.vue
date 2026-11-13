<template>
  <div class="app-container" v-loading="loading" v-has="['pre_menu:view']">
    <el-card shadow="never" class="search-wrapper">
      <div class="flex">
        <el-button type="primary" :icon="Plus" v-has="['pre_menu:create']" @click="handleAdd(null)">Add</el-button>
        <el-button type="success" :icon="Sort" @click="switchExpandAndCollapse">{{
          expandAndCollapse ? "Collapse" : "Expand"
        }}</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table ref="tableRef" :data="tableData" row-key="id" :default-expand-all="expandAndCollapse">
          <el-table-column prop="menuId" label="Permissions ID" />
          <el-table-column prop="menuName" label="Permission name" />
          <el-table-column prop="perms" label="Permission key" />
          <el-table-column prop="component" label="Permission component" />
          <el-table-column prop="path" label="Route path">
            <template #default="scope">
              <el-popover :content="scope.row.path" placement="top-start" :width="200" trigger="hover">
                <template #reference>
                  <el-text class="w-150px mb-2" truncated>
                    {{ scope.row.path }}
                  </el-text>
                </template>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="icon" label="Permission icon">
            <template #default="scope">
              <svg-icon v-if="scope.row.icon" :name="scope.row.icon" :size="28" />
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="Sort order" width="80" />
          <el-table-column prop="target" label="Owner" width="100">
            <template #default="scope">
              <el-tag v-if="scope.row.target === 0" effect="dark">
                {{ dictStore.getMenuTarget(scope.row.target) }}
              </el-tag>
              <el-tag v-else-if="scope.row.target === 1" effect="light" type="info">
                {{ dictStore.getMenuTarget(scope.row.target) }}
              </el-tag>
              <el-tag v-else effect="plain" type="danger">
                {{ dictStore.getMenuTarget(scope.row.target) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="menuType" label="Permission type">
            <template #default="scope">
              <el-tag v-if="scope.row.menuType === 0" effect="plain" type="info">
                {{ dictStore.getMenuType(scope.row.menuType) }}
              </el-tag>
              <el-tag v-else effect="light" type="danger">
                {{ dictStore.getMenuType(scope.row.menuType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="visible" label="Status">
            <template #default="scope">
              {{ dictStore.getShowHide(scope.row.visible) }}
            </template>
          </el-table-column>
          <el-table-column prop="keepAlive" label="Keep alive">
            <template #default="scope">
              {{ dictStore.getYesNo(scope.row.keepAlive) }}
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
              <el-link
                :icon="Plus"
                v-if="scope.row.menuType === 0"
                v-has="['pre_menu:create']"
                type="primary"
                @click="handleAdd(scope.row)"
                >Add</el-link
              >
              &nbsp;
              <el-link :icon="Edit" type="warning" v-has="['pre_menu:update']" @click="handleEdit(scope.row)"
                >Edit</el-link
              >
              &nbsp;
              <el-link
                :icon="Delete"
                v-if="!scope.row.children"
                type="danger"
                v-has="['pre_menu:delete']"
                @click="handleDelete(scope.row)"
                >Delete</el-link
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- Add/edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%" :close-on-click-modal="false">
      <el-form
        ref="entityFormRef"
        :model="entityForm"
        :rules="entityFormRules"
        label-position="right"
        label-width="80px"
        @keyup.enter="handleSaveAndFlush"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="menuName" label="Permission name">
              <el-input v-model.trim="entityForm.menuName" placeholder="Name" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="perms" label="Permission key">
              <el-input v-model.trim="entityForm.perms" placeholder="Key" type="text" tabindex="2" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="sort" label="Permission sort order">
              <el-input-number v-model="entityForm.sort" :min="1" :max="9999999" tabindex="6" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="menuType" label="Permission type">
              <select-dict-data v-model:value="entityForm.menuType" dictTypeKey="sys_menu_type" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="entityForm.menuType == 0">
          <el-col :span="12">
            <el-form-item prop="component" label="Permission component">
              <el-input v-model.trim="entityForm.component" placeholder="Component" type="text" tabindex="3" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="icon" label="Icon">
              <svg-icon-select v-model:value="entityForm.icon" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="entityForm.menuType == 0">
          <el-col :span="12">
            <el-form-item prop="visible" label="Visibility">
              <select-dict-data v-model:value="entityForm.visible" dictTypeKey="sys_show_hide" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="path" label="Address" v-if="entityForm.menuType == 0">
              <el-input v-model.trim="entityForm.path" placeholder="Route" type="text" tabindex="5" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="target" label="Permission scope">
              <select-dict-data v-model:value="entityForm.target" dictTypeKey="sys_menu_target" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="parentId" label="Parent permission">
              <el-cascader
                v-model="entityForm.parentId"
                :options="parentNodes"
                :props="{ checkStrictly: true }"
                @change="cascaderChange"
                filterable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="keepAlive" label="Keep alive">
              <select-dict-data v-model:value="entityForm.keepAlive" dictTypeKey="sys_yes_no" />
            </el-form-item>
          </el-col>
          <el-col :span="12"> &nbsp; </el-col>
        </el-row>
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
import { treeMenuApi, saveMenuApi, updateMenuApi, deleteMenuApi } from "@/api/menu"
import { useDictStoreHook } from "@/store/modules/dict"
import { cloneDeep } from "lodash-es"
import { ISysMenu } from "@/types/pms"
import { ref, onMounted } from "vue"
import { Plus, Edit, Delete, Sort } from "@element-plus/icons-vue"
import SvgIconSelect from "@/components/SvgIconSelect/index.vue"
import {
  ElMessage,
  ElMessageBox,
  FormInstance,
  FormRules,
  TableInstance,
  type CascaderOption,
  type CascaderValue
} from "element-plus"
import { flatTreeToCascaderOption } from "@/utils"

const dictStore = useDictStoreHook()

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableRef = ref<TableInstance | null>(null)

const tableData = ref<ISysMenu[]>([])

/// Form data
const entityForm = ref<ISysMenu>({
  menuId: 0,
  parentId: 0,
  menuName: "",
  perms: "",
  component: "",
  icon: "",
  sort: 0,
  path: "",
  target: 2,
  menuType: 0,
  visible: 0,
  keepAlive: 0,
  children: []
})

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  parentId: [{ required: true, message: "Select a parent permission", trigger: "blur" }],
  menuName: [{ required: true, message: "Enter a name", trigger: "blur" }],
  perms: [
    { required: true, message: "Enter a permission key", trigger: "blur" },
    { min: 5, max: 64, message: "At least 5 characters", trigger: "blur" }
  ],
  component: [{ required: true, message: "Enter a component name", trigger: "blur" }],
  icon: [{ required: true, message: "Enter an icon name", trigger: "blur" }],
  sort: [{ required: true, message: "Enter a sort order", trigger: "blur" }],
  path: [{ required: true, message: "Enter a route", trigger: "blur" }],
  target: [{ required: true, message: "Enter a permission scope", trigger: "blur" }],
  menuType: [{ required: true, message: "Enter a permission type", trigger: "blur" }],
  visible: [{ required: true, message: "Choose shown or hidden", trigger: "blur" }],
  keepAlive: [{ required: true, message: "Select keep-alive", trigger: "blur" }]
}

// Parent permission
const parentNodes = ref<CascaderOption[]>()

const cascaderChange = (data: CascaderValue) => {
  const arr1 = data as number[]
  entityForm.value.parentId = arr1[arr1.length - 1]
}

const expandAndCollapse = ref(false)

// Expand and collapse
const switchExpandAndCollapse = () => {
  expandAndCollapse.value = !expandAndCollapse.value
  const deep = (arr1: ISysMenu[]) => {
    arr1.forEach((item: ISysMenu) => {
      if (item.children) {
        deep(item.children)
      }
      tableRef.value!.toggleRowExpansion(item, expandAndCollapse.value)
    })
  }
  deep(tableData.value!)
}

// Fetch data
const getTableData = () => {
  loading.value = true
  treeMenuApi()
    .then((resp) => {
      tableData.value = resp.data
      parentNodes.value = flatTreeToCascaderOption(resp.data, { value: "id", label: "menuName" })
      const temp: CascaderOption = {
        value: 0,
        label: "Top-level permission"
      }
      parentNodes.value.unshift(temp)
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
    menuId: 0,
    parentId: 0,
    menuName: "",
    perms: "",
    component: "",
    icon: "",
    sort: 0,
    path: "",
    target: 2,
    menuType: 0,
    visible: 0,
    keepAlive: 0,
    children: []
  }
}

// Add
const handleAdd = (data: ISysMenu | null) => {
  resetEntity()
  if (data) {
    // Parent permission
    entityForm.value.parentId = data.menuId
    // Sort order
    if (data.children) {
      entityForm.value.sort = (data.children.length + 1) * 10
    } else {
      entityForm.value.sort = 0
    }
  } else {
    // Parent permission
    entityForm.value.parentId = 0
    entityForm.value.sort = (tableData.value.length + 1) * 10
  }
  // Shown and hidden
  entityForm.value.visible = 1
  dialogTitle.value = "Add"
  saveFlag.value = true
  dialogVisible.value = true
}

// Edit
const handleEdit = (data: ISysMenu) => {
  resetEntity()
  entityForm.value = cloneDeep(data)
  dialogTitle.value = "Edit"
  saveFlag.value = false
  dialogVisible.value = true
}

// Delete
const handleDelete = (data: ISysMenu) => {
  ElMessageBox.confirm(`Delete ${data.menuName}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteMenuApi(data.menuId)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.menuName}`,
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
        saveMenuApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Permission added",
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
        updateMenuApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Permission updated",
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
