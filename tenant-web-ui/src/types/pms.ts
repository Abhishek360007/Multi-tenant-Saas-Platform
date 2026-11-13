import {IBaseEntity, ISimpleTenant} from "./base"

/** User login */
export interface ILoginData {
  /** Tenant ID */
  tenantId: string
  /** Username */
  username: string
  /** Password */
  password: string
  /** Captcha */
  codeText: string
  /** Captcha key */
  codeKey: string
  /** Remember me */
  rememberMe: boolean
}

/** Signed-in user details */
export interface ILoginUserInfo {
  tenantId: string
  loginId: string
  grantType: string
  userId: number
  mobile: string
  email: string
  nickname: string
  avatar: string
  username: string
  status: number
  gender: number
  adminFlag: number
  userType: number
  loginTime: string
  roles: IUserRole[]
  /** 1 = must change password before using the app */
  passwordChangeRequired?: number
}

/** Signed-in user roles */
export interface IUserRole {
  /* Role ID */
  roleId: number
  /* Role name */
  roleName: string
  /* Role description */
  description: string
}

/** Authentication token */
export interface IOAuth2Token {
  /** `token` */
  access_token: string
  /** Prefix `Bearer ` */
  token_type: string
  /** needed when calling the endpoint that refreshes the `accessToken``token` */
  refresh_token: string
  /* Authorization response */
  scope: string
  /** `accessToken`expiry time in seconds */
  expires_in: number
}

// System menu
export interface ISysMenu extends IBaseEntity {
  /*Menu ID*/
  menuId: number
  /*Parent menu*/
  parentId: number
  /*Menu name*/
  menuName: string
  /*Permission key*/
  perms: string
  /*Component*/
  component: string
  /*Menu icon*/
  icon: string
  /*Sort order*/
  sort: number
  /*Route path*/
  path: string
  /*Target users: 0 = tenant users, 1 = platform users, 2 = all*/
  target: number
  /*Menu type (0 = menu, 1 = button)*/
  menuType: number
  /*Menu visibility (1 = shown, 0 = hidden)*/
  visible: number
  /*Keep alive (1 = yes, 0 = no)*/
  keepAlive: number
  /*Submenu*/
  children: ISysMenu[]
}

/** User permissions */
export interface IUserPerms {
  /** Button */
  buttons: IPermsButton[]
  /** Menu */
  menus: IPermsMenus[]
}

/** Button permissions */
export interface IPermsButton {
  /** `Identifier` */
  perms: string
  /** Title */
  title: string
}

/** Menu permissions */
export interface IPermsMenus {
  /* Menu ID */
  id: number
  /* Parent ID */
  parentId: number
  /* Weight */
  weight: number
  /* Name */
  name: string
  /* Property */
  meta: IPermsMenusMeta
  /* Path */
  path: string
  /* vue.js Component */
  component: string
  /* Submenu */
  children: IPermsMenus[]
}

/** Menu properties */
export interface IPermsMenusMeta {
  /* Icon */
  icon: string
  /* Title */
  title: string
  /** Hidden */
  hidden: boolean
  /** Keep alive */
  keepAlive: boolean
}

/** Platform user */
export interface IPmsUser extends IBaseEntity {
  /**
   * Unique user id
   */
  userId: number
  /**
   * Avatar
   */
  avatar: string
  /**
   * Username
   */
  username: string
  /**
   * Mobile number
   */
  mobile: string
  /**
   * Email
   */
  email: string
  /**
   * Nickname
   */
  nickname: string
  /**
   * Password
   */
  password: string
  /**
   * Gender [0 = female, 1 = male, 2 = unknown]
   */
  gender: number
  /**
   * Is admin: 0 = regular user, 1 = admin
   */
  adminFlag: number
  /**
   * Status [0 = disabled, 1 = normal]
   */
  status: number
  /**
   * Department
   */
  deptId: number
  /**
   * Expiry time (empty = never expires)
   */
  expired: string
  /**
   * Department
   */
  dept: IPmsDept | null
  /**
   * Roles
   */
  roles: IPmsRole[]
  /**
   * Empty means not signed in.
   */
  loginId: string
}

/** System role */
export interface IPmsRole extends IBaseEntity {
  /**
   * Unique user id
   */
  roleId: number
  /**
   * Role name
   */
  roleName: string
  /**
   * Sort order
   */
  sort: number
  /**
   * Data permissions
   */
  dataScope: number
  /**
   * Role description
   */
  description: string
  /**
   * Department ID
   */
  deptIds: Array<number>
}

/** System department */
export interface IPmsDept extends IBaseEntity {
  /**
   * Department ID
   */
  deptId: number
  /**
   * Parent department
   */
  parentId: number
  /**
   * Department name
   */
  deptName: string
  /**
   * Department leader name
   */
  leader: string
  /**
   * Department leader mobile
   */
  leaderMobile: string
  /**
   * Sort order
   */
  sort: number
  /**
   * Department description
   */
  description: string
  /**
   * Ancestor list
   */
  ancestors: string
  /**
   * Submenu
   */
  children: IPmsDept[]
}

/** Tenants linked to the user */
export interface IUserListTenant {
  /**
   * Assigned tenants ID
   */
  have: string[]
  /**
   * All tenants
   */
  all: ISimpleTenant[]
}
