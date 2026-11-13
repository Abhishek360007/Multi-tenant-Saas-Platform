import {computed} from "vue"
import {indiaAreaDeepQuery, expiredDateAgo, timeAgo} from "@/utils"

// Earlier time
const showTimeAgo = computed(() => {
  return (value: string) => timeAgo(value)
})

// Future time
const showExpiredDateAgo = computed(() => {
  return (value: string) => expiredDateAgo(value)
})

// Truncate a string
const showStringOverflow = computed(() => {
  return (value: string, len = 20) => {
    return value.length <= len ? value : value.substring(0, len)
  }
})

// Show the India area name
const showIndiaArea = computed(() => {
  return (value: number) => {
    const area = indiaAreaDeepQuery(value)
    if (area) {
      return area.mergerName
    }
    return ""
  }
})

export { showTimeAgo, showExpiredDateAgo, showStringOverflow, showIndiaArea }
