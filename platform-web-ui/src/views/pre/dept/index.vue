<template>
  <div class="app-container" v-loading="loading" v-has="['pre_dept:view']">
    <el-card shadow="never" class="search-wrapper">
      <div class="flex">
        <el-button type="primary" :icon="Plus" v-has="['pre_dept:create']" @click="handleAdd(null)">Add</el-button>
        <el-button type="success" :icon="Sort" @click="switchExpandAndCollapse">{{
          expandAndCollapse ? "Collapse" : "Expand"
          }}</el-button>
      </div>
    </el-card>
    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table ref="tableRef" :data="tableData" row-key="id" :default-expand-all="expandAndCollapse">
          <el-table-column prop="deptId" label="Department ID" />
          <el-table-column prop="deptName" label="Department name" />
          <el-table-column prop="leader" label="Leader name" />
          <el-table-column prop="leaderMobile" label="Leader contact" />
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
          <el-table-column prop="sort" label="Sort order" width="80" />
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
              <el-link :icon="Plus" type="primary" v-has="['pre_dept:create']"
                @click="handleAdd(scope.row)">Add</el-link>
              &nbsp;
              <el-link :icon="Edit" type="warning" v-has="['pre_dept:update']"
                @click="handleEdit(scope.row)">Edit</el-link>
              &nbsp;
              <el-link :icon="Delete" v-if="!scope.row.children" type="danger" v-has="['pre_dept:delete']"
                @click="handleDelete(scope.row)">Delete</el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    <!-- Add/edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%" :close-on-click-modal="false">
      <el-form ref="entityFormRef" :model="entityForm" :rules="entityFormRules" label-position="right"
        label-width="80px" @keyup.enter="handleSaveAndFlush">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="deptName" label="Department name">
              <el-input v-model.trim="entityForm.deptName" placeholder="Name" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="parentId" label="Parent department">
              <el-cascader v-model="entityForm.parentId" :options="parentNodes" :props="{ checkStrictly: true }"
                @change="cascaderChange" filterable tabindex="2" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="leader" label="Leader name">
              <el-input v-model.trim="entityForm.leader" placeholder="Leader name" type="text" tabindex="3" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="leaderMobile" label="Contact details">
              <el-input v-model.trim="entityForm.leaderMobile" placeholder="Leader contact" type="text" maxlength="11"
                show-word-limit tabindex="4" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="sort" label="Department sort order">
              <el-input-number v-model="entityForm.sort" :min="1" :max="9999999" tabindex="5" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="description" label="Description">
              <el-input v-model.trim="entityForm.description" placeholder="Description" :rows="3" type="textarea" tabindex="6" />
            </el-form-item>
          </el-col>
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
import { treeDeptApi, saveDeptApi, updateDeptApi, deleteDeptApi } from "@/api/dept"
import { IPmsDept } from "@/types/pms"
import { ref, onMounted } from "vue"
import { Plus, Edit, Delete, Sort } from "@element-plus/icons-vue"
import type { CascaderOption, CascaderValue } from "element-plus"
import { ElMessage, ElMessageBox, FormInstance, FormRules, TableInstance } from "element-plus"
import { isPhone } from "@/utils/validate"
import { cloneDeep } from "lodash-es"
import { showStringOverflow } from "@/hooks/useIndex"
import { flatTreeToCascaderOption } from "@/utils"

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableRef = ref<TableInstance | null>(null)

const tableData = ref<IPmsDept[]>([])

/// Form data
const entityForm = ref<IPmsDept>({
  deptId: 0,
  parentId: 0,
  deptName: "",
  leader: "",
  leaderMobile: "",
  sort: 0,
  description: "",
  ancestors: "",
  children: []
})

const phoneValidator = (rule: any, value: any, callback: any) => {
  if (!isPhone(value)) {
    callback(new Error("Invalid mobile number!"))
  } else {
    callback()
  }
}

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  parentId: [{ required: true, message: "Select a parent department", trigger: "blur" }],
  deptName: [{ required: true, message: "Enter a department name", trigger: "blur" }],
  leader: [{ required: true, message: "Enter a department leader", trigger: "blur" }],
  leaderMobile: [
    { required: true, message: "Enter the leader contact", trigger: "blur" },
    { validator: phoneValidator, trigger: "blur" }
  ],
  sort: [{ required: true, message: "Enter a sort order", trigger: "blur" }],
  description: [{ required: true, message: "Enter a description", trigger: "blur" }]
}

// Parent node
const parentNodes = ref<CascaderOption[]>()

const cascaderChange = (data: CascaderValue) => {
  const arr1 = data as number[]
  entityForm.value.parentId = arr1[arr1.length - 1]
}

const expandAndCollapse = ref(true)

// Expand and collapse
const switchExpandAndCollapse = () => {
  expandAndCollapse.value = !expandAndCollapse.value
  const deep = (arr1: IPmsDept[]) => {
    arr1.forEach((item: IPmsDept) => {
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
  treeDeptApi()
    .then((resp) => {
      tableData.value = resp.data
      parentNodes.value = flatTreeToCascaderOption(resp.data, { value: "deptId", label: "deptName" })
      const temp: CascaderOption = {
        value: 0,
        label: "Top-level department"
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
    deptId: 0,
    parentId: 0,
    deptName: "",
    leader: "",
    leaderMobile: "",
    sort: 0,
    description: "",
    ancestors: "",
    children: []
  }
}

// Add
const handleAdd = (data: IPmsDept | null) => {
  resetEntity()
  if (data) {
    // Parent permission
    entityForm.value.parentId = data.deptId
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
  dialogTitle.value = "Add"
  saveFlag.value = true
  dialogVisible.value = true
}

// Edit
const handleEdit = (data: IPmsDept) => {
  resetEntity()
  entityForm.value = cloneDeep(data)
  dialogTitle.value = "Edit"
  saveFlag.value = false
  dialogVisible.value = true
}

// Delete
const handleDelete = (data: IPmsDept) => {
  ElMessageBox.confirm(`Delete ${data.deptName}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteDeptApi(data.deptId)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.deptName}`,
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
        saveDeptApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Department added",
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
        if (entityForm.value.deptId === entityForm.value.parentId) {
          ElMessage({
            message: "A department cannot be its own parent",
            type: "warning"
          })
          loading.value = false
          return
        }
        updateDeptApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Department updated",
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
