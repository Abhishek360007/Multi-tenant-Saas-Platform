import { IBaseEntity } from "./base"

/** Tenant template */
export interface ISysTemplate extends IBaseEntity {
  /**
   * Unique id
   */
  id: number
  /**
   * Name
   */
  name: string
  /**
   * Description
   */
  description: string
  /**
   * Menu ID
   */
  menuIds?: number[]
}

/** Tenant */
export interface ISysTenant extends IBaseEntity {
  /**
   * Tenant id
   */
  tenantId: string
  /**
   * Tenant logo
   */
  logo: string
  /**
   * Tenant name
   */
  name: string
  /**
   * Tenant email
   */
  email: string
  /**
   * Contact name
   */
  linkman: string
  /**
   * Contact number
   */
  contactNumber: string
  /**
   * Address area
   */
  areaCode: number
  /**
   * Address
   */
  address: string
  /** 6-digit PIN code */
  pinCode: string
  /** GSTIN (optional) */
  gstin?: string
  /** PAN (optional) */
  pan?: string
  /**
   * Status [0 = disabled, 1 = normal]
   */
  status: number
  /**
   * Expiry time (default 100 years)
   */
  expired: string
  /**
   * Tenant template
   */
  templates: ISysTemplate[]
}

/** Create tenant */
export interface ICreateTenantAdmin {
  /**
   * Admin nickname
   */
  adminNickname: string
  /**
   * Admin username
   */
  adminUsername: string
  /**
   * Admin mobile number
   */
  adminMobile: string
  /**
   * Admin email
   */
  adminEmail: string
  /**
   * Admin password
   */
  adminPwd: string
}

/** Create tenant */
export interface ICreateTenant extends ICreateTenantAdmin {
  /**
   * Tenant id
   */
  tenantId: string
  /**
   * Tenant logo
   */
  logo: string
  /**
   * Tenant name
   */
  name: string
  /**
   * Tenant email
   */
  email: string
  /**
   * Contact name
   */
  linkman: string
  /**
   * Contact number
   */
  contactNumber: string
  /**
   * Address area
   */
  areaCode: number
  /**
   * Address
   */
  address: string
  /** 6-digit PIN code */
  pinCode: string
  /** GSTIN (optional) */
  gstin?: string
  /** PAN (optional) */
  pan?: string
  /**
   * Tenant template
   */
  templates: ISysTemplate[]
  /**
   * Expiry time (default 100 years)
   */
  expired: string
}

/** System settings */
export interface ISysConfig extends IBaseEntity {
  /**
   * Unique id
   */
  configId: number
  /**
   * Parameter name
   */
  configName: string
  /**
   * Parameter key
   */
  configKey: string
  /**
   * Parameter value
   */
  configValue: string
  /**
   * Built in (1 = yes, 0 = no)
   */
  configType: number
}

/** Server information */
export interface IServerInfo {
  /**
   * CPU Related information
   */
  cpu: Cpu
  /**
   * Memory details
   */
  mem: Mem
  /**
   * JVM Related information
   */
  jvm: Jvm
  /**
   * Server details
   */
  sys: Sys
  /**
   * Disk details
   */
  sysFiles: SysFile[]
}

/** Server CPU */
export interface Cpu {
  /**
   * Cores
   */
  cpuNum: number
  /**
   * CPU Total usage
   */
  total: number
  /**
   * CPU System usage
   */
  sys: number
  /**
   * CPU User usage
   */
  used: number
  /**
   * CPU Current I/O wait
   */
  wait: number
  /**
   * CPU Current idle
   */
  free: number
}

/** Server JVM */
export interface Jvm {
  /**
   * Total memory held by the JVM (M)
   */
  total: number
  /**
   * JVM Maximum available memory (M)
   */
  max: number
  /**
   * JVM Free memory (M)
   */
  free: number
  /**
   * JVM Memory used (M)
   */
  used: number
  /**
   * JVM Memory used (M)
   */
  usage: number
  /**
   * JDK Version
   */
  version: string
  /**
   * JDK Path
   */
  home: string
  /**
   * Java Name
   */
  name: string
  /**
   * Started at
   */
  startTime: string
  /**
   * Uptime
   */
  runTime: string
  /**
   * Runtime arguments
   */
  inputArgs: string
}

/** Server memory */
export interface Mem {
  /**
   * Total memory
   */
  total: number
  /**
   * Used memory
   */
  used: number
  /**
   * Free memory
   */
  free: number
  /**
   * Usage
   */
  usage: number
}

/** Server system */
export interface Sys {
  /**
   * Server name
   */
  computerName: string
  /**
   * Server Ip
   */
  computerIp: string
  /**
   * Project path
   */
  userDir: string
  /**
   * Operating system
   */
  osName: string
  /**
   * Architecture
   */
  osArch: string
}

/** Server file systems */
export interface SysFile {
  /**
   * Mount path
   */
  dirName: string
  /**
   * File system type
   */
  sysTypeName: string
  /**
   * File type
   */
  typeName: string
  /**
   * Total size
   */
  total: string
  /**
   * Free space
   */
  free: string
  /**
   * Used
   */
  used: string
  /**
   * Resource usage
   */
  usage: number
}
