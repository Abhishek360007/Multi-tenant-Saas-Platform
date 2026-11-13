import {ISearchQuery} from "@/types/base"
import {httpDelete, httpGet, httpPost, httpPut} from "@/utils/service"
import {IDictData, IDictSearchQuery, IDictType, IMapDictData} from "@/types/dict"

/** [Map] Query all */
export const mapKeysApi = () => {
  return httpGet<string, IJsonResult<Map<string, IMapDictData[]>>>("/dict/data/mapKeys")
}

/** Query by ID */
export const getDictTypeApi = (id: number) => {
  return httpGet<number, IJsonResult<IDictType>>(`/dict/type/${id}`)
}

/** Paged query of all */
export const queryDictTypeApi = (data: ISearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<IDictType>>("/dict/type/query", data)
}

/** Query all */
export const listDictTypeApi = () => {
  return httpGet<any, IJsonResult<IDictType[]>>("/dict/type/list")
}

/** Add */
export const saveDictTypeApi = (data: IDictType) => {
  return httpPost<IDictType, IJsonResult<IDictType>>("/dict/type", data)
}

/** Edit */
export const updateDictTypeApi = (data: IDictType) => {
  return httpPut<any, IJsonResult<boolean>>("/dict/type", data)
}

/** Delete */
export const deleteDictTypeApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/dict/type/${id}`)
}

/// ----------------------  Dictionary data --------------------------------

/** Paged query of all */
export const queryDictDataApi = (data: IDictSearchQuery) => {
  return httpGet<ISearchQuery, IPageResult<IDictData>>("/dict/data/query", data)
}

/** Add */
export const saveDictDataApi = (data: IDictData) => {
  return httpPost<IDictData, IJsonResult<IDictData>>("/dict/data", data)
}

/** Edit */
export const updateDictDataApi = (data: IDictData) => {
  return httpPut<any, IJsonResult<boolean>>("/dict/data", data)
}

/** Delete */
export const deleteDictDataApi = (id: number) => {
  return httpDelete<number, IJsonResult<boolean>>(`/dict/data/${id}`)
}
