import { IBaseEntity, ISearchQuery } from "./base"

// Paged dictionary search
export interface IDictSearchQuery extends ISearchQuery {
  // Dictionary type
  dictTypeKey: string
}

/** Dictionary type table */
export interface IDictType extends IBaseEntity {
  /** Dictionary type ID */
  typeId: number
  /** Dictionary type name */
  typeName: string
  /** Dictionary type key */
  typeKey: string
  /** Dictionary description */
  description: string
}

/** Dictionary data table */
export interface IDictData extends IBaseEntity {
  /** Dictionary type ID */
  dictCode: number
  /** Dictionary sort order */
  dictSort: number
  /** Dictionary label */
  dictLabel: string
  /** Dictionary value */
  dictValue: string
  /** Dictionary type key */
  typeKey: string
  /** Is default (1 = yes, 0 = no) */
  isDefault: number
}

/** Dictionary data table */
export interface IMapDictData extends IBaseEntity {
  /** Dictionary sort order */
  dictSort: number
  /** Dictionary label */
  dictLabel: string
  /** Dictionary value */
  dictValue: Object
  /** Default */
  isDefault: boolean
}
