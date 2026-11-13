<script lang="ts" setup>
import { reactive, ref } from "vue"
import { useRouter, useRoute } from "vue-router"
import { useUserStoreHook } from "@/store/modules/user"
import { type FormInstance, type FormRules } from "element-plus"
import { User, Lock, Key, Picture, Loading } from "@element-plus/icons-vue"
import ThemeSwitch from "@/components/ThemeSwitch/index.vue"
import { v4 as uuidv4 } from "uuid"
import { getEnvBaseURL } from "@/utils"
import {
  getUserAndPassword,
  setUserAndPassword,
  removeUserAndPassword,
  setLastTenantId,
  getLastTenantId
} from "@/utils/cache/local-storage"
import { type ILoginData } from "@/types/pms"
import { merge, clone } from "lodash-es"
import { useProjectStoreHook } from "@/store/modules/project"
import { getSimpleTenantApi } from "@/api/tenant"
import { ISimpleTenant } from "@/types/base"
import logoPng from "@/assets/layouts/logo.png?url"
import { onMounted } from "vue"

const useUserStore = useUserStoreHook()

const router = useRouter()

/** Sign-in form ref */
const loginFormRef = ref<FormInstance | null>(null)

/** Sign-in button Loading */
const loading = ref(false)

/** Title */
const appTitle = import.meta.env.VITE_APP_TITLE

/** Captcha image URL */
const codeUrl = ref("")

const target: ILoginData = {
  tenantId: "",
  username: "",
  password: "",
  codeKey: uuidv4().replace(/-/g, ""),
  codeText: "",
  rememberMe: false
}
const source = getUserAndPassword()
if (source) {
  source.codeKey = target.codeKey
  source.codeText = ""
  merge(target, source)
}
/** Sign-in form data */
const loginFormData: ILoginData = reactive(target)

/** Login form validation rules */
const loginFormRules: FormRules = {
  tenantId: [
    { required: true, message: "Enter a tenant ID", trigger: "blur" },
    { min: 4, max: 12, message: "Must be 4 to 12 characters", trigger: "blur" }
  ],
  username: [
    { required: true, message: "Enter your username", trigger: "blur" },
    { min: 5, max: 32, message: "Must be 5 to 32 characters", trigger: "blur" }
  ],
  password: [
    { required: true, message: "Enter your password", trigger: "blur" },
    { min: 5, max: 32, message: "Must be 5 to 32 characters", trigger: "blur" }
  ],
  codeText: [
    { required: true, message: "Enter the captcha", trigger: "blur" },
    { min: 4, max: 4, message: "Must be 4 characters", trigger: "blur" }
  ]
}

/** Sign-in logic */
const handleLogin = () => {
  loginFormRef.value?.validate((valid: boolean, fields) => {
    if (valid) {
      loading.value = true
      useUserStore.userLogin(loginFormData)
        .then(() => {
          // Get the tenant ID from the last sign-in; if it differs, clear the project cache
          if (getLastTenantId() !== loginFormData.tenantId) {
            // Reset the project to the default
            useProjectStoreHook().resetCurrentProject()
          }
          // Remember the password after signing in ?
          if (loginFormData.rememberMe) {
            const saveUserInfo = clone(loginFormData)
            setUserAndPassword(saveUserInfo)
          } else {
            removeUserAndPassword()
          }
          setLastTenantId(loginFormData.tenantId)
          router.push({ path: "/" })
        })
        .catch(() => {
          createCode()
        })
        .finally(() => {
          loading.value = false
        })
    } else {
      console.error("Form validation failed", fields)
    }
  })
}

/** Create a captcha */
const createCode = () => {
  // Clear the captcha input first
  loginFormData.codeText = ""
  // Get captcha
  codeUrl.value = `${getEnvBaseURL()}/auth/captcha/codes?codeKey=${loginFormData.codeKey}&r=${Math.random()}`
}

/** Check whether the tenant ID exists */
const getSimpleTenant = async (tenantId: string) => {
  const { data } = await getSimpleTenantApi(tenantId)
  return data
}

/** Initialize the captcha */
createCode()

