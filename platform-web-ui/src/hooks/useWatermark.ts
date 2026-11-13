import { type Ref, onBeforeUnmount, ref } from "vue"
import { debounce } from "lodash-es"

type Observer = {
  watermarkElMutationObserver?: MutationObserver
  parentElMutationObserver?: MutationObserver
  parentElResizeObserver?: ResizeObserver
}

type DefaultConfig = typeof defaultConfig

/** Default settings */
const defaultConfig = {
  /** Defense (on by default; stops the watermark being removed or hidden, may cost some performance) */
  defense: true,
  /** Text color */
  color: "#c0c4cc",
  /** Text opacity */
  opacity: 0.5,
  /** Font size */
  size: 16,
  /** Font family */
  family: "serif",
  /** Text angle */
  angle: -20,
  /** Width of each watermark tile (larger = sparser) */
  width: 300,
  /** Height of each watermark tile (larger = sparser) */
  height: 200
}

/** body Element */
const bodyEl = ref<HTMLElement>(document.body)

/**
 * Create the watermark
 * 1. Optionally pass the container element for the watermark; defaults to body
 * 2. Defended watermark: resists removal or hiding from the developer console
 */
export function useWatermark(parentEl: Ref<HTMLElement | null> = bodyEl) {
  /** Backup text */
  let backupText: string
  /** Final configuration */
  let mergeConfig: DefaultConfig
  /** Watermark element */
  let watermarkEl: HTMLElement | null = null
  /** Observer */
  const observer: Observer = {
    watermarkElMutationObserver: undefined,
    parentElMutationObserver: undefined,
    parentElResizeObserver: undefined
  }

  /** Set the watermark */
  const setWatermark = (text: string, config: Partial<DefaultConfig> = {}) => {
    if (!parentEl.value) {
      console.warn("Call setWatermark only after the DOM is mounted")
      return
    }
    // Backup text
    backupText = text
    // Merge configuration
    mergeConfig = { ...defaultConfig, ...config }
    // Create or update the watermark element
    watermarkEl ? updateWatermarkEl() : createWatermarkEl()
    // Watch the watermark and container elements
    addElListener(parentEl.value)
  }

  /** Create the watermark element */
  const createWatermarkEl = () => {
    const isBody = parentEl.value!.tagName.toLowerCase() === bodyEl.value.tagName.toLowerCase()
    const watermarkElPosition = isBody ? "fixed" : "absolute"
    const parentElPosition = isBody ? "" : "relative"
    watermarkEl = document.createElement("div")
    watermarkEl.style.pointerEvents = "none"
    watermarkEl.style.top = "0"
    watermarkEl.style.left = "0"
    watermarkEl.style.position = watermarkElPosition
    watermarkEl.style.zIndex = "99999"
    const { clientWidth, clientHeight } = parentEl.value!
    updateWatermarkEl({ width: clientWidth, height: clientHeight })
    // Make the watermark container position relative
    parentEl.value!.style.position = parentElPosition
    // Add the watermark element to its container
    parentEl.value!.appendChild(watermarkEl)
  }

  /** Update the watermark element */
  const updateWatermarkEl = (
    options: Partial<{
      width: number
      height: number
    }> = {}
  ) => {
    if (!watermarkEl) return
    backupText && (watermarkEl.style.background = `url(${createBase64()}) left top repeat`)
    options.width && (watermarkEl.style.width = `${options.width}px`)
    options.height && (watermarkEl.style.height = `${options.height}px`)
  }

  /** Create a base64 image */
  const createBase64 = () => {
    const { color, opacity, size, family, angle, width, height } = mergeConfig
    const canvasEl = document.createElement("canvas")
    canvasEl.width = width
    canvasEl.height = height
    const ctx = canvasEl.getContext("2d")
    if (ctx) {
      ctx.fillStyle = color
      ctx.globalAlpha = opacity
      ctx.font = `${size}px ${family}`
      ctx.rotate((Math.PI / 180) * angle)
      ctx.fillText(backupText, 0, height / 2)
    }
    return canvasEl.toDataURL()
  }

  /** Clear the watermark */
  const clearWatermark = () => {
    if (!parentEl.value || !watermarkEl) return
    // Stop watching the watermark and container elements
    removeListener()
    // Remove the watermark element
    try {
      parentEl.value.removeChild(watermarkEl)
    } catch {
      // e.g. without defense the user removed this element from the console
      console.warn("The watermark element no longer exists; create it again")
    } finally {
      watermarkEl = null
    }
  }

  /** Refresh the watermark (called by the defense) */
  const updateWatermark = debounce(() => {
    clearWatermark()
    createWatermarkEl()
    addElListener(parentEl.value!)
  }, 100)

  /** Watch the watermark and container elements (DOM changes and size changes) */
  const addElListener = (targetNode: HTMLElement) => {
    // Check whether defense is enabled
    if (mergeConfig.defense) {
      // Avoid adding the listener twice
      if (!observer.watermarkElMutationObserver && !observer.parentElMutationObserver) {
        // Watch DOM changes
        addMutationListener(targetNode)
      }
    } else {
      // No mutation observer needed without defense
      removeListener("mutation")
    }
    // Avoid adding the listener twice
    if (!observer.parentElResizeObserver) {
      // Watch DOM size changes
      addResizeListener(targetNode)
    }
  }

  /** Stop watching the watermark and container elements; pass which observer to remove, or remove all by default */
  const removeListener = (kind: "mutation" | "resize" | "all" = "all") => {
    // Remove the mutation observer
    if (kind === "mutation" || kind === "all") {
      observer.watermarkElMutationObserver?.disconnect()
      observer.watermarkElMutationObserver = undefined
      observer.parentElMutationObserver?.disconnect()
      observer.parentElMutationObserver = undefined
    }
    // Remove the resize observer
    if (kind === "resize" || kind === "all") {
      observer.parentElResizeObserver?.disconnect()
      observer.parentElResizeObserver = undefined
    }
  }

  /** Watch DOM changes */
  const addMutationListener = (targetNode: HTMLElement) => {
    // Callback run when a change is observed
    const mutationCallback = debounce((mutationList: MutationRecord[]) => {
      // Watermark defense (stops the user deleting the watermark element or hiding it with CSS)
      mutationList.forEach(
        debounce((mutation: MutationRecord) => {
          switch (mutation.type) {
            case "attributes":
              mutation.target === watermarkEl && updateWatermark()
              break
            case "childList":
              mutation.removedNodes.forEach((item) => {
                item === watermarkEl && targetNode.appendChild(watermarkEl)
              })
              break
          }
        }, 100)
      )
    }, 100)
    // Create an observer instance with the callback
    observer.watermarkElMutationObserver = new MutationObserver(mutationCallback)
    observer.parentElMutationObserver = new MutationObserver(mutationCallback)
    // Start observing the target node with this configuration
    observer.watermarkElMutationObserver.observe(watermarkEl!, {
      // Observe attribute changes on the target node; default true
      attributes: true,
      // Observe added or removed child nodes; default false
      childList: false,
      // Observe all descendants; default false
      subtree: false
    })
    observer.parentElMutationObserver.observe(targetNode, {
      attributes: false,
      childList: true,
      subtree: false
    })
  }

  /** Watch DOM size changes */
  const addResizeListener = (targetNode: HTMLElement) => {
    // Resize the watermark when targetNode changes size
    const resizeCallback = debounce(() => {
      const { clientWidth, clientHeight } = targetNode
      updateWatermarkEl({ width: clientWidth, height: clientHeight })
    }, 500)
    // Create an observer instance with the callback
    observer.parentElResizeObserver = new ResizeObserver(resizeCallback)
    // Start observing the target node
    observer.parentElResizeObserver.observe(targetNode)
  }

  /** Remove the watermark and all observers before unmount */
  onBeforeUnmount(() => {
    clearWatermark()
  })

  return { setWatermark, clearWatermark }
}
