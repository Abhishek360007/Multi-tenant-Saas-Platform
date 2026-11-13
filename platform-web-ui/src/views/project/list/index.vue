<template>
  <div class="app-container" v-loading="loading" v-has="['cms_project:view']">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item label="Department">
          <CascaderDept v-model="params.deptId" :options="deptTree" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by project name" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchTableData">Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" :icon="Plus" v-has="['cms_project:create']" @click="handleAdd()">Add</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table :data="tableData">
          <el-table-column prop="projectId" label="Project ID" />
          <el-table-column prop="projectName" label="Project name" />
          <el-table-column prop="linkman" label="Contact person" />
          <el-table-column prop="contactNumber" label="Contact number" />
          <el-table-column prop="areaCode" label="Area">
            <template #default="scope">
              {{ showIndiaArea(scope.row.areaCode) }}
            </template>
          </el-table-column>
          <el-table-column prop="address" label="Address" />
          <el-table-column prop="pinCode" label="PIN" width="90" />
          <el-table-column prop="dept" label="Department" width="120">
            <template #default="scope">
              <el-popover v-if="scope.row.dept" placement="top-start" :width="230">
                <template #reference>
                  <el-tag>{{ scope.row.dept.deptName }}</el-tag>
                </template>
                <div><strong>Department leader:</strong> &nbsp;&nbsp;&nbsp;{{ scope.row.dept.leader }}</div>
                <div><strong>Leader mobile number:</strong> &nbsp;&nbsp;&nbsp;{{ scope.row.dept.leaderMobile }}</div>
                <div><strong>Department description:</strong> &nbsp;&nbsp;&nbsp;{{ scope.row.dept.description }}</div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status">
            <template #default="scope">
              {{ dictStore.getNormalDisable(scope.row.status) }}
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
              <el-link :icon="Edit" type="warning" v-has="['cms_project:update']" @click="handleEdit(scope.row)"
                >Edit</el-link
              >
              <!-- v-admin Only admins can reset project passwords and delete projects -->
              &nbsp;
              <el-dropdown v-admin @command="(cmd: string) => handleCommand(cmd, scope.row)">
                <span class="el-dropdown-link">
                  More
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <div v-has="['cms_project:reset:password']">
                      <el-dropdown-item :icon="Link" command="ResetPassword">Reset password</el-dropdown-item>
                    </div>
					<div v-if="scope.row.projectId !== 10001" v-has="['cms_project:delete']">
                      <el-dropdown-item :icon="Delete" command="Delete">Delete</el-dropdown-item>
                    </div>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
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
            <el-form-item prop="projectName" label="Project name">
              <el-input v-model.trim="entityForm.projectName" placeholder="Project name" type="text" tabindex="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="saveFlag" prop="password" label="Password">
              <el-input
                v-model.trim="entityForm.password"
                placeholder="Project password"
                type="password"
                tabindex="2"
                show-password
              />
            </el-form-item>
            <el-form-item v-else prop="projectId" label="Project ID">
              <el-input
                v-model.trim="entityForm.projectId"
                placeholder="Project ID"
                type="text"
                tabindex="1"
                :disabled="!saveFlag"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="linkman" label="Contact person">
              <el-input v-model.trim="entityForm.linkman" placeholder="Contact person" type="text" tabindex="4" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="contactNumber" label="Contact number">
              <el-input
                v-model.trim="entityForm.contactNumber"
                placeholder="Contact number"
                type="text"
                show-word-limit
                tabindex="3"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="status" label="Status">
              <select-dict-data v-model:value="entityForm.status" dictTypeKey="sys_normal_disable" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="deptId" label="Department">
              <CascaderDept v-model="entityForm.deptId" :options="deptTree" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="areaCode" label="Address area">
              <search-india-area v-model="entityForm.areaCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="address" label="Address">
              <el-input
                v-model="entityForm.address"
                :rows="3"
                clearable
                placeholder="Address"
                type="textarea"
                tabindex="6"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="pinCode" label="PIN code">
              <el-input v-model.trim="entityForm.pinCode" maxlength="6" clearable placeholder="e.g. 560001" />
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
import { ref, reactive, onMounted } from "vue"
import { deleteProjectApi, queryProjectApi, saveProjectApi, updateProjectApi, resetPasswordApi } from "@/api/project"
import { treeDeptApi } from "@/api/dept"
import { IPmsDept } from "@/types/pms"
import { ICmsProject } from "@/types/cms"
import { isPinCode, isTelphone } from "@/utils/validate"
import { Plus, Edit, Delete, Search, Link } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus"
import SearchIndiaArea from "@/components/SearchIndiaArea/index.vue"
import CascaderDept from "@/components/CascaderDept/index.vue"
import { useDictStoreHook } from "@/store/modules/dict"
import { cloneDeep } from "lodash-es"
import { showIndiaArea } from "@/hooks/useIndex"

