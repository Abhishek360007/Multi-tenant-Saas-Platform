import {onBeforeUnmount} from "vue"
import mitt, {type Handler} from "mitt"
import {type RouteLocationNormalized} from "vue-router"

/** Callback type */
type Callback = (route: RouteLocationNormalized) => void

const emitter = mitt()
const key = Symbol("ROUTE_CHANGE")
let latestRoute: RouteLocationNormalized

/** Set the latest route and fire the route change event */
export const setRouteChange = (to: RouteLocationNormalized) => {
  // Fire the event
  emitter.emit(key, to)
  // Cache the latest route
  latestRoute = to
}

/** Watching the route separately wastes rendering; use publish/subscribe instead */
export function useRouteListener() {
  /** Callbacks */
  const callbackList: Callback[] = []

  /** Listen for route changes (optionally run immediately) */
  const listenerRouteChange = (callback: Callback, immediate = false) => {
    // Cache the callback
    callbackList.push(callback)
    // Listen for the event
    emitter.on(key, callback as Handler)
    // Optionally run the callback once immediately
    immediate && latestRoute && callback(latestRoute)
  }

  /** Remove the route change listener */
  const removeRouteListener = (callback: Callback) => {
    emitter.off(key, callback as Handler)
  }

  /** Remove the listener before the component is destroyed */
  onBeforeUnmount(() => {
    for (let i = 0; i < callbackList.length; i++) {
      removeRouteListener(callbackList[i])
    }
  })

  return { listenerRouteChange, removeRouteListener }
}
