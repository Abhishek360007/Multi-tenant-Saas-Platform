<template>
  <div>
    <el-form ref="searchFormRef" :inline="true">
      <el-form-item>
        <el-button type="primary" link :icon="Sort" @click="switchExpandAndCollapse">{{
          expandAndCollapse ? "Collapse" : "Expand"
          }}</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" link :icon="checkAll ? 'CloseBold' : 'Select'" @click="switchCheckAll">{{
          checkAll ? "Clear" : "Select all"
          }}</el-button>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="checkStrictly" label="Cascade parent and child" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterMenuText" clearable placeholder="Filter by name" />
      </el-form-item>
    </el-form>
    <br>
    <div :style="{ height: '300px', overflowY: 'scroll' }">
      <el-tree ref="treeRef" :data="props.data" :check-strictly="!checkStrictly" :default-expand-all="expandAndCollapse"
        :filter-node-method="filterNode" show-checkbox node-key="id" highlight-current
        :props="{ children: 'children', label: 'name', }" />
    </div>
  </div>
</template>

<script setup lang='ts'>
import { ITreeNode } from '@/types/base';
import { Sort } from "@element-plus/icons-vue"
import { ElTree } from 'element-plus';
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
  data: {
    type: Array<ITreeNode>,
    required: true
  }
})

const filterMenuText = ref("")

// Expand
const expandAndCollapse = ref(true)

// Select all
const checkAll = ref(true)

// Cascade parent and child
const checkStrictly = ref(false)

const treeRef = ref<InstanceType<typeof ElTree>>()

// Get all menus ID
const getMenuIdAll = () => {
  const result: number[] = []
  const deep = (arr1: ITreeNode[], arr2: number[]) => {
    arr1.forEach((item: ITreeNode) => {
      arr2.push(item.id)
      if (item.children && item.children.length > 0) {
        deep(item.children, arr2)
      }
    })
  }
  deep(props.data, result)
  return result
}

// Expand and collapse
const switchExpandAndCollapse = () => {
  expandAndCollapse.value = !expandAndCollapse.value
  props.data.forEach((v) => {
    treeRef.value!.store.nodesMap[v.id].expanded = expandAndCollapse.value
  })
}

// Select all / clear all
const switchCheckAll = () => {
  checkAll.value = !checkAll.value
  if (checkAll.value) {
    const result: number[] = getMenuIdAll()
    treeRef.value!.setCheckedKeys(result, false)
  } else {
    treeRef.value!.setCheckedKeys([], false)
  }
}

watch(filterMenuText, (val) => {
  treeRef.value!.filter(val)
})

const filterNode = (value: string, data: any) => {
  if (!value) return true
  return data.name.includes(value)
}

// Set tree menu data
const setCheckedKeys = (value: number[]) => {
  checkAll.value = value.length > 0
  treeRef.value!.setCheckedKeys(value, false)
  expandAndCollapse.value = true
}

// Get tree menu data
const getCheckedKeys = (): number[] => {
  return treeRef.value!.getCheckedKeys(false) as number[]
}

defineExpose({
  setCheckedKeys,
  getCheckedKeys
});

</script>

<style lang='scss' scoped></style>
