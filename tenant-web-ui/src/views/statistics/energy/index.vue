<template>
  <div><h1>Energy statistics</h1></div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
const data1 = ref({})
onMounted(() => {
  console.log("data1 :>> ", data1.value)
})
</script>

<style lang="scss" scoped></style>
