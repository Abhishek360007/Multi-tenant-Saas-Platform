<template>
  <div class="app-container" v-loading="loading" v-has="['pre_user:view']">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item label="Department">
          <CascaderDept v-model="params.deptId" :options="deptTree" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by username or nickname" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchTableData">Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" :icon="Plus" v-has="['pre_user:create']" @click="handleAdd()">Add</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table :data="tableData">
          <el-table-column prop="userId" label="User ID" />
          <el-table-column prop="avatar" label="Avatar">
            <template #default="scope">
              <el-avatar :size="40" :icon="UserFilled" :src="scope.row.avatar" />
            </template>
          </el-table-column>
          <el-table-column prop="nickname" label="Nickname" />
          <el-table-column prop="username" label="Username" />
          <el-table-column prop="mobile" label="Mobile number" />
          <el-table-column prop="gender" label="Gender">
            <template #default="scope">
              {{ dictStore.getUserSex(scope.row.gender) }}
            </template>
          </el-table-column>
          <el-table-column prop="roles" label="Role" width="150">
            <template #default="scope">
              <el-tooltip
                v-for="(item, index) in scope.row.roles"
                :key="index"
                effect="dark"
                :content="item.description"
                placement="top-start"
              >
                <el-tag>{{ showStringOverflow(item.roleName, 10) }}</el-tag>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="roles" label="Department" width="120">
            <template #default="scope">
              <el-popover placement="top-start" :width="230">
                <template #reference>
                  <el-tag>{{ showStringOverflow(scope.row.dept.deptName, 10) }}</el-tag>
                </template>
                <div><strong>Department leader:</strong> &nbsp;&nbsp;&nbsp;{{ scope.row.dept.leader }}</div>
                <div><strong>Leader mobile number:</strong> &nbsp;&nbsp;&nbsp;{{ scope.row.dept.leaderMobile }}</div>
                <div><strong>Department description:</strong> &nbsp;&nbsp;&nbsp;{{ scope.row.dept.description }}</div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="adminFlag" label="Admin">
            <template #default="scope">
              <el-tag v-if="scope.row.adminFlag" type="success">Yes</el-tag>
              <span v-else>No</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status">
            <template #default="scope">
              <el-link v-if="scope.row.loginId" @click="showUserLoginLog(scope.row.loginId)" type="success"
                >Online</el-link
              >
              <span v-else>{{ dictStore.getNormalDisable(scope.row.status) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="expired" label="Expiry time">
            <template #default="scope">
              <el-tooltip v-if="scope.row.expired" :content="scope.row.expired" placement="top">
                <el-link> {{ showExpiredDateAgo(scope.row.expired) }}</el-link>
              </el-tooltip>
              <span v-else>Never expires</span>
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
              <el-link :icon="Edit" type="warning" v-has="['pre_user:update']" @click="handleEdit(scope.row)"
                >Edit</el-link
              >
              &nbsp;
              <el-dropdown
                @command="(cmd: string) => handleCommand(cmd, scope.row)"
                v-has="['pre_user:reset:password', 'pre_user:delete']"
              >
                <span class="el-dropdown-link">
                  More
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <div v-has="['pre_user:reset:password']">
                      <el-dropdown-item :icon="Link" command="ResetPassword">Reset password</el-dropdown-item>
                    </div>
                    <div v-if="scope.row.adminFlag !== 1" v-has="['pre_user:delete']">
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

    <!-- Login log dialog -->
    <el-dialog v-model="userLoginLog.dialogVisible" width="50%">
      <UserLoginLog :key="userLoginLog.loginId" :value="userLoginLog.loginId" />
    </el-dialog>

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
            <el-form-item prop="avatar" label="Avatar">
              <user-avatar :key="entityForm.avatar" :src="entityForm.avatar" @change="getAvatar" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="username" label="Username">
              <el-input
                v-model.trim="entityForm.username"
                placeholder="Username"
                type="text"
                tabindex="1"
                :disabled="!saveFlag"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="password" label="Password" v-if="saveFlag">
              <el-input
                v-model.trim="entityForm.password"
                placeholder="Password"
                type="password"
                tabindex="2"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="mobile" label="Mobile number">
              <el-input
                v-model.trim="entityForm.mobile"
                placeholder="Mobile number"
                type="text"
                maxlength="11"
                show-word-limit
                tabindex="3"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="email" label="Email">
              <el-input v-model.trim="entityForm.email" placeholder="Email" type="text" tabindex="4" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="nickname" label="Nickname">
              <el-input v-model.trim="entityForm.nickname" placeholder="Nickname" type="text" tabindex="5" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="gender" label="Gender">
              <select-dict-data v-model:value="entityForm.gender" dictTypeKey="sys_user_sex" />
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
              <CascaderDept v-model="entityForm.deptId" :options="deptTree" tabindex="6" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="roles" label="Role">
              <el-select v-model="entityRoles" filterable clearable multiple placeholder="Select roles">
                <el-option v-for="item in roleList" :key="item.roleId" :label="item.roleName" :value="item.roleId">
                  <span style="float: left">{{ item.roleId }}</span>
                  <span style="float: right; font-size: 13px">{{ showStringOverflow(item.roleName) }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="expired" label="Expired">
              <el-date-picker
                v-model="entityForm.expired"
                type="datetime"
                placeholder="Select an expiry time; leave empty to never expire"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                :shortcuts="futureShortcuts"
              />
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
import { ref, reactive, onMounted, computed } from "vue"
import { deleteUserApi, queryUserApi, resetPasswordApi, saveUserApi, updateUserApi } from "@/api/user"
import { treeDeptApi } from "@/api/dept"
import { listRoleApi } from "@/api/role"
import { IPmsUser, IPmsDept, IPmsRole } from "@/types/pms"
import UserAvatar from "@/components/UserAvatar/index.vue"
import UserLoginLog from "@/components/UserLoginLog/index.vue"
import CascaderDept from "@/components/CascaderDept/index.vue"
import { isEmail, isPassword, isPhone } from "@/utils/validate"
import { Plus, Edit, Delete, UserFilled, Search, Link } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus"
import { useDictStoreHook } from "@/store/modules/dict"
import { futureShortcuts } from "@/utils"
import { showExpiredDateAgo, showStringOverflow } from "@/hooks/useIndex"
import { cloneDeep } from "lodash-es"

const dictStore = useDictStoreHook()

// Show login log
const userLoginLog = ref({
  dialogVisible: false,
  loginId: ""
})

const showUserLoginLog = (loginId: string) => {
  userLoginLog.value.loginId = loginId
  userLoginLog.value.dialogVisible = true
}

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<IPmsUser[]>([])

const deptTree = ref<IPmsDept[]>([])

const roleList = ref<IPmsRole[]>([])

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  deptId: 0,
  keywords: "",
  includeCauu: true,
  includeRad: true
})

/// Form data
const entityForm = ref<IPmsUser>({
  userId: 0,
  avatar: "",
  username: "",
  mobile: "",
  email: "",
  nickname: "",
  password: "",
  gender: 0,
  adminFlag: 0,
  status: 1,
  deptId: 0,
  expired: "",
  dept: null,
  roles: [],
  loginId: ""
})

const emailValidator = (rule: any, value: any, callback: any) => {
  if (!isEmail(value)) {
    callback(new Error("Invalid email address!"))
  } else {
    callback()
  }
}

const phoneValidator = (rule: any, value: any, callback: any) => {
  if (!isPhone(value)) {
    callback(new Error("Invalid mobile number!"))
  } else {
    callback()
  }
}

const passwordValidator = (rule: any, value: any, callback: any) => {
  if (!isPassword(value)) {
    callback(new Error("At least 1 letter and longer than 5 characters"))
  } else {
    callback()
  }
}

const entityFormRef = ref<FormInstance | null>(null)

/// Form validation rules
const entityFormRules: FormRules = {
  avatar: [{ required: true, message: "An avatar is required", trigger: "blur" }],
  username: [
    { required: true, message: "Enter a username", trigger: "blur" },
    { min: 5, max: 32, message: "Must be longer than 5 characters", trigger: "blur" }
  ],
  mobile: [
    { required: true, message: "Enter a mobile number", trigger: "blur" },
    { validator: phoneValidator, trigger: "blur" }
  ],
  email: [
    { required: true, message: "Enter an email", trigger: "blur" },
    { validator: emailValidator, trigger: "blur" }
  ],
  nickname: [{ required: true, message: "Enter a nickname", trigger: "blur" }],
  password: [
    { required: true, message: "Enter a password", trigger: "blur" },
    { validator: passwordValidator, trigger: "blur" }
  ],
  gender: [{ required: true, message: "Select a gender", trigger: "blur" }],
  status: [{ required: true, message: "Select a status", trigger: "blur" }],
  deptId: [{ required: true, message: "Select a department", trigger: "blur" }],
  roles: [{ required: true, message: "Select a role", trigger: "blur" }]
}

const getTableData = () => {
  loading.value = true
  queryUserApi(params)
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
    userId: 0,
    avatar: "",
    username: "",
    mobile: "",
    email: "",
    nickname: "",
    password: "",
    gender: 0,
    adminFlag: 0,
    status: 1,
    deptId: 0,
    expired: "",
    dept: null,
    roles: [],
    loginId: ""
  }
}

