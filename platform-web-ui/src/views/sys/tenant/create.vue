<template>
  <el-steps :active="active" finish-status="success">
    <el-step title="Tenant details" />
    <el-step title="Admin details" />
    <el-step title="Result" />
  </el-steps>
  <br />
  <br />
  <el-form ref="entityFormRef" :model="entityForm" :rules="entityFormRules" label-position="right" label-width="140px">
    <template v-if="active === 0">
      <el-alert title="Logo Recommended: a 100x100 px PNG..." type="info" />
      <br />
      <el-form-item prop="logo" label="Logo">
        <image-upload :key="entityForm.logo" :src="entityForm.logo" @change="getLogoSrc" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="tenantId" label="Tenant ID">
            <el-input
              v-model.trim="entityForm.tenantId"
              clearable
              placeholder="Tenant ID"
              type="text"
              tabindex="1"
              onkeyup="value=value.replace(/[^\w]/g,'')"
              maxlength="12"
              show-word-limit
            >
              <template #append>
                <el-button :icon="Refresh" @click="refreshTenantId" />
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12" />
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="name" label="Name">
            <el-input v-model.trim="entityForm.name" clearable placeholder="Tenant name" type="text" tabindex="2" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="email" label="Email">
            <el-input v-model.trim="entityForm.email" clearable placeholder="Tenant email" type="text" tabindex="3" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="linkman" label="Contact person">
            <el-input v-model="entityForm.linkman" clearable placeholder="Tenant contact" type="text" tabindex="4" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="contactNumber" label="Contact details">
            <el-input
              v-model.trim="entityForm.contactNumber"
              clearable
              placeholder="Tenant contact number"
              type="text"
              tabindex="5"
            />
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
            <el-input v-model="entityForm.address" clearable placeholder="Tenant address" type="text" tabindex="6" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item prop="pinCode" label="PIN code">
            <el-input v-model.trim="entityForm.pinCode" maxlength="6" clearable placeholder="e.g. 560001" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item prop="gstin" label="GSTIN (optional)">
            <el-input
              :model-value="entityForm.gstin"
              @update:model-value="(v: string) => (entityForm.gstin = toUpperTaxId(v))"
              maxlength="15"
              clearable
              placeholder="e.g. 29ABCPE1234F1Z7"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item prop="pan" label="PAN (optional)">
            <el-input
              :model-value="entityForm.pan"
              @update:model-value="(v: string) => (entityForm.pan = toUpperTaxId(v))"
              maxlength="10"
              clearable
              placeholder="e.g. ABCPE1234F"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="templates" label="Tenant template">
            <el-select v-model="entityTemplates" filterable clearable multiple placeholder="Select tenant templates">
              <el-option v-for="item in templateList" :key="item.id" :label="item.name" :value="item.id">
                <span style="float: left">{{ item.name }}</span>
                <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">{{
                  showStringOverflow(item.description)
                }}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="expired" label="Expiry time">
            <el-date-picker
              v-model="entityForm.expired"
              clearable
              type="datetime"
              placeholder="Select an expiry time"
              format="DD-MM-YYYY HH:mm:ss"
              value-format="DD-MM-YYYY HH:mm:ss"
              :shortcuts="futureShortcuts"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <template v-else-if="active === 1">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="adminUsername" label="Username">
            <el-input v-model.trim="entityForm.adminUsername" placeholder="Admin username" type="text" tabindex="1">
              <template #append>
                <el-button :icon="Refresh" @click="refreshUsername" />
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="adminPwd" label="Password">
            <el-input v-model.trim="entityForm.adminPwd" placeholder="Admin password" type="text" tabindex="2" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="adminEmail" label="Email">
            <el-input v-model.trim="entityForm.adminEmail" placeholder="Admin email" type="text" tabindex="3" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="adminMobile" label="Mobile number">
            <el-input v-model.trim="entityForm.adminMobile" placeholder="Admin mobile number" type="text" tabindex="4" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="adminNickname" label="Nickname">
            <el-input v-model.trim="entityForm.adminNickname" placeholder="Admin nickname" type="text" tabindex="5" />
          </el-form-item>
        </el-col>
        <el-col :span="12" />
      </el-row>
    </template>
    <template v-else>
      <el-form-item prop="adminNickname" label-width="170px" label="Admin nickname">
        {{ adminResult.adminNickname }}
      </el-form-item>
      <el-form-item prop="adminUsername" label-width="170px" label="Sign-in username">
        {{ adminResult.adminUsername }}
      </el-form-item>
      <el-form-item prop="adminPwd" label-width="170px" label="Sign-in password">
        {{ adminResult.adminPwd }}
      </el-form-item>
      <el-form-item prop="adminEmail" label-width="170px" label="Admin email">
        {{ adminResult.adminEmail }}
      </el-form-item>
      <el-form-item prop="adminMobile" label-width="170px" label="Admin mobile number">
        {{ adminResult.adminMobile }}
      </el-form-item>
    </template>
  </el-form>
  <br />
  <el-button-group :style="{ float: 'right' }">
    <template v-if="active === 0">
      <el-button type="primary" @click="next">
        Next<el-icon class="el-icon--right"><ArrowRight /></el-icon>
      </el-button>
    </template>
    <template v-if="active === 1">
      <el-button type="info" :icon="ArrowLeft" @click="previous" v-loading="loading">Back</el-button>
      <el-button type="primary" @click="next" v-preventReClick v-loading="loading">
        OK <el-icon class="el-icon--right"><Check /></el-icon>
      </el-button>
    </template>
    <template v-if="active === 2">
      <el-button type="primary" @click="close">
        Close<el-icon class="el-icon--right"><Close /></el-icon>
      </el-button>
    </template>
  </el-button-group>
  <br />
  <br />
