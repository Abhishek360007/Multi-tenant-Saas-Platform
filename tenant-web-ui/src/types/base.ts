// Paged search
export interface ISearchQuery {
  // Includes operator details
  includeCauu?: boolean
  // Current page number
  pageIndex: number
  // Items per page
  pageSize: number
  // Keyword
  keywords: string
  // Start date YYYY-MM-DD
  startDate?: string
  // End date YYYY-MM-DD
  endDate?: string
  // Sort direction, true: ascending, false: descending
  asc?: boolean
  // Field to sort by, e.g.: createUser,lastUpdateTime
  orderByColumns?: string[]
}

// Simple tenant
export interface ISimpleTenant {
  /** Tenant ID */
  tenantId: string
  /** Logo */
  logo: string
  /** Tenant name */
  name: string
  /** Contact name */
  linkman: string
}

// Simple project
export interface ISimpleProject {
  /** Project ID */
  projectId: number
  /** Project name */
  projectName: string
  /** Contact name */
  linkman: string
  /** Address area */
  areaCode: number
  /** Address */
  address: string
}

// Base type
export interface IBaseEntity {
  /** Created at */
  createTime?: string
  /** Created by id */
  createUser?: number
  /** Creator details */
  createUserEntity?: IOperateUserEntity
  /** Last updated at */
  lastUpdateTime?: string
  /** Last updated by id */
  lastUpdateUser?: number
  /** Last updater details */
  lastUpdateUserEntity?: IOperateUserEntity
}

// Operator details
export interface IOperateUserEntity {
  /** Operator ID */
  userId: number
  /** Operator avatar */
  avatar: string
  /** Operator nickname */
  nickname: string
}

// Simple menu
export interface ITreeNode {
  /** Menu ID */
  id: number
  /** Menu name */
  name: string
  /** Parent */
  parentId: number
  /** Sort order */
  weight: number
  /** Submenu */
  children: ITreeNode[]
}

// Edit menu
export interface IUpdateMenus {
  /** Assigned menus ID */
  have: number[]
  /** All menus */
  all: ITreeNode[]
}

// Push message
export interface IPushMessage {
  /** ID */
  id: number
  /** Title */
  title: string
  /** Message */
  content: string
  /** Created at */
  createTime: string
  /** Status: 0 = failed, 1 = succeeded  */
  status: number
}
