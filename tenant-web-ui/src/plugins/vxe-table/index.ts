import {type App} from "vue"
// https://vxetable.cn/#/table/start/install
import VXETable from "vxe-table"
// https://github.com/x-extends/vxe-table-plugin-element
import VXETablePluginElement from "vxe-table-plugin-element"
import XEUtils from "xe-utils"
import enUS from "vxe-table/es/locale/lang/en-US"

VXETable.use(VXETablePluginElement)

/** Global default parameters */
VXETable.setup({
  /** English UI text (vxe-table defaults to Chinese) */
  i18n: (key: string, args?: any) => XEUtils.toFormatString(XEUtils.get(enUS, key), args),
  /** Global size */
  size: "medium",
  /** Global starting zIndex; raise it if the project uses large z-index values so dialogs are not hidden */
  zIndex: 9999,
  /** Version number; used by features that cache data. Bump it to reset that data */
  version: 0,
  /** Global loading text; null hides the text */
  loadingText: null,
  table: {
    showHeader: true,
    showOverflow: "tooltip",
    showHeaderOverflow: "tooltip",
    autoResize: true,
    // stripe: false,
    border: "inner",
    // round: false,
    emptyText: "No data",
    rowConfig: {
      isHover: true,
      isCurrent: true
    },
    columnConfig: {
      resizable: false
    },
    align: "center",
    headerAlign: "center",
    /** Row primary key field name */
    rowId: "_VXE_ID"
  },
  pager: {
    // size: "medium",
    /** Matching styles */
    perfect: false,
    pageSize: 10,
    pagerCount: 7,
    pageSizes: [10, 20, 50],
    layouts: ["Total", "PrevJump", "PrevPage", "Number", "NextPage", "NextJump", "Sizes", "FullJump"]
  },
  modal: {
    minWidth: 500,
    minHeight: 400,
    lockView: true,
    mask: true,
    // duration: 3000,
    // marginSize: 20,
    dblclickZoom: false,
    showTitleOverflow: true,
    transfer: true,
    draggable: false
  }
})

export function loadVxeTable(app: App) {
  /** Vxe Table Import all components */
  app.use(VXETable)
}
