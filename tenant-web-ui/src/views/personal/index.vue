<template>
  <div class="app-container">
    <el-row :gutter="10">
      <el-col :span="6">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>Profile</span>
            </div>
          </template>
          <div class="basic-info">
            <div class="text-center">
              <user-avatar :src="userInfoForm.avatar" @change="getAvatar" />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <el-icon><Connection /></el-icon>
                User ID
                <div class="pull-right">{{ userInfoForm.userId }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Filter /></el-icon>
                Nickname
                <div class="pull-right">{{ userInfoForm.nickname }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><User /></el-icon>
                Username
                <div class="pull-right">{{ userInfoForm.username }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Calendar /></el-icon>
                Email
                <div class="pull-right">{{ userInfoForm.email }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Phone /></el-icon>
                Phone
                <div class="pull-right">{{ userInfoForm.mobile }}</div>
              </li>
              <li class="list-group-item">
                <el-icon><Brush /></el-icon>
                Role
                <div class="pull-right">
                  <el-tag v-for="(role, index) in userInfoForm.roles" :key="index" type="success"
                    >{{ role.description }}
                  </el-tag>
                </div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>Basic information</span>
            </div>
          </template>
          <div class="basic-info">
            <el-tabs v-model="activeName" class="demo-tabs">
              <el-tab-pane label="Basic information" name="first">
                <el-form
                  ref="userInfoFormRef"
                  :model="userInfoForm"
                  :rules="userInfoFormRules"
                  label-width="80px"
                  v-loading="loading"
                  class="user-form"
                >
                  <el-form-item label="Nickname" prop="nickname">
                    <el-input v-model.trim="userInfoForm.nickname" placeholder="Nickname" tabindex="1" />
                  </el-form-item>
                  <el-form-item label="Email" prop="email">
                    <el-input v-model.trim="userInfoForm.email" placeholder="Email" tabindex="2" />
                  </el-form-item>
                  <el-form-item label="Mobile number" prop="mobile">
                    <el-input
                      v-model.trim="userInfoForm.mobile"
                      placeholder="Mobile number"
                      type="text"
                      maxlength="11"
                      show-word-limit
                      tabindex="3"
                    />
                  </el-form-item>
                  <el-form-item label="Gender" prop="gender">
                    <select-dict-data v-model:value="userInfoForm.gender" dictTypeKey="sys_user_sex" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" v-preventReClick @click="onUpdateUserInfo">Save changes</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
              <el-tab-pane label="Change password" name="second">
                <el-form
                  ref="updatePwdFormRef"
                  :model="updatePwdForm"
                  :rules="updatePwdFormRules"
                  label-width="80px"
                  v-loading="loading"
                  class="user-form"
                >
                  <el-form-item label="Current password" prop="oldPwd">
                    <el-input v-model="updatePwdForm.oldPwd" show-password />
                  </el-form-item>
                  <el-form-item label="New password" prop="newPwd">
                    <el-input v-model="updatePwdForm.newPwd" show-password />
                  </el-form-item>
                  <el-form-item label="Confirm password" prop="confirmPwd">
                    <el-input v-model="updatePwdForm.confirmPwd" show-password />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" v-preventReClick @click="onUpdatePassword">Save changes</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup name="Personal">
import { useUserStore } from "@/store/modules/user"
import UserAvatar from "@/components/UserAvatar/index.vue"
import { ref, reactive } from "vue"
import { type FormInstance, FormRules, ElMessageBox } from "element-plus"
import { updatePasswordApi, updateUserInfoApi } from "@/api/user"
import { isEmail, isPassword, isPhone } from "@/utils/validate"
import { useRouter } from "vue-router"
import { ILoginUserInfo } from "@/types/pms"
const router = useRouter()

const activeName = ref("first")

const loading = ref(false)

const userStore = useUserStore()

const userInfoFormRef = ref<FormInstance | null>(null)

const userInfoForm = reactive<ILoginUserInfo>(userStore.userInfo!)

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

/** Login form validation rules */
const userInfoFormRules: FormRules = {
  nickname: [
    { required: true, message: "Enter a nickname", trigger: "blur" },
    { min: 5, max: 32, message: "Must be longer than 5 characters", trigger: "blur" }
  ],
  email: [
    { required: true, message: "Enter an email", trigger: "blur" },
    { validator: emailValidator, trigger: "blur" }
  ],
  mobile: [
    { required: true, message: "Enter a mobile number", trigger: "blur" },
    { validator: phoneValidator, trigger: "blur" }
  ],
  gender: [{ required: true, message: "Select a gender", trigger: "blur" }]
}

const getAvatar = (data: string) => {
  userInfoForm.avatar = data
}

const onUpdateUserInfo = () => {
  userInfoFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      ElMessageBox.confirm("Save these changes?", "Warning", {
        confirmButtonText: "OK",
        cancelButtonText: "Cancel",
        type: "warning"
      })
        .then(() => {
          loading.value = true
          const params = {
            userId: userInfoForm.userId,
            username: userInfoForm.username,
            mobile: userInfoForm.mobile,
            avatar: userInfoForm.avatar,
            nickname: userInfoForm.nickname,
            gender: userInfoForm.gender,
            email: userInfoForm.email
          }
          updateUserInfoApi(params)
            .then(() => userStore.getUserInfo())
            .catch((err) => {
              console.log("err", err)
            })
            .finally(() => {
              loading.value = false
            })
        })
        .catch(() => {
          loading.value = false
        })
    } else {
      loading.value = false
      return false
    }
  })
}

//-------------- Change password ---------------

const updatePwdFormRef = ref<FormInstance | null>(null)

const updatePwdForm = reactive({
  oldPwd: "",
  newPwd: "",
  confirmPwd: ""
})

const newPwdValidator = (rule: any, value: string, callback: any) => {
  if (!isPassword(value)) {
    callback(new Error("Password must contain letters and numbers!"))
  } else {
    callback()
  }
}

const confirmPwdValidator = (rule: any, value: string, callback: any) => {
  if (!isPassword(value)) {
    callback(new Error("Password must contain letters and numbers!"))
  } else {
    if (value === updatePwdForm.newPwd) {
      callback()
    } else {
      callback(new Error("Passwords do not match!"))
    }
  }
}

const updatePwdFormRules: FormRules = {
  oldPwd: [
    { required: true, message: "Enter your current password", trigger: "blur" },
    { min: 5, max: 32, message: "Must be at least 5 characters", trigger: "blur" }
  ],
  newPwd: [
    { required: true, message: "Enter a new password", trigger: "blur" },
    { validator: newPwdValidator, trigger: "blur" }
  ],
  confirmPwd: [
    { required: true, message: "Confirm the new password", trigger: "blur" },
    { validator: confirmPwdValidator, trigger: "blur" }
  ],
  gender: [{ required: true, message: "Select a gender", trigger: "blur" }]
}

const onUpdatePassword = () => {
  updatePwdFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      ElMessageBox.confirm("Change your password? You will need to sign in again.", "Warning", {
        confirmButtonText: "OK",
        cancelButtonText: "Cancel",
        type: "warning"
      })
        .then(() => {
          const params = {
            userId: userInfoForm.userId,
            oldPwd: updatePwdForm.oldPwd,
            newPwd: updatePwdForm.newPwd,
            confirmPwd: updatePwdForm.confirmPwd
          }
          updatePasswordApi(params)
            .then(async () => {
              await userStore.userLogout()
              router.push("/login")
            })
            .catch((err) => {
              console.log("err", err)
            })
            .finally(() => {
              loading.value = false
            })
        })
        .catch(() => {
          loading.value = false
        })
    } else {
      loading.value = false
      return false
    }
  })
}
</script>

<style lang="scss" scoped>
.list-group {
  padding-left: 0;
  list-style: none;
}

.list-group-striped {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
  padding-left: 0;
  padding-right: 0;
}

.list-group-item {
  border-bottom: 1px solid #e7eaec;
  border-top: 1px solid #e7eaec;
  margin-bottom: -1px;
  padding: 11px 0;

  .svg-icon {
    margin-right: 10px;
  }

  i {
    margin-right: 10px;
  }
}

.pull-right {
  float: right !important;
}

.text-center {
  text-align: center;
}

.basic-info {
  height: 500px;
}

.user-form {
  .el-input {
    width: 300px;
  }

  .el-select {
    width: 300px;
  }
}
</style>
