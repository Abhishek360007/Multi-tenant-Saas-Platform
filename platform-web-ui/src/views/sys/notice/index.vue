<template>
  <div>
    <h1>Notifications</h1>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
const data1 = ref({})
console.log("data1 :>> ", data1)
onMounted(() => {
  console.log("1.-Runs after the component is mounted")
})
</script>

<style lang="scss" scoped></style>
