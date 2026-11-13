/** Device type */
export enum DeviceEnum {
  Mobile,
  Desktop
}

/** Sidebar open state */
export const SIDEBAR_OPENED = "opened"
/** Sidebar closed state */
export const SIDEBAR_CLOSED = "closed"

export type SidebarOpened = typeof SIDEBAR_OPENED
export type SidebarClosed = typeof SIDEBAR_CLOSED