</template>

<script setup lang="ts">
import { ElMessage, FormInstance, FormRules } from "element-plus"
import { ref, computed, toRefs } from "vue"
import { ISysTemplate, ICreateTenant, ICreateTenantAdmin } from "@/types/sys"
import { futureShortcuts } from "@/utils"
import { saveTenantApi } from "@/api/tenant"
import { Refresh, ArrowLeft, ArrowRight, Check, Close } from "@element-plus/icons-vue"
import ImageUpload from "@/components/ImageUpload/index.vue"
import SearchIndiaArea from "@/components/SearchIndiaArea/index.vue"
import { v4 as uuidv4 } from "uuid"
import { isEmail, isTelphone } from "@/utils/validate"
import { showStringOverflow } from "@/hooks/useIndex"
import { toUpperTaxId, useIndianBusinessRules } from "@/hooks/useIndianBusinessRules"

// Generate a random tenant ID
const randomTenantId = () => {
  return uuidv4().split("-")[4]
}

/** Load */
const loading = ref(false)

const active = ref(0)

const props = defineProps({
  templateList: {
    type: Array<ISysTemplate>,
    required: true
  }
})

const { templateList } = toRefs(props)

// Type-based
const emit = defineEmits<{
  (e: "close"): void
}>()

/// Form data
const entityForm = ref<ICreateTenant>({
  tenantId: randomTenantId(),
  logo: "",
  name: "",
  email: "",
  linkman: "",
  contactNumber: "",
  areaCode: 0,
  address: "",
  pinCode: "",
  gstin: "",
  pan: "",
  templates: [],
  expired: "",
  adminNickname: "",
  adminUsername: "",
  adminMobile: "",
  adminEmail: "",
  adminPwd: ""
})

/// Form data
const adminResult = ref<ICreateTenantAdmin>({
  adminNickname: "",
  adminUsername: "",
  adminMobile: "",
  adminEmail: "",
  adminPwd: ""
})

const entityFormRef = ref<FormInstance | null>(null)

