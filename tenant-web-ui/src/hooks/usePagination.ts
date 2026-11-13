import {reactive} from "vue"

interface DefaultPaginationData {
  total: number
  currentPage: number
  pageSizes: number[]
  pageSize: number
  layout: string
}

interface PaginationData {
  total?: number
  currentPage?: number
  pageSizes?: number[]
  pageSize?: number
  layout?: string
  keywords?: string
}

/** Default pagination parameters */
const defaultPaginationData: DefaultPaginationData = {
  total: 0,
  currentPage: 1,
  pageSizes: [10, 20, 50],
  pageSize: 10,
  layout: "total, sizes, prev, pager, next, jumper"
}

export function usePagination(fn: Function, initialPaginationData: PaginationData = {}) {
  /** Merge pagination parameters */
  const paginationData = reactive({ ...defaultPaginationData, ...initialPaginationData })

  /** Change the current page */
  const handleCurrentChange = (value: number) => {
    paginationData.currentPage = value
    fn()
  }

  /** Change the page size */
  const handleSizeChange = (value: number) => {
    paginationData.pageSize = value
    fn()
  }

  return { paginationData, handleCurrentChange, handleSizeChange }
}
