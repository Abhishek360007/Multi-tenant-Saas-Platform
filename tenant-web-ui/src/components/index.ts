import {type App} from "vue"

// Dictionary data picker
import SelectDictData from "@/components/SelectDictData/index.vue"
import OperateUser from "@/components/OperateUser/index.vue"

export function loadGlobalComponent(app: App) {
  app.component("SelectDictData", SelectDictData)
  app.component("OperateUser", OperateUser)
}
