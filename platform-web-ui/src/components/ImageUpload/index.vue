<template>
  <div id="imageUpload">
    <el-upload
      class="avatar-uploader"
      :action="getAction"
      :headers="myHeaders"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :before-upload="beforeAvatarUpload"
      accept=".jpg,.jpeg,.png,.gif"
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon">
        <Plus />
      </el-icon>
    </el-upload>
  </div>
</template>

<script setup lang="ts" name="UserAvatar">
import { ref, onMounted } from "vue"
import { ElMessage } from "element-plus"
import { Plus } from "@element-plus/icons-vue"
import type { UploadProps } from "element-plus"
import { deleteFile } from "@/api/upload"
import { getEnvBaseURLPrefix } from "@/utils"
import { getToken, getCurrentTenant } from "@/utils/cache/local-storage"

const props = defineProps({
  src: {
    type: String,
    default: ""
  }
})

// Type-based
const emit = defineEmits<{
  (e: "change", id: string): void
}>()

const imageUrl = ref<string>("")

onMounted(() => {
  imageUrl.value = props.src!
})

// Delete the old image
const deleteOldFile = (url: string) => {
  deleteFile(url).then((result) => console.log("deleteOldFile :", result.data))
}

// Get upload URL
const getAction = `${getEnvBaseURLPrefix()}/upload/image?width=100&height=100`

// Request headers
const myHeaders = {
  Authorization: getToken(),
  "x-tenant-id": getCurrentTenant().tenantId
}

const handleAvatarSuccess: UploadProps["onSuccess"] = (response, _) => {
  if (response.status === 200) {
    const resp = response.data
    // Delete the old avatar
    if (imageUrl.value) {
      deleteOldFile(imageUrl.value)
    }
    // Assign the new avatar
    imageUrl.value = resp.url
    emit("change", imageUrl.value)
  } else {
    ElMessage.error(response.message)
  }
}

const regular = new RegExp("(image/jpg|image/jpeg|image/png|image/gif|)")

const beforeAvatarUpload: UploadProps["beforeUpload"] = (rawFile) => {
  if (!regular.test(rawFile.type)) {
    ElMessage.error("Images must be jpeg, png, jpg or gif")
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error("Image size cannot exceed 2MB")
    return false
  }
  return true
}
</script>

<style lang="scss">
$size: 100px;

#imageUpload {
  .avatar-uploader {
    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
    }

    .el-upload:hover {
      border-color: var(--el-color-primary);
    }
  }

  .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: $size;
    height: $size;
    text-align: center;
  }
}
</style>

<style lang="scss" scoped>
$size: 100px;

.avatar-uploader {
  .avatar {
    width: $size;
    height: $size;
    display: block;
  }
}
</style>