const tenantIdValidator = (rule: any, value: any, callback: any) => {
  if (!/^\w{4,12}$/.test(value)) {
    callback(new Error("Letters and numbers only, 4 to 12 characters!"))
  } else {
    callback()
  }
}

const emailValidator = (rule: any, value: any, callback: any) => {
  if (!isEmail(value)) {
    callback(new Error("Invalid email address!"))
  } else {
    callback()
  }
}

const telphoneValidator = (rule: any, value: any, callback: any) => {
  if (!isTelphone(value)) {
    callback(new Error("Invalid mobile or phone number!"))
  } else {
    callback()
  }
}

const businessRules = useIndianBusinessRules(() => entityForm.value)

/// Form validation rules
const entityFormRules: FormRules = {
  tenantId: [
    { required: true, message: "Enter a tenant ID", trigger: "blur" },
    { validator: tenantIdValidator, trigger: "blur" }
  ],
  logo: [{ required: true, message: "Upload a tenant Logo", trigger: "blur" }],
  name: [{ required: true, message: "Enter a name", trigger: "blur" }],
  email: [
    { required: true, message: "Enter an email", trigger: "blur" },
    { validator: emailValidator, trigger: "blur" }
  ],
  linkman: [{ required: true, message: "Enter a contact person", trigger: "blur" }],
  contactNumber: [
    { required: true, message: "Enter contact details", trigger: "blur" },
    { validator: telphoneValidator, trigger: "blur" }
  ],
  areaCode: [{ required: true, message: "Select an area", trigger: "blur" }],
  address: [{ required: true, message: "Enter an address", trigger: "blur" }],
  ...businessRules,
  status: [{ required: true, message: "Enter a status", trigger: "blur" }],
  templates: [{ required: true, message: "Select a tenant template", trigger: "blur" }]
}

const entityTemplates = computed({
  get() {
    if (entityForm.value.templates) {
      return entityForm.value.templates.map((t) => t.id)
    } else {
      return []
    }
  },
  set(data: number[]) {
    entityForm.value.templates = data.map((id) => {
      return {
        id: id,
        name: "",
        description: ""
      }
    })
  }
})

// Reset entity fields
const resetEntity = () => {
  active.value = 0
  entityForm.value = {
    tenantId: randomTenantId(),
    logo: "",
    name: "",
    email: "",
    linkman: "",
    contactNumber: "",
    areaCode: 0,
    address: "",
    pinCode: "",
    gstin: "",
    pan: "",
    templates: [],
    expired: "",
    adminNickname: "",
    adminUsername: "",
    adminMobile: "",
    adminEmail: "",
    adminPwd: ""
  }
  adminResult.value = {
    adminNickname: "",
    adminUsername: "",
    adminMobile: "",
    adminEmail: "",
    adminPwd: ""
  }
}

const getLogoSrc = (data: string) => {
  entityForm.value.logo = data
}

const refreshTenantId = () => {
  entityForm.value.tenantId = randomTenantId()
}

const refreshUsername = () => {
  entityForm.value.adminUsername = randomTenantId()
}

const next = () => {
  if (active.value === 0) {
    entityFormRef.value?.validate((valid: boolean) => {
      if (valid) {
        active.value++
        return
      }
    })
  }
  if (active.value === 1) {
    entityFormRef.value?.validate((valid: boolean) => {
      if (valid) {
        loading.value = true
        saveTenantApi(entityForm.value)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: "Tenant added",
                type: "success"
              })
              adminResult.value = resp.data
            }
          })
          .catch((err) => {
            console.log("err :>> ", err)
          })
          .finally(() => {
            loading.value = false
            active.value++
          })
      }
    })
  }
}

const previous = () => {
  if (active.value > 0) {
    active.value--
  }
}

const close = () => {
  resetEntity()
  emit("close")
}
</script>

<style lang="scss" scoped></style>
