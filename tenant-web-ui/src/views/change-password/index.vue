<script lang="ts" setup>
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { type FormInstance, type FormRules, ElMessage } from "element-plus"
import { Lock } from "@element-plus/icons-vue"
import { useUserStoreHook } from "@/store/modules/user"
import { updatePasswordApi } from "@/api/user"
import { isPassword } from "@/utils/validate"
import ThemeSwitch from "@/components/ThemeSwitch/index.vue"

/**
 * Shown right after login when an administrator set this account's password.
 * The router guard keeps the user here, and the API refuses other calls, until a new password is set.
 */
const router = useRouter()
const userStore = useUserStoreHook()
const appTitle = import.meta.env.VITE_APP_TITLE

const formRef = ref<FormInstance | null>(null)
const loading = ref(false)
const form = reactive({ oldPwd: "", newPwd: "", confirmPwd: "" })

const newPwdValidator = (_rule: any, value: string, callback: (e?: Error) => void) => {
  if (!isPassword(value)) return callback(new Error("Use 5-32 letters and numbers, with at least one of each"))
  if (value === form.oldPwd) return callback(new Error("The new password must be different from the current one"))
  callback()
}

const confirmPwdValidator = (_rule: any, value: string, callback: (e?: Error) => void) => {
  value === form.newPwd ? callback() : callback(new Error("Passwords do not match"))
}

const rules: FormRules = {
  oldPwd: [{ required: true, message: "Enter your current password", trigger: "blur" }],
  newPwd: [
    { required: true, message: "Enter a new password", trigger: "blur" },
    { validator: newPwdValidator, trigger: "blur" }
  ],
  confirmPwd: [
    { required: true, message: "Confirm the new password", trigger: "blur" },
    { validator: confirmPwdValidator, trigger: "blur" }
  ]
}

const onSubmit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return
    loading.value = true
    updatePasswordApi({ ...form })
      .then(async () => {
        ElMessage.success("Password changed. Please sign in with your new password.")
        await userStore.userLogout()
        router.replace("/login")
      })
      .catch(() => {})
      .finally(() => (loading.value = false))
  })
}

const onLogout = async () => {
  await userStore.userLogout()
  router.replace("/login")
}
</script>

<template>
  <div class="change-password-container">
    <ThemeSwitch class="theme-switch" />
    <div class="change-password-card">
      <div class="title">{{ appTitle }}</div>
      <div class="content">
        <h3>Set a new password</h3>
        <p class="hint">
          Your password was set by an administrator. Choose your own password to continue.
        </p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="onSubmit">
          <el-form-item label="Current password" prop="oldPwd">
            <el-input v-model.trim="form.oldPwd" :prefix-icon="Lock" type="password" show-password size="large" />
          </el-form-item>
          <el-form-item label="New password" prop="newPwd">
            <el-input v-model.trim="form.newPwd" :prefix-icon="Lock" type="password" show-password size="large" />
          </el-form-item>
          <el-form-item label="Confirm new password" prop="confirmPwd">
            <el-input v-model.trim="form.confirmPwd" :prefix-icon="Lock" type="password" show-password size="large" />
          </el-form-item>
          <el-button :loading="loading" type="primary" size="large" class="submit" @click.prevent="onSubmit">
            Change password
          </el-button>
          <el-button link class="logout" @click="onLogout">Sign out</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.change-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-height: 100%;
  padding: 16px;
  box-sizing: border-box;
  .theme-switch {
    position: fixed;
    top: 5%;
    right: 5%;
    cursor: pointer;
  }
  .change-password-card {
    width: 480px;
    max-width: 100%;
    border-radius: 20px;
    box-shadow: 0 0 10px #dcdfe6;
    background-color: var(--el-bg-color);
    overflow: hidden;
    .title {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 110px;
      font-size: 26px;
      font-weight: 600;
    }
    .content {
      padding: 0 40px 32px;
      h3 {
        margin: 0 0 8px;
      }
      .hint {
        margin: 0 0 20px;
        color: var(--el-text-color-secondary);
        line-height: 1.5;
      }
      .submit {
        width: 100%;
        margin-top: 8px;
      }
      .logout {
        display: block;
        margin: 12px auto 0;
      }
    }
  }
}
</style>
