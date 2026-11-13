<template>
  <iframe id="swagger" :src="docAction" scrolling="no" frameborder="0" />
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue"
import { getEnvBaseURL } from "@/utils"

const docAction = ref(`${getEnvBaseURL()}/doc.html`)

const changeSwagger = () => {
  const swagger = document.getElementById("swagger")
  const deviceWidth = document.body.clientWidth
  const deviceHeight = document.body.clientHeight
  if (swagger) {
    if (swagger!.style) {
      swagger!.style.width = Number(deviceWidth) - 240 + "px" // The number is the layout width offset
      swagger!.style.height = Number(deviceHeight) - 64 + "px" // The number is the layout height offset
    }
  }
}

onMounted(() => {
  changeSwagger()
  window.onresize = () => {
    changeSwagger()
  }
})
</script>

<style lang="scss" scoped></style>
