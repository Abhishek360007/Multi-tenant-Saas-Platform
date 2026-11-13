import {getConfigLayout} from "@/utils/cache/local-storage"

/** Project settings type */
export interface LayoutSettings {
  /** Visible Settings Panel */
  showSettings: boolean
  /** Layout */
  layoutMode: "left" | "top" | "left-top"
  /** Show tab bar */
  showTagsView: boolean
  /** Visible Logo */
  showLogo: boolean
  /** Fixed Header */
  fixedHeader: boolean
  /** Show footer */
  showFooter: boolean
  /** Show notifications */
  showNotify: boolean
  /** Show theme switch */
  showThemeSwitch: boolean
  /** Show full-screen button */
  showScreenfull: boolean
  /** Show search button */
  showSearchMenu: boolean
  /** Cache tab bar */
  cacheTagsView: boolean
  /** Enable watermark */
  showWatermark: boolean
  /** Grayscale mode */
  showGreyMode: boolean
  /** Color-blind mode */
  showColorWeakness: boolean
  /** Show tenant search */
  showSearchTenant: boolean
  /** Show component size */
  showControlSize: boolean
}

/** Default settings */
const defaultSettings: LayoutSettings = {
  layoutMode: "left",
  showSettings: true,
  showTagsView: true,
  fixedHeader: true,
  showFooter: true,
  showLogo: true,
  showNotify: true,
  showThemeSwitch: true,
  showScreenfull: true,
  showSearchMenu: true,
  cacheTagsView: false,
  showWatermark: false,
  showGreyMode: false,
  showColorWeakness: false,
  showSearchTenant: true,
  showControlSize: true
}

/** Project settings */
export const layoutSettings: LayoutSettings = { ...defaultSettings, ...getConfigLayout() }
