import {ISearchQuery, ISimpleProject} from "@/types/base"
import {ICmsProject} from "@/types/cms"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"

/** Query by ID */
export const getProjectApi = (id: number) => {
  return httpGet<number, IJsonResult<ICmsProject>>(`/cms/project/${id}`)
}

// Simple query
export const simpleQueryProjectApi = (params: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ISimpleProject>>("/cms/project/simple/query", params)
}

/** Paged query of all */
export const queryProjectApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<ICmsProject>>("/cms/project/query", data)
}

/** Query all */
export const listProjectApi = () => {
  return httpGet<any, IJsonResult<ICmsProject[]>>("/cms/project/list")
}

/** Add */
export const saveProjectApi = (data: ICmsProject) => {
  return httpPost<ICmsProject, IJsonResult<ICmsProject>>("/cms/project", data)
}

/** Edit */
export const updateProjectApi = (data: ICmsProject) => {
  return httpPut<any, IJsonResult<boolean>>("/cms/project", data)
}

/** Delete */
export const deleteProjectApi = (id: number, password: string) => {
  const params = {
    projectId: id,
    password: password
  }
  return httpDelete<any, IJsonResult<boolean>>("/cms/project", params)
}

/** Reset password */
export const resetPasswordApi = (data: any) => {
  return httpPost<any, IJsonResult<boolean>>("/cms/project/reset/password", data)
}
