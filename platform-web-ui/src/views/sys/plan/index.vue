<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue"
import { ElMessage, type FormInstance, type FormRules } from "element-plus"
import { listPlansApi, savePlanApi, type IPlan } from "@/api/plan"
import { formatPaise } from "@/utils/utc-time"

defineOptions({ name: "SysPlan" })

const loading = ref(false)
const plans = ref<IPlan[]>([])

const load = async () => {
  loading.value = true
  try {
    plans.value = (await listPlansApi()).data
  } finally {
    loading.value = false
  }
}
onMounted(load)

const limit = (n: number) => (n < 0 ? "Unlimited" : String(n))

/** The form edits prices in rupees; the API stores paise */
interface PlanForm {
  planCode: string
  name: string
  description: string
  monthlyRupees: number
  yearlyRupees: number
  maxUsers: number
  maxProjects: number
  sort: number
  status: number
}

const dialogVisible = ref(false)
const isNew = ref(true)
const saving = ref(false)
const formRef = ref<FormInstance | null>(null)
const form = reactive<PlanForm>({
  planCode: "",
  name: "",
  description: "",
  monthlyRupees: 0,
  yearlyRupees: 0,
  maxUsers: -1,
  maxProjects: -1,
  sort: 0,
  status: 1
})

const rules: FormRules = {
  planCode: [
    { required: true, message: "Enter a plan code", trigger: "blur" },
    { pattern: /^[a-z0-9_-]{2,20}$/, message: "2-20 lowercase letters, digits, - or _", trigger: "blur" }
  ],
  name: [{ required: true, message: "Enter a name", trigger: "blur" }]
}

const openDialog = (plan?: IPlan) => {
  isNew.value = !plan
  Object.assign(form, {
    planCode: plan?.planCode ?? "",
    name: plan?.name ?? "",
    description: plan?.description ?? "",
    monthlyRupees: (plan?.monthlyPricePaise ?? 0) / 100,
    yearlyRupees: (plan?.yearlyPricePaise ?? 0) / 100,
    maxUsers: plan?.maxUsers ?? -1,
    maxProjects: plan?.maxProjects ?? -1,
    sort: plan?.sort ?? plans.value.length + 1,
    status: plan?.status ?? 1
  })
  dialogVisible.value = true
}

const save = () => {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return
    saving.value = true
    savePlanApi({
      planCode: form.planCode,
      name: form.name,
      description: form.description,
      monthlyPricePaise: Math.round(form.monthlyRupees * 100),
      yearlyPricePaise: Math.round(form.yearlyRupees * 100),
      maxUsers: form.maxUsers,
      maxProjects: form.maxProjects,
      sort: form.sort,
      status: form.status
    })
      .then(() => {
        ElMessage.success("Plan saved")
        dialogVisible.value = false
        load()
      })
      .catch(() => {})
      .finally(() => (saving.value = false))
  })
}
</script>

<template>
  <div class="app-container">
    <el-card shadow="never">
      <div class="toolbar">
        <span class="hint">Prices are before 18% GST. Use -1 for unlimited users or projects.</span>
        <el-button type="primary" @click="openDialog()">Add plan</el-button>
      </div>
      <el-table :data="plans" v-loading="loading">
        <el-table-column prop="name" label="Plan" min-width="120" />
        <el-table-column prop="planCode" label="Code" width="100" />
        <el-table-column label="Monthly" width="130" align="right">
          <template #default="{ row }">{{ formatPaise(row.monthlyPricePaise) }}</template>
        </el-table-column>
        <el-table-column label="Yearly" width="140" align="right">
          <template #default="{ row }">{{ formatPaise(row.yearlyPricePaise) }}</template>
        </el-table-column>
        <el-table-column label="Users" width="100" align="center">
          <template #default="{ row }">{{ limit(row.maxUsers) }}</template>
        </el-table-column>
        <el-table-column label="Projects" width="100" align="center">
          <template #default="{ row }">{{ limit(row.maxProjects) }}</template>
        </el-table-column>
        <el-table-column label="Status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "Offered" : "Hidden" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="Description" min-width="200" />
        <el-table-column label="Actions" width="90" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">Edit</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isNew ? 'Add plan' : 'Edit plan'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="Code" prop="planCode">
          <el-input v-model.trim="form.planCode" :disabled="!isNew" placeholder="e.g. enterprise" />
        </el-form-item>
        <el-form-item label="Name" prop="name">
          <el-input v-model.trim="form.name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model.trim="form.description" />
        </el-form-item>
        <el-form-item label="Monthly price (₹)">
          <el-input-number v-model="form.monthlyRupees" :min="0" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="Yearly price (₹)">
          <el-input-number v-model="form.yearlyRupees" :min="0" :precision="2" :step="1000" />
        </el-form-item>
        <el-form-item label="Max users">
          <el-input-number v-model="form.maxUsers" :min="-1" />
        </el-form-item>
        <el-form-item label="Max projects">
          <el-input-number v-model="form.maxProjects" :min="-1" />
        </el-form-item>
        <el-form-item label="Sort order">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="Offered">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="save">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
  .hint {
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }
}
</style>