const getAvatar = (data: string) => {
  entityForm.value.avatar = data
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

const entityRoles = computed({
  get() {
    if (entityForm.value.roles) {
      return entityForm.value.roles.map((role) => role.roleId)
    } else {
      return []
    }
  },
  set(data: number[]) {
    entityForm.value.roles = data.map((id) => {
      const role: IPmsRole = {
        roleId: id,
        roleName: "",
        sort: 0,
        description: "",
        dataScope: 0,
        deptIds: []
      }
      return role
    })
  }
})

const handleCommand = (command: string, data: IPmsUser) => {
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
const handleResetPassword = (data: IPmsUser) => {
  ElMessageBox.prompt(`Reset the password of ${data.nickname}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    inputPattern: /^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]{5,24}$/,
    inputErrorMessage: "Password must contain letters and numbers, 5 to 24 characters"
  })
    .then(({ value }) => {
      const params = {
        userId: data.userId,
        newPwd: value
      }
      resetPasswordApi(params)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: `Password of ${data.nickname} reset`,
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
const handleEdit = (data: IPmsUser) => {
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

// Fetch role data
const getListRoleData = () => {
  loading.value = true
  listRoleApi()
    .then((resp) => {
      roleList.value = resp.data
    })
    .catch((err) => {
      console.log("err :>> ", err)
    })
    .finally(() => {
      loading.value = false
    })
}

// Delete
const handleDelete = (data: IPmsUser) => {
  ElMessageBox.confirm(`Delete ${data.nickname}?`, "Warning", {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    type: "warning"
  })
    .then(() => {
      deleteUserApi(data.userId)
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

// Add and edit
const handleSaveAndFlush = () => {
  entityFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      if (saveFlag.value) {
        saveUserApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "User added",
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
        updateUserApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "User updated",
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
  getListRoleData()
})
</script>

<style lang="scss" scoped>
.el-tag {
  margin: 2px;
}
</style>
