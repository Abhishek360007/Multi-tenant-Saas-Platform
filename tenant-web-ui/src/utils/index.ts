import dayjs from "dayjs"
import { removeConfigLayout } from "@/utils/cache/local-storage"
import indiaAreaJson from "@/assets/IndiaArea.json"
import { ISimpleProject } from "@/types/base"
import { CascaderOption } from "element-plus"

//#region Format date and time
export const DEFAULT_DATE_TIME_PATTERN = "YYYY-MM-DD HH:mm:ss"

export const DEFAULT_DATE_PATTERN = "YYYY-MM-DD"

export const DEFAULT_TIME_PATTERN = "HH:mm:ss"

/** Format date and time */
export const formatDateTime = (time: string | number | Date) => {
  return time ? dayjs(new Date(time)).format(DEFAULT_DATE_TIME_PATTERN) : "N/A"
}

/** Format date */
export const formatDate = (time: string | number | Date) => {
  return time ? dayjs(new Date(time)).format(DEFAULT_DATE_PATTERN) : "N/A"
}

/** Format time */
export const formatTime = (time: string | number | Date) => {
  return time ? dayjs(new Date(time)).format(DEFAULT_TIME_PATTERN) : "N/A"
}

/** Time difference */
export const diffDateTime = (time1: string | number | Date, time2: string | number | Date) => {
  if (!time1) {
    return "N/A"
  }
  if (!time2) {
    return "N/A"
  }
  const diff = dayjs(new Date(time1)).diff(new Date(time2), "days")
  return diff
}

/**
 * @param {number} time
 */
export function timeAgo(time: string | number | Date) {
  const limit = dayjs(Date.now()).diff(dayjs(new Date(time)), "second")
  let content = ""
  if (limit < 60) {
    content = "just now"
  } else if (limit >= 60 && limit < 3600) {
    content = Math.floor(limit / 60) + " minutes ago"
  } else if (limit >= 3600 && limit < 86400) {
    content = Math.floor(limit / 3600) + " hours ago"
  } else if (limit >= 86400 && limit < 2592000) {
    content = Math.floor(limit / 86400) + " days ago"
  } else if (limit >= 2592000 && limit < 31104000) {
    content = Math.floor(limit / 2592000) + " months ago"
  } else {
    content = dayjs(time).format(DEFAULT_DATE_PATTERN)
  }
  return content
}

export function expiredDateAgo(time: string | number | Date) {
  const data = dayjs(time, DEFAULT_DATE_TIME_PATTERN)
  const now = dayjs()
  if (data.diff(now, "year") > 0) {
    return `${data.diff(now, "year")} years from now`
  } else if (data.diff(now, "month") > 0) {
    return `${data.diff(now, "month")} months from now`
  } else if (data.diff(now, "day") > 0) {
    return `${data.diff(now, "day")} days from now`
  } else if (data.isBefore(now)) {
    return "Expired"
  } else {
    return data.format("MM-DD HH:mm")
  }
}

//#endregion

/** Read a global CSS variable from JS */
export const getCssVariableValue = (cssVariableName: string) => {
  let cssVariableValue = ""
  try {
    // Returns an empty string if not found
    cssVariableValue = getComputedStyle(document.documentElement).getPropertyValue(cssVariableName)
  } catch (error) {
    console.error(error)
  }
  return cssVariableValue
}

/** Set a global CSS variable from JS */
export const setCssVariableValue = (cssVariableName: string, cssVariableValue: string) => {
  try {
    document.documentElement.style.setProperty(cssVariableName, cssVariableValue)
  } catch (error) {
    console.error(error)
  }
}

/** Reset project settings */
export const resetConfigLayout = () => {
  removeConfigLayout()
  location.reload()
}

/**
 * Convert the string to hashCode
 * @param {string} str
 * @returns {number}
 */
export const hashCode = (str: string) => {
  let hash = 0
  if (str.length == 0) {
    return hash
  }
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i)
    hash = (hash << 5) - hash + char
    hash = hash & hash
  }
  return hash
}

/**
 * @param {string} url
 * @returns {Object}
 */
export const getQueryObject = (url: string) => {
  url = url == null ? window.location.href : url
  const search = url.substring(url.lastIndexOf("?") + 1)
  const obj: any = {}
  const reg = /([^?&=]+)=([^?&=]*)/g
  search.replace(reg, (rs, $1, $2) => {
    const name = decodeURIComponent($1)
    const val = String(decodeURIComponent($2))
    obj[name] = val
    return rs
  })
  return obj
}

/** Get the HTTP header prefix */
export const getEnvBaseURLPrefix = () => {
  return import.meta.env.MODE === "development" ? "/api/v1" : getEnvBaseURL()
}

/** Get the default api url */
export const getEnvBaseURL = () => {
  return import.meta.env.VITE_BASE_API
}

/** Get the area name */
export const indiaAreaDeepQuery = (areaCode: number) => {
  let isGet = false
  let node = { areaCode: 0, mergerName: "" }
  const deepSearch = (tree: any, id: any) => {
    for (let i = 0; i < tree.length; i++) {
      const temp = tree[i]
      if (temp.children && temp.children.length > 0) {
        deepSearch(temp.children, id)
      }
      if (id === temp.areaCode || isGet) {
        isGet || (node = { areaCode: temp.areaCode, mergerName: temp.mergerName })
        isGet = true
        break
      }
    }
  }
  deepSearch(indiaAreaJson, areaCode)
  return node
}

/** Flatten the permission tree into an array for lookups */
export const flatTreeToCascaderOption = (arr: any[], { value = "id", label = "label", disabled = "disabled", children = "children" }) => {
  const result: CascaderOption[] = []
  const deep = (arr1: any[], arr2: CascaderOption[]) => {
    arr1.forEach((item: any) => {
      const temp: CascaderOption = {
        value: item[value],
        label: item[label],
        disabled: item[disabled]
      }
      arr2.push(temp)
      if (item[children] && item[children].length > 0) {
        temp.children = []
        deep(item[children], temp.children)
      }
    })
  }
  deep(arr, result)
  return result
}

// Future time
export const futureShortcuts = [
  {
    text: "In 1 week",
    value: dayjs().add(1, "week").toDate()
  },
  {
    text: "In 1 month",
    value: dayjs().add(1, "month").toDate()
  },
  {
    text: "In 3 months",
    value: dayjs().add(3, "month").toDate()
  },
  {
    text: "In 6 months",
    value: dayjs().add(6, "month").toDate()
  },
  {
    text: "In 1 year",
    value: dayjs().add(1, "year").toDate()
  },
  {
    text: "In 3 years",
    value: dayjs().add(3, "year").toDate()
  }
]

// Past time
export const pastShortcuts = [
  {
    text: "1 week ago",
    value: dayjs().subtract(1, "week").toDate()
  },
  {
    text: "1 month ago",
    value: dayjs().subtract(1, "month").toDate()
  },
  {
    text: "3 months ago",
    value: dayjs().subtract(3, "month").toDate()
  },
  {
    text: "6 months ago",
    value: dayjs().subtract(6, "month").toDate()
  },
  {
    text: "1 year ago",
    value: dayjs().subtract(1, "year").toDate()
  },
  {
    text: "3 years ago",
    value: dayjs().subtract(3, "year").toDate()
  }
]

// Set the default project
export const defaultProject: ISimpleProject = {
  projectId: 10001,
  projectName: "Default project",
  linkman: "Default project",
  areaCode: 29052505542,
  address: "Default project"
}
