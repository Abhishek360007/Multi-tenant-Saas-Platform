import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"

dayjs.extend(utc)

/**
 * The API stores and returns date-times in UTC as "YYYY-MM-DD HH:mm:ss" (no zone suffix).
 * These helpers convert such strings between UTC and the browser's local time zone,
 * so pages can keep displaying and submitting plain local strings.
 * Date-only values ("YYYY-MM-DD") are left untouched.
 */
const DATE_TIME = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/
const PATTERN = "YYYY-MM-DD HH:mm:ss"

type Convert = (value: string) => string

const utcToLocal: Convert = (v) => dayjs.utc(v).local().format(PATTERN)
const localToUtc: Convert = (v) => dayjs(v).utc().format(PATTERN)

function walk<T>(value: T, convert: Convert): T {
  if (typeof value === "string") {
    return (DATE_TIME.test(value) ? convert(value) : value) as T
  }
  if (Array.isArray(value)) {
    return value.map((item) => walk(item, convert)) as T
  }
  if (value && typeof value === "object" && Object.getPrototypeOf(value) === Object.prototype) {
    const out: Record<string, unknown> = {}
    for (const [k, v] of Object.entries(value as Record<string, unknown>)) out[k] = walk(v, convert)
    return out as T
  }
  return value
}

/** Converts every UTC date-time string inside an API response to local time. */
export const responseTimesToLocal = <T>(value: T): T => walk(value, utcToLocal)

/** Converts every local date-time string inside a request body/params to UTC. */
export const requestTimesToUtc = <T>(value: T): T => walk(value, localToUtc)
