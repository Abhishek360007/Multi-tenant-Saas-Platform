import {ISearchQuery} from "@/types/base"
import {ISysMenu} from "@/types/pms"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

/** Query by ID */
export const getMenuApi = (id: number) => {
  return httpGet<number, IJsonResult<ISysMenu>>(`/sys/menu/${id}`)
}

/** Paged query of all */
export const queryMenuApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ISysMenu[]>>("/sys/menu/query", data)
}

/** Query all */
export const listMenuApi = () => {
  return httpGet<any, IJsonResult<ISysMenu[]>>("/sys/menu/list")
}

/** Query tree nodes */
export const treeMenuApi = () => {
  return httpGet<any, IJsonResult<ISysMenu[]>>("/sys/menu/tree")
}

/** Add */
export const saveMenuApi = (data: ISysMenu) => {
  return httpPost<ISysMenu, IJsonResult<ISysMenu>>("/sys/menu", data)
}

/** Edit */
export const updateMenuApi = (data: ISysMenu) => {
  return httpPut<any, IJsonResult<boolean>>("/sys/menu", data)
}

/** Delete */
export const deleteMenuApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/sys/menu/${id}`)
}
