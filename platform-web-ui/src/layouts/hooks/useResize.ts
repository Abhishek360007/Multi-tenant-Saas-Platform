import {onBeforeMount, onBeforeUnmount, onMounted} from "vue"
import {useAppStore} from "@/store/modules/app"
import {useRouteListener} from "@/hooks/useRouteListener"
import {DeviceEnum} from "@/constants/app-key"

/** Following Bootstrap responsive breakpoints, the maximum mobile width is 992 */
const MAX_MOBILE_WIDTH = 992

/** Switch layout when the browser width changes */
export default () => {
  const appStore = useAppStore()
  const { listenerRouteChange } = useRouteListener()

  /** Whether the current device is mobile */
  const _isMobile = () => {
    const rect = document.body.getBoundingClientRect()
    return rect.width - 1 < MAX_MOBILE_WIDTH
  }

  /** Handle window resize */
  const _resizeHandler = () => {
    if (!document.hidden) {
      const isMobile = _isMobile()
      appStore.toggleDevice(isMobile ? DeviceEnum.Mobile : DeviceEnum.Desktop)
      isMobile && appStore.closeSidebar(true)
    }
  }
  /** Adjust the layout to the device type on route change */
  listenerRouteChange(() => {
    if (appStore.device === DeviceEnum.Mobile && appStore.sidebar.opened) {
      appStore.closeSidebar(false)
    }
  })

  /** Add the window resize listener before mount */
  onBeforeMount(() => {
    window.addEventListener("resize", _resizeHandler)
  })

  /** Detect the device type from the window size after mount and adjust the layout */
  onMounted(() => {
    if (_isMobile()) {
      appStore.toggleDevice(DeviceEnum.Mobile)
      appStore.closeSidebar(true)
    }
  })

  /** Remove the window resize listener before unmount */
  onBeforeUnmount(() => {
    window.removeEventListener("resize", _resizeHandler)
  })
}
