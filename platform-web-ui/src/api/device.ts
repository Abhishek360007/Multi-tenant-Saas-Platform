import { ISearchQuery } from "@/types/base"
import { ICmsDevice } from "@/types/cms"
import { httpDelete, httpGet, httpPost, httpPut } from "@/utils/service"

/** Query by ID */
export const getDeviceApi = (id: number) => {
  return httpGet<number, IJsonResult<ICmsDevice>>(`/cms/device/${id}`)
}

/** Paged query of all */
export const queryDeviceApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ICmsDevice>>("/cms/device/query", data)
}

/** Query all */
export const listDeviceApi = () => {
  return httpGet<any, IJsonResult<ICmsDevice[]>>("/cms/device/list")
}

/** Add */
export const saveDeviceApi = (data: ICmsDevice) => {
  return httpPost<ICmsDevice, IJsonResult<ICmsDevice>>("/cms/device", data)
}

/** Edit */
export const updateDeviceApi = (data: ICmsDevice) => {
  return httpPut<any, IJsonResult<boolean>>("/cms/device", data)
}

/** Delete */
export const deleteDeviceApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/cms/device/${id}`)
}
