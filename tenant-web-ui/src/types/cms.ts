import {IBaseEntity} from "./base"
import {IPmsDept} from "./pms"

/** Project */
export interface ICmsProject extends IBaseEntity {
  /* Project ID */
  projectId: number
  /*Project name*/
  projectName: string
  /*Contact name*/
  linkman: string
  /*Contact number*/
  contactNumber: string
  /*Address area*/
  areaCode: number
  /*Address, e.g. 12 MG Road, Block A*/
  address: string
  /*6-digit PIN code*/
  pinCode: string
  /*Sort order*/
  sort: number
  /*Project password, required for dangerous operations*/
  password: string
  /*Status [0 = disabled, 1 = normal, 2 = locked]*/
  status: number
  /*Department*/
  deptId: number
  /*Department*/
  dept: IPmsDept | null
}

/** Project */
export interface ICmsDevice extends IBaseEntity {
  /* Device ID */
  deviceId: number
  /*Device name*/
  deviceName: string
  /*Status [0 = disabled, 1 = normal, 2 = locked]*/
  status: number
}
