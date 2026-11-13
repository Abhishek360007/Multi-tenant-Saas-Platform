import {type App} from "vue"
import ElementPlus from "element-plus"

export function loadElementPlus(app: App) {
  /** Element Plus Import all components */
  app.use(ElementPlus)
}
