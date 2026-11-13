import {ISearchQuery, IUpdateMenus} from "@/types/base"
import {ISysTemplate} from "@/types/sys"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

/** Query by ID */
export const getTemplateApi = (id: number) => {
  return httpGet<number, IJsonResult<ISysTemplate>>(`/sys/template/${id}`)
}

/** Paged query of all */
export const queryTemplateApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ISysTemplate>>("/sys/template/query", data)
}

/** Query all */
export const listTemplateApi = () => {
  return httpGet<any, IJsonResult<ISysTemplate[]>>("/sys/template/list")
}

/** Add */
export const saveTemplateApi = (data: ISysTemplate) => {
  return httpPost<ISysTemplate, IJsonResult<ISysTemplate>>("/sys/template", data)
}

/** Edit */
export const updateTemplateApi = (data: ISysTemplate) => {
  return httpPut<any, IJsonResult<boolean>>("/sys/template", data)
}

/** Delete */
export const deleteTemplateApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/sys/template/${id}`)
}

/** Granted permissions */
export const getTemplateMenusApi = (data: number) => {
  return httpGet<ISearchQuery, IJsonResult<IUpdateMenus>>(`/sys/template/menus/${data}`)
}

/** Update granted permissions */
export const updateTemplateMenusApi = (data: any) => {
  return httpPost<any, IJsonResult<Boolean>>("/sys/template/menus", data)
}