const dictStore = useDictStoreHook()

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<ICmsProject[]>([])

const deptTree = ref<IPmsDept[]>([])

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  deptId: 0,
  keywords: "",
  includeCauu: true,
  includeDept: true
})

/// Form data
const entityForm = ref<ICmsProject>({
  projectId: 0,
  projectName: "",
  linkman: "",
  contactNumber: "",
  areaCode: 0,
  address: "",
  pinCode: "",
  sort: 0,
  status: 1,
  deptId: 0,
  password: "",
  dept: null
})

const telphoneValidator = (rule: any, value: any, callback: any) => {
  if (!isTelphone(value)) {
    callback(new Error("Invalid mobile or phone number!"))
  } else {
    callback()
  }
}
const passwordValidator = (rule: any, value: any, callback: any) => {
  if (!/^\w{5,24}$/.test(value)) {
    callback(new Error("Password: letters and numbers, 5 to 24 characters"))
  } else {
    callback()
  }
}

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  projectName: [{ required: true, message: "Enter a project name", trigger: "blur" }],
  linkman: [{ required: true, message: "Enter a contact person", trigger: "blur" }],
  contactNumber: [
    { required: true, message: "Enter contact details", trigger: "blur" },
    { validator: telphoneValidator, trigger: "blur" }
  ],
  areaCode: [{ required: true, message: "Select an area", trigger: "blur" }],
  address: [{ required: true, message: "Enter an address", trigger: "blur" }],
  pinCode: [
    { required: true, message: "Enter a PIN code", trigger: "blur" },
    {
      validator: (_rule: any, value: string, callback: (e?: Error) => void) =>
        isPinCode(value) ? callback() : callback(new Error("PIN code must be 6 digits")),
      trigger: "blur"
    }
  ],
  sort: [{ required: true, message: "Enter a sort order", trigger: "blur" }],
  status: [{ required: true, message: "Select a status", trigger: "blur" }],
  password: [
    { required: true, message: "Enter a project password", trigger: "blur" },
    { validator: passwordValidator, trigger: "blur" }
  ],
  deptId: [{ required: true, message: "Select a department", trigger: "blur" }]
}

const getTableData = () => {
  loading.value = true
  queryProjectApi(params)
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
    projectId: 0,
    projectName: "",
    linkman: "",
    contactNumber: "",
    areaCode: 0,
    address: "",
    pinCode: "",
    sort: 0,
    status: 1,
    deptId: 0,
    password: "123456",
    dept: null
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

const handleCommand = (command: string, data: ICmsProject) => {
  switch (command) {
    case "ResetPassword":
      handleResetPassword(data)
      break
    case "Delete":
      handleDelete(data)
      break
    default:
      console.log("command :>> ", command)
      break
  }
}

// Add
const handleAdd = () => {
  resetEntity()
  dialogTitle.value = "Add"
  saveFlag.value = true
  dialogVisible.value = true
}

// Reset password
const handleResetPassword = (data: ICmsProject) => {
  ElMessageBox.prompt(`Reset the password of ${data.projectName}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    inputPattern: /^\w{5,24}$/,
    inputErrorMessage: "Password must contain letters and numbers, 5 to 24 characters",
    type: "warning"
  })
    .then(({ value }) => {
      const params = {
        projectId: data.projectId,
        newPwd: value
      }
      resetPasswordApi(params)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Password of ${data.projectName} reset`,
              type: "success"
            })
          }
        })
        .catch((err) => {
          console.log("err :>> ", err)
        })
    })
    .catch(() => console.log("catch..."))
}

// Edit
const handleEdit = (data: ICmsProject) => {
  resetEntity()
  entityForm.value = cloneDeep(data)
  dialogTitle.value = "Edit"
  saveFlag.value = false
  dialogVisible.value = true
}

// Fetch department data
const getDeptTreeData = () => {
  loading.value = true
  treeDeptApi()
    .then((resp) => {
      deptTree.value = resp.data
    })
    .catch((err) => {
      console.log("err :>> ", err)
    })
    .finally(() => {
      loading.value = false
    })
}

// Delete
const handleDelete = (data: ICmsProject) => {
  ElMessageBox.prompt(`Enter the project password below`, `Delete ${data.projectName}?`, {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    inputPattern: /^\w{5,24}$/,
    inputErrorMessage: "Password must contain letters and numbers, 5 to 24 characters",
    type: "warning"
  })
    .then((resp) => {
      deleteProjectApi(data.projectId, resp.value)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Deleted ${data.projectName}`,
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
        saveProjectApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Project added",
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
        updateProjectApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Project updated",
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
  getDeptTreeData()
})
</script>

<style lang="scss" scoped>
.el-tag {
  margin: 2px;
}
</style>
