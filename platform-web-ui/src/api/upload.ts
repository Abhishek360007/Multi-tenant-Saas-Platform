import { httpDelete } from "@/utils/service"

// Delete the image from OSS
export const deleteFile = (params: string) => {
  return httpDelete<any, IJsonResult<boolean>>("/upload", { fileUrl: params })
}
