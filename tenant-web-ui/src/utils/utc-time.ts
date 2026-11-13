import dayjs, { type Dayjs } from "dayjs"
import utc from "dayjs/plugin/utc"
import customParseFormat from "dayjs/plugin/customParseFormat"

dayjs.extend(utc)
dayjs.extend(customParseFormat)

/**
 * Date handling between the API and the UI.
 *
 * - The API stores and returns date-times in UTC as "YYYY-MM-DD HH:mm:ss" and dates as "YYYY-MM-DD".
 * - The UI works with Indian-style strings everywhere: "DD-MM-YYYY HH:mm:ss" (viewer's time zone)
 *   and "DD-MM-YYYY". Date pickers use the same formats.
 *
 * The HTTP interceptors convert at the boundary, so pages can display and submit plain strings.
 */
export const API_DATE_TIME = "YYYY-MM-DD HH:mm:ss"
export const API_DATE = "YYYY-MM-DD"
export const UI_DATE_TIME = "DD-MM-YYYY HH:mm:ss"
export const UI_DATE = "DD-MM-YYYY"

const API_DATE_TIME_RE = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/
const API_DATE_RE = /^\d{4}-\d{2}-\d{2}$/
const UI_DATE_TIME_RE = /^\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}$/
const UI_DATE_RE = /^\d{2}-\d{2}-\d{4}$/

type Convert = (value: string) => string

/** API (UTC) -> UI (local, DD-MM-YYYY) */
const toUi: Convert = (v) => {
  if (API_DATE_TIME_RE.test(v)) return dayjs.utc(v, API_DATE_TIME).local().format(UI_DATE_TIME)
  if (API_DATE_RE.test(v)) return dayjs(v, API_DATE).format(UI_DATE)
  return v
}

/** UI (local, DD-MM-YYYY or YYYY-MM-DD) -> API (UTC) */
const toApi: Convert = (v) => {
  if (UI_DATE_TIME_RE.test(v)) return dayjs(v, UI_DATE_TIME).utc().format(API_DATE_TIME)
  if (API_DATE_TIME_RE.test(v)) return dayjs(v, API_DATE_TIME).utc().format(API_DATE_TIME)
  if (UI_DATE_RE.test(v)) return dayjs(v, UI_DATE).format(API_DATE)
  return v
}

function walk<T>(value: T, convert: Convert): T {
  if (typeof value === "string") return convert(value) as T
  if (Array.isArray(value)) return value.map((item) => walk(item, convert)) as T
  if (value && typeof value === "object" && Object.getPrototypeOf(value) === Object.prototype) {
    const out: Record<string, unknown> = {}
    for (const [k, v] of Object.entries(value as Record<string, unknown>)) out[k] = walk(v, convert)
    return out as T
  }
  return value
}

/** Converts every date/date-time string inside an API response to the UI format. */
export const responseTimesToLocal = <T>(value: T): T => walk(value, toUi)

/** Converts every date/date-time string inside a request body/params to the API format. */
export const requestTimesToUtc = <T>(value: T): T => walk(value, toApi)

/** Parses a UI or API date string (or a Date/timestamp) into a dayjs object in local time. */
export const parseAppDate = (value: string | number | Date): Dayjs => {
  if (typeof value === "string") {
    if (UI_DATE_TIME_RE.test(value)) return dayjs(value, UI_DATE_TIME)
    if (UI_DATE_RE.test(value)) return dayjs(value, UI_DATE)
    if (API_DATE_TIME_RE.test(value)) return dayjs(value, API_DATE_TIME)
    if (API_DATE_RE.test(value)) return dayjs(value, API_DATE)
  }
  return dayjs(value)
}

const INR = new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", minimumFractionDigits: 2 })
const IN_NUMBER = new Intl.NumberFormat("en-IN")

/** ₹1,23,456.00 (Indian lakh/crore digit grouping). Amounts are in rupees. */
export const formatINR = (rupees: number | null | undefined) => (rupees == null ? "N/A" : INR.format(rupees))

/** Formats paise (as stored by payment gateways) as ₹. */
export const formatPaise = (paise: number | null | undefined) => (paise == null ? "N/A" : INR.format(paise / 100))

/** 1,23,45,678 (Indian digit grouping) */
export const formatIndianNumber = (n: number | null | undefined) => (n == null ? "N/A" : IN_NUMBER.format(n))
