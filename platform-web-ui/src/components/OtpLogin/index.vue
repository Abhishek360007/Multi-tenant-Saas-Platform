<script setup lang="ts">
import { onBeforeUnmount, reactive, ref } from "vue"
import { type FormInstance, type FormRules, ElMessage } from "element-plus"
import { Message, Iphone, Key, Picture, Loading } from "@element-plus/icons-vue"
import { v4 as uuidv4 } from "uuid"
import { getEnvBaseURL } from "@/utils"
import { isEmail } from "@/utils/validate"
import { sendOtpApi, type OtpChannel } from "@/api/login"
import { useUserStoreHook } from "@/store/modules/user"

/**
 * Sign in with a one-time code sent by email or SMS.
 * tenantId is sent with the requests when the login page knows it (tenant portal).
 */
const props = defineProps<{ tenantId?: string }>()
const emit = defineEmits<{ (e: "success"): void }>()

const userStore = useUserStoreHook()
const formRef = ref<FormInstance | null>(null)
const sending = ref(false)
const loading = ref(false)
const countdown = ref(0)
let timer: ReturnType<typeof setInterval> | undefined

const form = reactive({
  channel: "EMAIL" as OtpChannel,
  target: "",
  codeKey: uuidv4().replace(/-/g, ""),
  codeText: "",
  otp: ""
})

const codeUrl = ref("")
const refreshCaptcha = () => {
  form.codeKey = uuidv4().replace(/-/g, "")
  form.codeText = ""
  codeUrl.value = `${getEnvBaseURL()}/auth/captcha/codes?codeKey=${form.codeKey}&r=${Math.random()}`
}
refreshCaptcha()

const normalizeMobile = (v: string) => {
  let d = v.replace(/\D/g, "")
  if (d.length === 12 && d.startsWith("91")) d = d.slice(2)
  if (d.length === 11 && d.startsWith("0")) d = d.slice(1)
  return d
}

const targetValidator = (_rule: any, value: string, callback: (e?: Error) => void) => {
  if (form.channel === "EMAIL") return isEmail(value) ? callback() : callback(new Error("Enter a valid email address"))
  return /^[6-9]\d{9}$/.test(normalizeMobile(value))
    ? callback()
    : callback(new Error("Enter a valid 10-digit mobile number"))
}

const rules: FormRules = {
  target: [{ validator: targetValidator, trigger: "blur" }],
  codeText: [{ required: true, len: 4, message: "Enter the 4-character captcha", trigger: "blur" }],
  otp: [{ required: true, pattern: /^\d{4,8}$/, message: "Enter the code you received", trigger: "blur" }]
}

const startCountdown = () => {
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0 && timer) clearInterval(timer)
  }, 1000)
}
onBeforeUnmount(() => timer && clearInterval(timer))

const sendCode = () => {
  formRef.value?.validateField(["target", "codeText"], (valid: boolean) => {
    if (!valid) return
    sending.value = true
    sendOtpApi({
      channel: form.channel,
      target: form.target,
      codeKey: form.codeKey,
      codeText: form.codeText,
      tenantId: props.tenantId
    })
      .then((resp) => {
        ElMessage.success(resp.message || "Code sent")
        startCountdown()
      })
      .catch(() => {})
      .finally(() => {
        sending.value = false
        // A captcha can only be used once
        refreshCaptcha()
      })
  })
}

const signIn = () => {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return
    loading.value = true
    userStore
      .userOtpLogin({ channel: form.channel, target: form.target, otp: form.otp, tenantId: props.tenantId })
      .then(() => emit("success"))
      .catch(() => {})
      .finally(() => (loading.value = false))
  })
}

const onChannelChange = () => {
  form.target = ""
  form.otp = ""
  formRef.value?.clearValidate()
}
</script>

<template>
  <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="signIn">
    <el-form-item>
      <el-radio-group v-model="form.channel" @change="onChannelChange" size="large" class="channel">
        <el-radio-button value="EMAIL">Email</el-radio-button>
        <el-radio-button value="SMS">Mobile (SMS)</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item prop="target">
      <!-- Separate inputs: el-input only detects a prepend slot when it is created -->
      <el-input
        v-if="form.channel === 'EMAIL'"
        v-model.trim="form.target"
        placeholder="Email address"
        :prefix-icon="Message"
        size="large"
        clearable
      />
      <el-input
        v-else
        v-model.trim="form.target"
        placeholder="10-digit mobile number"
        :prefix-icon="Iphone"
        size="large"
        clearable
      >
        <template #prepend>+91</template>
      </el-input>
    </el-form-item>
    <el-form-item prop="codeText">
      <el-input v-model.trim="form.codeText" placeholder="Captcha" :prefix-icon="Key" maxlength="4" size="large">
        <template #append>
          <el-image :src="codeUrl" fit="fill" @click="refreshCaptcha" class="code-url" draggable="false">
            <template #placeholder>
              <el-icon><Picture /></el-icon>
            </template>
            <template #error>
              <el-icon><Loading /></el-icon>
            </template>
          </el-image>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="otp">
      <el-input v-model.trim="form.otp" placeholder="One-time code" maxlength="8" size="large" inputmode="numeric">
        <template #append>
          <el-button :loading="sending" :disabled="countdown > 0" @click="sendCode">
            {{ countdown > 0 ? `Resend in ${countdown}s` : "Send code" }}
          </el-button>
        </template>
      </el-input>
    </el-form-item>
    <el-button :loading="loading" type="primary" size="large" class="submit" @click.prevent="signIn">Sign in</el-button>
  </el-form>
</template>

<style lang="scss" scoped>
.channel {
  width: 100%;
  :deep(.el-radio-button) {
    flex: 1;
  }
  :deep(.el-radio-button__inner) {
    width: 100%;
  }
}
// Captcha size comes from the login page styles, same as the password tab
.code-url {
  border: #dcdfe6 2px solid;
}
:deep(.el-input-group__append) {
  padding: 0;
  overflow: hidden;
  .el-button {
    margin: 0;
    padding: 0 16px;
  }
}
.submit {
  width: 100%;
  margin-top: 10px;
}
</style>
