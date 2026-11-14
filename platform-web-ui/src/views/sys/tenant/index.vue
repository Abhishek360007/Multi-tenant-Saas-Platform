<template>
  <div class="app-container" v-loading="loading">
    <el-card shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="params">
        <el-form-item>
          <el-input v-model="params.keywords" clearable placeholder="Search by tenant name" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchTableData">Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" :icon="Plus" @click="handleAdd()">Add</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar-wrapper">
        <el-table :data="tableData">
          <el-table-column prop="tenantId" label="Tenant ID" min-width="110" />
          <el-table-column prop="logo" label="Logo" min-width="70">
            <template #default="scope">
              <el-avatar :size="40" :icon="UserFilled" :src="scope.row.logo" />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="Name" min-width="190" />
          <el-table-column prop="email" label="Email" min-width="230" />
          <el-table-column prop="linkman" label="Contact person" min-width="150" />
          <el-table-column prop="contactNumber" label="Contact details" min-width="140" />
          <el-table-column prop="areaCode" label="Area" min-width="230">
            <template #default="scope">
              {{ showIndiaArea(scope.row.areaCode) }}
            </template>
          </el-table-column>
          <el-table-column prop="address" label="Address" min-width="220" />
          <el-table-column prop="pinCode" label="PIN" width="90" />
          <el-table-column prop="gstin" label="GSTIN" width="170" />
          <el-table-column prop="planCode" label="Plan" width="90">
            <template #default="scope">
              <el-tag>{{ planName(scope.row.planCode) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="templates" label="Permission templates" min-width="180">
            <template #default="scope">
              <el-tooltip
                v-for="(item, index) in scope.row.templates"
                :key="index"
                effect="dark"
                :content="item.description"
                placement="top-start"
              >
                <el-tag>{{ item.name }}</el-tag>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status" min-width="110">
            <template #default="scope">
              <template v-if="scope.row.status === 9">
                <el-text class="mx-1" type="warning">Initializing...</el-text>
                <el-icon class="is-loading">
                  <Loading />
                </el-icon>
              </template>
              <span v-else>
                {{ dictStore.getNormalDisable(scope.row.status) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="expired" label="Expiry time" min-width="130">
            <template #default="scope">
              <el-tooltip v-if="scope.row.expired" :content="scope.row.expired" placement="top">
                <el-link> {{ showExpiredDateAgo(scope.row.expired) }}</el-link>
              </el-tooltip>
              <span v-else>Never expires</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="Created at" min-width="190">
            <template #default="scope">
              <operateUser :dateTime="scope.row.createTime" :entity="scope.row.createUserEntity" />
            </template>
          </el-table-column>
          <el-table-column prop="lastUpdateTime" label="Updated at" min-width="190">
            <template #default="scope">
              <operateUser :dateTime="scope.row.lastUpdateTime" :entity="scope.row.lastUpdateUserEntity" />
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="130">
            <template #default="scope">
              <el-link :icon="Edit" type="warning" @click="handleEdit(scope.row)">Edit</el-link>
              &nbsp;
              <!-- v-admin Only admins can delete tenants and reset data -->
              <el-dropdown
                @command="(cmd: string) => handleCommand(cmd, scope.row)"
                v-if="!isDefaultTenantId(scope.row.tenantId)"
              >
                <span class="el-dropdown-link">
                  More
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :icon="Link" command="Jump">Open tenant portal</el-dropdown-item>
                    <el-dropdown-item command="QRcode">
                      <template #default><SvgIcon name="qrcode" />&nbsp;Generate QR code</template>
                    </el-dropdown-item>
                    <div v-admin>
                      <el-dropdown-item :icon="Refresh" command="ResetData">Reset data</el-dropdown-item>
                    </div>
                    <div v-admin>
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

    <!-- QR code dialog -->
    <el-dialog v-model="qrCodeDialog.visible" :title="qrCodeDialog.title" width="500px">
      <div style="text-align: center">
        <el-link type="primary" :href="qrCodeDialog.link" target="_blank">
          {{ qrCodeDialog.link }}
        </el-link>
        <br />
        <br />
        <QRCodeVue3
          :key="qrCodeDialog.tenantId"
          :width="300"
          :value="qrCodeDialog.link"
          :imageOptions="{ hideBackgroundDots: true, imageSize: 0.4, margin: 0 }"
          :image="qrCodeDialog.logo"
        />
      </div>
    </el-dialog>

    <!-- Add/edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="60%" :close-on-click-modal="false">
      <template v-if="saveFlag">
        <create-tenant :template-list="templateList!" @close="closeCreateTenant" />
      </template>

      <template v-if="!saveFlag">
        <el-form
          ref="entityFormRef"
          :model="entityForm"
          :rules="entityFormRules"
          label-position="right"
          label-width="80px"
        >
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
                  disabled
                  type="text"
                  tabindex="1"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="name" label="Name">
                <el-input v-model.trim="entityForm.name" clearable placeholder="Tenant name" type="text" tabindex="2" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item prop="email" label="Email">
                <el-input v-model.trim="entityForm.email" clearable placeholder="Tenant email" type="text" tabindex="3" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="status" label="Status">
                <select-dict-data v-model:value="entityForm.status" dictTypeKey="sys_normal_disable" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item prop="linkman" label="Contact person">
                <el-input
                  v-model.trim="entityForm.linkman"
                  clearable
                  placeholder="Tenant contact"
                  type="text"
                  tabindex="4"
                />
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
                <el-input
                  v-model.trim="entityForm.address"
                  clearable
                  placeholder="Tenant address"
                  type="text"
                  tabindex="6"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item prop="planCode" label="Plan">
                <el-select v-model="entityForm.planCode" placeholder="Plan">
                  <el-option v-for="p in plans" :key="p.planCode" :label="p.name" :value="p.planCode" />
                </el-select>
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
                  tabindex="7"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </template>

      <template v-if="!saveFlag" #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" v-preventReClick @click="handleUpdate">OK</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from "vue"
import { Plus, Edit, Delete, Search, UserFilled, Loading, Link, Refresh } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox, FormInstance, FormRules } from "element-plus"
import { queryTenantApi, updateTenantApi, deleteTenantApi, resetDataTenantApi } from "@/api/tenant"
import { listTemplateApi } from "@/api/template"
import { ISysTenant, ISysTemplate } from "@/types/sys"
import ImageUpload from "@/components/ImageUpload/index.vue"
import SearchIndiaArea from "@/components/SearchIndiaArea/index.vue"
import createTenant from "./create.vue"
import { useDictStoreHook } from "@/store/modules/dict"
import { cloneDeep } from "lodash-es"
import { showExpiredDateAgo, showStringOverflow, showIndiaArea, isDefaultTenantId } from "@/hooks/useIndex"
import { toUpperTaxId, useIndianBusinessRules } from "@/hooks/useIndianBusinessRules"
import { listPlansApi, type IPlan } from "@/api/plan"

/** Subscription plans, for the Plan column and selector */
const plans = ref<IPlan[]>([])
listPlansApi().then((r) => (plans.value = r.data))
const planName = (code: string) => plans.value.find((p) => p.planCode === code)?.name ?? code
import { isEmail, isTelphone } from "@/utils/validate"
import { futureShortcuts } from "@/utils"
import { useNoticeStoreHook } from "@/store/modules/notice"
import QRCodeVue3 from "qrcode-vue3"
import { getFileBlob } from "@/utils/service"

const dictStore = useDictStoreHook()

const noticeStore = useNoticeStoreHook()

// Watch
watch(
  () => noticeStore.createTenantNotice,
  (newValue, _) => {
    ElMessageBox.confirm(newValue!.content, "Reload data??", {
      confirmButtonText: "OK",
      cancelButtonText: "Cancel",
      type: "warning"
    })
      .then(() => getTableData())
      .catch(() => console.log())
  }
)

/** Load */
const loading = ref(false)

// true : add, false: edit
const saveFlag = ref(false)

const dialogVisible = ref(false)

const dialogTitle = ref("")

const tableData = ref<ISysTenant[]>()

const templateList = ref<ISysTemplate[]>()

const params = reactive({
  pageTotal: 0,
  pageIndex: 1,
  pageSize: 10,
  keywords: "",
  includeCauu: true
})

/// Form data
const entityForm = ref<ISysTenant>({
  tenantId: "",
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
  status: 0,
  expired: "",
  templates: []
})

const entityFormRef = ref<FormInstance | null>(null)

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
  tenantId: [{ required: true, message: "Enter a tenant ID", trigger: "blur" }],
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

// Fetch template data
const getListTemplateData = () => {
  loading.value = true
  listTemplateApi()
    .then((resp) => {
      templateList.value = resp.data
    })
    .catch((err) => {
      console.log("err :>> ", err)
    })
    .finally(() => {
      loading.value = false
    })
}

// Fetch data
const getTableData = () => {
  loading.value = true
  queryTenantApi(params)
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
    tenantId: "",
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
    status: 0,
    expired: "",
    templates: []
  }
}

const getLogoSrc = (data: string) => {
  entityForm.value.logo = data
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

// Edit
const handleEdit = (data: ISysTenant) => {
  resetEntity()
  entityForm.value = cloneDeep(data)
  dialogTitle.value = "Edit"
  saveFlag.value = false
  dialogVisible.value = true
}

// Add
const handleAdd = () => {
  resetEntity()
  dialogTitle.value = "Add"
  saveFlag.value = true
  dialogVisible.value = true
}

const handleCommand = (command: string, data: ISysTenant) => {
  switch (command) {
    case "ResetData":
      handleResetData(data)
      break
    case "Delete":
      handleDelete(data)
      break
    case "Jump":
      window.open(genTenantLink(data.tenantId), "_blank")
      break
    case "QRcode":
      getFileBlob(data.logo)
        .then((blob) => {
          const reader = new FileReader()
          reader.readAsDataURL(blob)
          reader.onload = () => {
            qrCodeDialog.value.title = `Scan to open the ${data.name} tenant portal`
            qrCodeDialog.value.link = genTenantLink(data.tenantId)
            qrCodeDialog.value.tenantId = data.tenantId
            const base64 = reader.result as string
            qrCodeDialog.value.logo = base64
            qrCodeDialog.value.visible = true
          }
          reader.onerror = (err) => console.log("err :>> ", err)
        })
        .catch((err) => {
          console.log("QRcode error :>> ", err)
        })
      break
    default:
      console.log("command :>> ", command)
      break
  }
}

const qrCodeDialog = ref({
  visible: false,
  title: "",
  tenantId: "",
  link: "",
  logo: ""
})

// Link to a tenant's portal login. VITE_TENANT_LINK is either a full URL of the portal
// (e.g. https://tenanthub-portal.onrender.com/), which gets ?tenantId=..., or a domain for
// tenant subdomains (e.g. example.com/tenant/ -> http://acme.example.com/tenant/)
const genTenantLink = (tenantId: string) => {
  const link: string = import.meta.env.VITE_TENANT_LINK
  if (/^https?:\/\//.test(link)) {
    return `${link.replace(/\/?$/, "/")}#/login?tenantId=${encodeURIComponent(tenantId)}`
  }
  return `http://${tenantId}.${link}`
}

// Delete
const handleDelete = (data: ISysTenant) => {
  ElMessageBox.prompt(`Enter the tenant ID below`, `Delete ${data.name}?`, {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    inputPattern: /^\w{4,12}$/,
    inputErrorMessage: "Tenant ID: 4 to 12 letters and numbers",
    type: "warning"
  })
    .then((value) => {
      if (value.value === data.tenantId) {
        deleteTenantApi(data.tenantId)
          .then((resp) => {
            if (resp.data) {
              ElMessage({
                message: `Deleted ${data.name}`,
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
      } else {
        ElMessage({
          message: `Tenant ID does not match`,
          type: "error"
        })
      }
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
}

// Reset data
const handleResetData = (data: ISysTenant) => {
  ElMessageBox.prompt(`Enter the tenant ID below`, `Reset the data of ${data.name}?`, {
    confirmButtonText: "OK",
    cancelButtonText: "Cancel",
    inputPattern: /^\w{4,12}$/,
    inputErrorMessage: "Tenant ID: 4 to 12 letters and numbers",
    type: "warning"
  })
    .then((value) => {
      if (value.value === data.tenantId) {
        resetDataTenantApi(data.tenantId)
          .then((resp) => {
            if (resp.data) {
              // The reset recreates the tenant admin with a one-time password: show it once
              ElMessageBox.alert(
                `Username: ${resp.data.adminUsername}<br/>Password: ${resp.data.adminPwd}<br/><br/>` +
                  "Copy these now. They are not shown again, and the admin must change the password at first sign-in.",
                `Tenant ${data.name} has been reset`,
                { dangerouslyUseHTMLString: true, confirmButtonText: "Done", type: "success" }
              )
            }
          })
          .catch((err) => {
            console.log("err :>> ", err)
          })
          .finally(() => {
            getTableData()
          })
      } else {
        ElMessage({
          message: `Tenant ID does not match`,
          type: "error"
        })
      }
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
}

// Edit
const handleUpdate = () => {
  entityFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      updateTenantApi(entityForm.value)
        .then((resp) => {
          if (resp.data) {
            ElMessage({
              message: "Tenant updated",
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
      return false
    }
  })
}

// Add
const closeCreateTenant = () => {
  dialogVisible.value = false
  getTableData()
}

onMounted(() => {
  getTableData()
  getListTemplateData()
})
</script>

<style lang="scss" scoped>
.el-tag {
  margin: 2px;
}
</style>