onMounted(() => {
  // Initialize and fetch the public key
  useUserStore.getPublicKey()
})

const route = useRoute()
const tenantId = ref()

if (route.query.tenantId) {
  // Read the tenant from the URL parameters ID http://tenanthub.example.com/tenant/#/login?tenantId=google
  tenantId.value = route.query.tenantId as string
} else {
  // If not in the URL parameters, take it from the subdomain
  // e.g. take it from http://acme.tenanthub.example.com/tenant/#/login google
  const domain = location.hostname
  const idx1 = domain.indexOf(".")
  tenantId.value = domain.substring(0, idx1)
}

const simpleTenant = ref<ISimpleTenant>()

if (tenantId.value) {
  getSimpleTenant(tenantId.value).then((data) => {
    if (data) {
      target.tenantId = tenantId.value
      simpleTenant.value = data
    } else {
      tenantId.value = null
    }
  })
}
</script>

<template>
  <div class="login-container">
    <ThemeSwitch class="theme-switch" />
    <div class="login-card">
      <div class="head">
        <div class="img">
          <el-avatar v-if="simpleTenant" :size="80" :src="simpleTenant.logo" />
          <el-avatar v-else :size="80" :src="logoPng" />
        </div>
        <div class="title">
          <template v-if="simpleTenant">
            {{ appTitle.replaceAll("TenantHub", `${simpleTenant.name} `) }}
          </template>
          <template v-else>
            {{ appTitle }}
          </template>
        </div>
      </div>
      <div class="content">
        <el-form ref="loginFormRef" :model="loginFormData" :rules="loginFormRules" @keyup.enter="handleLogin">
          <el-form-item prop="tenantId" v-if="!tenantId">
            <el-input
              v-model.trim="loginFormData.tenantId"
              placeholder="Tenant Id"
              type="text"
              tabindex="1"
              size="large"
              clearable
            >
              <template #prefix>
                <SvgIcon name="peoples" />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="username">
            <el-input
              v-model.trim="loginFormData.username"
              placeholder="Username"
              type="text"
              tabindex="2"
              :prefix-icon="User"
              size="large"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model.trim="loginFormData.password"
              placeholder="Password"
              type="password"
              tabindex="3"
              :prefix-icon="Lock"
              size="large"
              clearable
              show-password
            />
          </el-form-item>
          <el-form-item prop="codeText">
            <el-input
              v-model.trim="loginFormData.codeText"
              placeholder="Captcha"
              type="text"
              tabindex="4"
              :prefix-icon="Key"
              maxlength="4"
              size="large"
              clearable
            >
              <template #append>
                <el-image :src="codeUrl" @click="createCode" class="code-url" draggable="false">
                  <template #placeholder>
                    <el-icon>
                      <Picture />
                    </el-icon>
                  </template>
                  <template #error>
                    <el-icon>
                      <Loading />
                    </el-icon>
                  </template>
                </el-image>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="rememberMe">
            <el-checkbox v-model="loginFormData.rememberMe" label="Remember me" size="large" />
          </el-form-item>
          <el-button :loading="loading" type="primary" size="large" @click.prevent="handleLogin">Sign in</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-height: 100%;
  background: url("@/assets/login-bg1.svg") no-repeat;

  .theme-switch {
    position: fixed;
    top: 5%;
    right: 5%;
    cursor: pointer;
  }

  .login-card {
    width: 430px;
    border-radius: 5px;
    box-shadow: 0 0 10px #dcdfe6;
    background-color: #fff;
    overflow: hidden;

    .head {
      text-align: center;

      .img {
        margin: 10px 0px;
      }

      .title {
        font-weight: 600;
        font-size: larger;
        margin-bottom: 10px;
      }
    }

    .content {
      padding: 20px 30px 30px 30px;

      :deep(.el-input-group__append) {
        padding: 0;
        overflow: hidden;

        .el-image {
          width: 100px;
          height: 40px;
          border-left: 0px;
          user-select: none;
          cursor: pointer;
          text-align: center;
        }
      }

      .el-button {
        width: 100%;
        margin-top: 10px;
      }
    }
  }

  .code-url {
    border: #dcdfe6 2px solid;
  }
}
</style>
