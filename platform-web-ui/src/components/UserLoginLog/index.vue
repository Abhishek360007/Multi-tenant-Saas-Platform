<template>
  <el-form v-loading="loading" :model="entityForm" label-position="right" label-width="80px">
    <el-form-item label="Sign-in location">
      {{ entityForm.address }}
    </el-form-item>
    <el-form-item label="Sign-in device">
      {{ entityForm.osName }}
      {{ entityForm.browser }}
    </el-form-item>
    <el-form-item label="Request IP">
      {{ entityForm.requestIp }}
    </el-form-item>
    <el-form-item label="Signed in at">
      {{ entityForm.createTime }}
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts" name="UserLoginLog">
import { onMounted, watch, ref } from "vue"
import { getLoginLogApi } from "@/api/loginLog"
import { ILoginLog } from "@/types/lms"

const props = defineProps({
  value: {
    type: String,
    required: true
  }
})

const loading = ref(false)

/// Form data
const entityForm = ref<ILoginLog>({
  /* ID */
  id: "",
  /*Grant type*/
  grantType: "",
  /*User ID*/
  userId: 0,
  /*User nickname*/
  nickname: "",
  /*Username*/
  username: "",
  /*Avatar*/
  avatar: "",
  /*Request URL*/
  requestUrl: "",
  /*Request IP*/
  requestIp: "",
  /*Request URL*/
  address: "",
  /*Operating system*/
  osName: "",
  /*Browser*/
  browser: "",
  /*Created at*/
  createTime: ""
})

const getLoginLogData = (loginId: String) => {
  loading.value = true
  getLoginLogApi(loginId)
    .then((resp) => {
      entityForm.value = resp.data
    })
    .catch((error) => {
      console.log("error :>> ", error)
    })
    .finally(() => {
      loading.value = false
    })
}

watch(props, (newValue, _) => {
  console.log("New value: ", newValue.value)
  getLoginLogData(newValue.value)
})

onMounted(() => {
  getLoginLogData(props.value)
})
</script>

<style lang="scss" scoped></style>
